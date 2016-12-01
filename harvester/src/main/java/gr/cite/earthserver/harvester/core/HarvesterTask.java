package gr.cite.earthserver.harvester.core;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.earthserver.harvester.datastore.model.Harvest;
import gr.cite.earthserver.harvester.datastore.model.Status;
import gr.cite.earthserver.harvester.datastore.mongodb.HarvesterDatastore;
import gr.cite.earthserver.harvester.wcs.WCSHarvestable;
import gr.cite.earthserver.wcs.adapter.WCSAdapter;
import gr.cite.femme.client.FemmeDatastoreException;

public class HarvesterTask implements Runnable {

	private final static Logger logger = LoggerFactory.getLogger(HarvesterTask.class);

	private HarvesterDatastore harvesterDatastore;

	public HarvesterTask(HarvesterDatastore harvesterDatastore){
		this.harvesterDatastore = harvesterDatastore;
	}

	@Override
	public void run() {
//		List<Harvest> harvestList = this.harvesterDatastore.getHarvests();
		List<Harvest> harvestList = this.harvesterDatastore.getHarvestsToBeHarvested();
		try {
			harvest(harvestList);
		} catch (IllegalStateException e){
 			logger.info(e.getMessage(),e);
		} catch (InterruptedException e){
			logger.error(e.getMessage(),e);
		} catch (ExecutionException e){
			logger.error(e.getMessage(),e);
		}
		logger.info("Finished Run");
	}

	private void harvest(List<Harvest> harvestList) throws InterruptedException , ExecutionException {

		ExecutorService executor = Executors.newFixedThreadPool(5);

		for (Harvest harvest : harvestList) {
			
			WCSHarvestable harvestable = new WCSHarvestable();
			harvestable.setHarvest(harvest);
			harvestable.setWcsAdapter(new WCSAdapter("http://localhost:8081/femme-application"));
			executor.submit(new Runnable() {
				
				@Override
				public void run() {
					
					String collectionId;
					String wcsHarvestableStatus = harvestable.getHarvest().getStatus().getStatusCode();
					
					if (Status.PENDING.getStatusCode().equals(wcsHarvestableStatus)) {
						
						harvesterDatastore.updateHarvestStatus(harvestable.getHarvest().getId(), Status.RUNNING);
						try {
							logger.info("Starting Harvest of " + harvestable.getHarvest().getEndpoint());
							collectionId = harvestable.harvest();
							logger.info("Finished Harvest of " + harvestable.getHarvest().getEndpoint());
						} catch (FemmeDatastoreException e) {
							logger.info(e.getMessage(),e);
						}
						harvesterDatastore.updateHarvestStatus(harvestable.getHarvest().getId(), Status.FINISHED);
						
					} else if (Status.FINISHED.getStatusCode().equals(wcsHarvestableStatus) || Status.ERROR.getStatusCode().equals(wcsHarvestableStatus)) {
						
						Duration period = Duration.of(harvest.getSchedule().getPeriod(), harvest.getSchedule().getTimeUnit());
						Instant endtimeOfHarvest = harvest.getEndTime();
						Instant now = Instant.now();
						Duration timePassed = Duration.between(endtimeOfHarvest, now);
						
						if (timePassed.compareTo(period) > 0) {
							harvesterDatastore.updateHarvestStatus(harvestable.getHarvest().getId(), Status.RUNNING);
							try {
								logger.info("Starting Harvest of " + harvestable.getHarvest().getEndpoint());
								collectionId = harvestable.harvest();
								logger.info("Finished Harvest of " + harvestable.getHarvest().getEndpoint());
							} catch (FemmeDatastoreException e) {
								logger.info(e.getMessage(),e);
							}
							harvesterDatastore.updateHarvestStatus(harvestable.getHarvest().getId(), Status.FINISHED);
						}
					}
				}
			});
			logger.info("Out of Runnable");
		}
		executor.shutdown();
	}
}
