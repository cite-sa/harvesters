package gr.cite.earthserver.harvester.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.earthserver.harvester.datastore.model.Harvest;
import gr.cite.earthserver.harvester.datastore.model.Schedule;
import gr.cite.earthserver.harvester.datastore.mongodb.HarvesterDatastore;
import gr.cite.earthserver.harvester.datastore.mongodb.HarvesterDatastoreMongo;
import gr.cite.earthserver.harvester.datastore.mongodb.HarvesterDatastoreMongoClient;
import gr.cite.earthserver.harvester.wcs.WCSHarvestable;
import gr.cite.femme.utils.Pair;

public class Harvester {
	
	private static final Logger logger = LoggerFactory.getLogger(Harvester.class);
	
	private HarvesterDatastore harvesterDatastore;

	private Map<Harvestable, ScheduledExecutorService> harvestables;

	@Inject
	public Harvester(HarvesterDatastore harvesterDatastore) {
		this.harvesterDatastore = harvesterDatastore;
		this.harvestables = this.buildHarvestableMap();
	}

	/**
	 * 
	 * @param harvestable
	 *            it is recommended to implement an {@code equals} and
	 *            {@code hash} method in the implementation of
	 *            {@linkplain Harvestable}
	 */
	public void register(Harvestable harvestable) {
		harvesterDatastore.registerHarvest(harvestable.getHarvest());
		
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		//executorService.scheduleAtFixedRate(new HarvestableTask(harvestable), 0, harvestable.getHarvest().getSchedule().getPeriod(), harvestable.getHarvest().getSchedule().getTimeUnit());

		harvestables.put(harvestable, executorService);
		
		logger.info("Endpoint " + harvestable.getHarvest().getEndpoint() + " has been registered successfully");
	}
	
	public void unregister(String endpoint) {
		this.harvesterDatastore.unregisterHarvest(endpoint);
		/*this.unregister(harvesterDatastore.getHarvest(endpoint));*/
	}
	
	public void unregister(Harvestable harvestable) {

		if (harvestables.containsKey(harvestable)) {

			ScheduledExecutorService executorService = harvestables.get(harvestable);

			if (executorService != null) {
				executorService.shutdown();
			}

			harvestables.remove(harvestable);
			
			logger.info("Endpoint " + harvestable.getHarvest().getEndpoint() + " has been unregistered successfully");

		} else {
			logger.warn("harvestable " + harvestable.toString() + " was not found in harvester.");
		}
	}

	public void harvest() {
		logger.info("Harvesting all registered Harvestable sources...");

		List<Future<String>> futures = new ArrayList<Future<String>>();
		ExecutorService executor = Executors.newFixedThreadPool(5);

		for (Harvestable harvestable : harvestables.keySet()) {
			futures.add(executor.submit(new Callable<String>() {

				@Override
				public String call() throws Exception {
					return harvestable.harvest();
				}
			}));
		}
		executor.shutdown();

		for (Future<String> future : futures) {
			try {
				String collectionId = future.get();
				logger.info("Collection " + collectionId + " successfully harvested");
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			} catch (ExecutionException e) {
				logger.error(e.getMessage(), e);
			}
		}

		logger.info("Harvested all registered Harvestable sources");

	}
	
	public void harvest(String id) {

	}
	
	public void harvestEndpoint(String endpoint) {

	}
	
	public List<Harvest> getHarvests(Integer limit, Integer offset) {
		return this.harvesterDatastore.getHarvests(limit, offset);
	}
	
	private Map<Harvestable, ScheduledExecutorService> buildHarvestableMap() {
		
		return new HashMap<>();
	}

}
