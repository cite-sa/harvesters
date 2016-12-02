package gr.cite.earthserver.harvester.wcs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.earthserver.harvester.core.Harvestable;
import gr.cite.earthserver.harvester.datastore.model.Harvest;
import gr.cite.earthserver.wcs.adapter.api.WCSAdapterAPI;
import gr.cite.earthserver.wcs.core.WCSRequestBuilder;
import gr.cite.earthserver.wcs.core.WCSRequestException;
import gr.cite.earthserver.wcs.core.WCSResponse;
import gr.cite.earthserver.wcs.utils.ParseException;
import gr.cite.earthserver.wcs.utils.WCSParseUtils;
import gr.cite.femme.client.FemmeDatastoreException;

public class WCSHarvestable implements Harvestable {
	
	private static final Logger logger = LoggerFactory.getLogger(WCSHarvestable.class);
	
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
	
	/*public WCSAdapter getWcsAdapter() {
		return this.wcsAdapter;
	}*/
	
	/*public WCSHarvestable(String endpoint, Schedule schedule, WCSAdapterAPI wcsAdapter) {
		Harvest harvest = new Harvest();
		harvest.setEndpoint(endpoint);
		harvest.setSchedule(schedule);
		
		this.setHarvest(harvest);
		
		this.wcsAdapter = wcsAdapter;
	}*/
	
	/*public WCSHarvestable(Harvest harvest) {
		this.setHarvest(harvest);
	}*/
	
	public Harvest getHarvest() {
		return this.harvest;
	}
	
	public void setHarvest(Harvest harvest) {
		this.harvest = harvest;
	}
	
	@Override
	public String harvest() throws FemmeDatastoreException {
		
		String collectionId = null;
		
		try {
			WCSRequestBuilder wcsRequestBuilder = new WCSRequestBuilder().endpoint(this.getHarvest().getEndpoint());
			WCSResponse getCapabilities = wcsRequestBuilder.getCapabilities().build().get();
			List<String> coverageIds = WCSParseUtils.getCoverageIds(getCapabilities.getResponse());
			collectionId = this.wcsAdapter.insertServer(this.getHarvest().getEndpoint(), this.getHarvest().getEndpointAlias(), getCapabilities);

			List<Future<String>> futures = new ArrayList<Future<String>>();
			ExecutorService executor = Executors.newFixedThreadPool(4);
			
			for (String coverageId : coverageIds) {
				futures.add(executor.submit(new RetrieveAndStoreCoverageCallable(wcsRequestBuilder, this.wcsAdapter, collectionId, coverageId)));
//				WCSResponse describeCoverage = wcsRequestBuilder.describeCoverage().coverageId(coverageId).build().get();
//				femmeClient.addToCollection(WCSFemmeMapper.fromCoverage(describeCoverage), collectionId);
			}
			
			for(Future<String> future : futures) {
				try {
					String coverageId = future.get();
					logger.info("Coverage " + coverageId + " added to server " + collectionId);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				} catch (ExecutionException e) {
					logger.error(e.getMessage(), e);
				}
			}
			executor.shutdown();
			
		} catch (WCSRequestException e) {
			logger.error(e.getMessage(), e);
		} catch (ParseException e1) {
			logger.error(e1.getMessage(), e1);
		}
		return collectionId;
	}
}
