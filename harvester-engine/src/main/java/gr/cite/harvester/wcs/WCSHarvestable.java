package gr.cite.harvester.wcs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import gr.cite.harvester.datastore.model.HarvestCycle;
import gr.cite.harvester.datastore.mongodb.HarvesterDatastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.harvester.core.Harvestable;
import gr.cite.harvester.datastore.model.Harvest;
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
		String importId;
		String collectionId;
		
		try {
			WCSRequestBuilder wcsRequestBuilder = new WCSRequestBuilder().endpoint(this.harvest.getEndpoint());
			WCSResponse getCapabilities = wcsRequestBuilder.getCapabilities().build().get();
			List<String> coverageIds = WCSParseUtils.getCoverageIds(getCapabilities.getResponse());

			importId = this.wcsAdapter.beginImport(this.harvest.getEndpoint());


			//collectionId = this.wcsAdapter.insertServer(this.harvest.getEndpoint(), this.harvest.getEndpointAlias(), getCapabilities);
			collectionId = this.wcsAdapter.importServer(importId, this.harvest.getEndpoint(), this.harvest.getEndpointAlias(), getCapabilities);

			List<Future<String>> futures = new ArrayList<>();
			ExecutorService executor = Executors.newFixedThreadPool(2);

			logger.info("Total coverages to be inserted: " + coverageIds.size());
			
			for (String coverageId : coverageIds) {
				futures.add(executor.submit(new RetrieveAndStoreCoverageCallable(wcsRequestBuilder, this.wcsAdapter, importId, collectionId, coverageId)));
//				WCSResponse describeCoverage = wcsRequestBuilder.describeCoverage().coverageId(coverageId).build().get();
//				femmeClient.addToCollection(WCSFemmeMapper.fromCoverage(describeCoverage), collectionId);
			}

			HarvestCycle countElementsHarvestCycle = this.harvest.getCurrentHarvestCycle();

			AtomicInteger harvestedElements = new AtomicInteger(0);
			for(Future<String> future : futures) {
				try {
					String coverageId = future.get();
					if (coverageId != null) {
						countElementsHarvestCycle.incrementNewElements();
					} else {
						countElementsHarvestCycle.incrementUpdatedElements();
					}
					logger.info("Coverage " + coverageId + " added to server " + collectionId);
				} catch (InterruptedException | ExecutionException e) {
					countElementsHarvestCycle.incrementFailedElements();
					logger.error(e.getMessage(), e);
				}
				countElementsHarvestCycle.incrementTotalElements();
				harvestedElements.incrementAndGet();

				if (harvestedElements.compareAndSet(50, 0)) {
					this.harvest = this.harvesterDatastore.updateHarvestedCyCle(harvest.getId(), countElementsHarvestCycle);
				}
			}
			this.harvest = this.harvesterDatastore.incrementHarvestedElementsCounters(harvest.getId(), countElementsHarvestCycle);
			this.wcsAdapter.endImport(importId);
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
