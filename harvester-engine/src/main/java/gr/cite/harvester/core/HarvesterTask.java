package gr.cite.harvester.core;

import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import gr.cite.harvester.datastore.model.Harvest;
import gr.cite.harvester.datastore.model.Status;
import gr.cite.harvester.datastore.mongodb.HarvesterDatastore;
import gr.cite.harvester.wcs.WCSHarvestable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.earthserver.wcs.adapter.api.WCSAdapterAPI;
import gr.cite.femme.client.FemmeException;

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
		if (harvests.size() > 0) {
			harvest(harvests);
		}
	}

	private void harvest(List<Harvest> harvests) {
		
		ExecutorService executor = Executors.newFixedThreadPool(5);

		for (Harvest harvest : harvests) {

			WCSHarvestable harvestable = new WCSHarvestable();
			harvestable.setHarvest(harvest);
			harvestable.setWcsAdapter(wcsAdapter);
			harvestable.setHarvesterDatastore(harvesterDatastore);
			executor.submit(() -> {
                harvestable.setHarvest(harvesterDatastore.updateHarvestStatus(harvestable.getHarvest().getId(), Status.RUNNING));
                try {

                    //logger.debug("Starting harvest of " + harvestable.getHarvest().getEndpoint());
                    Harvest updatedHarvest = harvestable.harvest();
                    //logger.debug("Finished harvest of " + harvestable.getHarvest().getEndpoint());
					logger.debug("Completed harvest cycle " + updatedHarvest.getCurrentHarvestCycle().getId() + " for endpoint " + updatedHarvest.getEndpoint());
					logger.info("------------------- Harvest Statistics -------------------");
					logger.info("Harvest endpoint: " + updatedHarvest.getEndpoint());
					logger.info("Total elements: " + updatedHarvest.getCurrentHarvestCycle().getTotalElements());
					logger.info("New elements: " + updatedHarvest.getCurrentHarvestCycle().getNewElements());
					logger.info("Updated elements: " + updatedHarvest.getCurrentHarvestCycle().getUpdatedElements());
					logger.info("Failed elements: " + updatedHarvest.getCurrentHarvestCycle().getFailedElements());
					logger.info("----------------------------------------------------------");

					harvesterDatastore.updateHarvestStatus(harvestable.getHarvest().getId(), Status.FINISHED);
                } catch (FemmeException e) {
                    harvesterDatastore.updateHarvestStatus(harvestable.getHarvest().getId(), Status.ERROR, e.getMessage());
					logger.error("Harvest for endpoint " + harvestable.getHarvest().getEndpoint() + " failed", e);
                }
            });
		}

		executor.shutdown();
	}
}
