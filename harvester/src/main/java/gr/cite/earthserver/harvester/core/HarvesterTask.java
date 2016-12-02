package gr.cite.earthserver.harvester.core;

import java.util.List;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.earthserver.harvester.datastore.model.Harvest;
import gr.cite.earthserver.harvester.datastore.model.Status;
import gr.cite.earthserver.harvester.datastore.mongodb.HarvesterDatastore;
import gr.cite.earthserver.harvester.wcs.WCSHarvestable;
import gr.cite.earthserver.wcs.adapter.api.WCSAdapterAPI;
import gr.cite.femme.client.FemmeDatastoreException;

public class HarvesterTask implements Runnable {

	private final static Logger logger = LoggerFactory.getLogger(HarvesterTask.class);

	private HarvesterDatastore harvesterDatastore;
	
	private WCSAdapterAPI wcsAdapter;

	@Inject
	public HarvesterTask(HarvesterDatastore harvesterDatastore, WCSAdapterAPI wcsAdapter){
		this.harvesterDatastore = harvesterDatastore;
		this.wcsAdapter = wcsAdapter;
	}

	@Override
	public void run() {
		List<Harvest> harvests = this.harvesterDatastore.getHarvestsToBeHarvested();
		try {
			if (harvests.size() > 0) {
				harvest(harvests);
			}
		} catch (InterruptedException e){
			logger.error(e.getMessage(),e);
		} catch (ExecutionException e){
			logger.error(e.getMessage(),e);
		}
	}

	private void harvest(List<Harvest> harvests) throws InterruptedException , ExecutionException {
		
		ExecutorService executor = Executors.newFixedThreadPool(5);

		for (Harvest harvest : harvests) {
			
			WCSHarvestable harvestable = new WCSHarvestable();
			harvestable.setHarvest(harvest);
//			harvestable.setWcsAdapter(new WCSAdapter("http://localhost:8081/femme-application"));
			harvestable.setWcsAdapter(wcsAdapter);
			executor.submit(new Runnable() {
				
				@Override
				public void run() {
					harvesterDatastore.updateHarvestStatus(harvestable.getHarvest().getId(), Status.RUNNING);
					try {
						
						logger.debug("Starting Harvest of " + harvestable.getHarvest().getEndpoint());
						harvestable.harvest();
						logger.debug("Finished Harvest of " + harvestable.getHarvest().getEndpoint());
						
					} catch (FemmeDatastoreException e) {
						logger.error(e.getMessage(),e);
						harvesterDatastore.updateHarvestStatus(harvestable.getHarvest().getId(), Status.ERROR);
						return;
					}
					
					harvesterDatastore.updateHarvestStatus(harvestable.getHarvest().getId(), Status.FINISHED);
				}
			});
		}
		executor.shutdown();
	}
}
