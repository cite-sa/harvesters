package gr.cite.earthserver.harvester.wcs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import gr.cite.earthserver.harvester.datastore.model.HarvestCycle;
import gr.cite.earthserver.harvester.datastore.mongodb.HarvesterDatastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.earthserver.harvester.core.Harvestable;
import gr.cite.earthserver.harvester.datastore.model.Harvest;
import gr.cite.earthserver.wcs.adapter.api.WCSAdapterAPI;
import gr.cite.earthserver.wcs.core.WCSRequestBuilder;
import gr.cite.earthserver.wcs.core.WCSResponse;
import gr.cite.earthserver.wcs.utils.WCSParseUtils;
import gr.cite.femme.client.FemmeDatastoreException;

public class WCSHarvestable implements Harvestable {
	
	private static final Logger logger = LoggerFactory.getLogger(WCSHarvestable.class);

	private HarvesterDatastore harvesterDatastore;

	private Harvest harvest;
	
	private WCSAdapterAPI wcsAdapter;

	/*@Inject
	public WCSHarvestable(WCSAdapterAPI wcsAdapter) {
		this.wcsAdapter = wcsAdapter;
	}*/
	
	@Inject
	public void setWcsAdapter(WCSAdapterAPI wcsAdapter) {
		this.wcsAdapter = wcsAdapter;
	}

	@Override
	public HarvesterDatastore getHarvesterDatastore() {
		return this.harvesterDatastore;
	}

	@Override
	public void setHarvesterDatastore(HarvesterDatastore harvesterDatastore) {
		this.harvesterDatastore = harvesterDatastore;
	}

	public Harvest getHarvest() {
		return this.harvest;
	}
	
	public void setHarvest(Harvest harvest) {
		this.harvest = harvest;
	}
	
	@Override
	public Harvest harvest() throws FemmeDatastoreException {
		
		String collectionId = null;
		
		try {
			WCSRequestBuilder wcsRequestBuilder = new WCSRequestBuilder().endpoint(this.harvest.getEndpoint());
			WCSResponse getCapabilities = wcsRequestBuilder.getCapabilities().build().get();
			List<String> coverageIds = WCSParseUtils.getCoverageIds(getCapabilities.getResponse());
			collectionId = this.wcsAdapter.insertServer(this.harvest.getEndpoint(), this.harvest.getEndpointAlias(), getCapabilities);

			List<Future<String>> futures = new ArrayList<>();
			ExecutorService executor = Executors.newFixedThreadPool(2);
			
			for (String coverageId : coverageIds) {
				futures.add(executor.submit(new RetrieveAndStoreCoverageCallable(wcsRequestBuilder, this.wcsAdapter, collectionId, coverageId)));
//				WCSResponse describeCoverage = wcsRequestBuilder.describeCoverage().coverageId(coverageId).build().get();
//				femmeClient.addToCollection(WCSFemmeMapper.fromCoverage(describeCoverage), collectionId);
			}

			HarvestCycle countElementsHarvestCycle = new HarvestCycle();
			countElementsHarvestCycle.setStartTime(null);

			int harvestedElements = 0;
			for(Future<String> future : futures) {
				try {
					String coverageId = future.get();
					if (coverageId != null) {
						countElementsHarvestCycle.setNewElements(countElementsHarvestCycle.getNewElements() + 1);
					} else {
						countElementsHarvestCycle.setUpdatedElements(countElementsHarvestCycle.getNewElements() + 1);
					}
					logger.info("Coverage " + coverageId + " added to server " + collectionId);
				} catch (InterruptedException | ExecutionException e) {
					countElementsHarvestCycle.setFailedElements(countElementsHarvestCycle.getFailedElements() + 1);
					logger.error(e.getMessage(), e);
				}
				countElementsHarvestCycle.setTotalElements(countElementsHarvestCycle.getTotalElements() + 1);

				if (++harvestedElements == 50) {
					this.harvest = harvesterDatastore.incrementHarvestedElementsCounters(harvest.getId(), countElementsHarvestCycle);
					harvestedElements = 0;
					countElementsHarvestCycle = new HarvestCycle();
					countElementsHarvestCycle.setStartTime(null);
				}
			}
			this.harvest = harvesterDatastore.incrementHarvestedElementsCounters(harvest.getId(), countElementsHarvestCycle);
			executor.shutdown();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new FemmeDatastoreException(e.getMessage(), e);
		}
		/*} catch (WCSRequestException e) {
			logger.error(e.getMessage(), e);

		} catch (ParseException e1) {
			logger.error(e1.getMessage(), e1);
		} catch (ProcessingException e2) {
			logger.error(e2.getMessage(), e2);
		}*/
		return this.harvest;
	}
}
