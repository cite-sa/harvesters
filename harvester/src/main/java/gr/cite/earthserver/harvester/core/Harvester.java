package gr.cite.earthserver.harvester.core;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.earthserver.harvester.datastore.model.Harvest;
import gr.cite.earthserver.harvester.datastore.model.Status;
import gr.cite.earthserver.harvester.datastore.mongodb.HarvesterDatastore;
import gr.cite.earthserver.harvester.wcs.WCSHarvestable;
import gr.cite.earthserver.wcs.adapter.WCSAdapter;
import gr.cite.femme.client.FemmeDatastoreException;

public class Harvester {

	private static final Logger logger = LoggerFactory.getLogger(Harvester.class);

	private HarvesterDatastore harvesterDatastore;

	/*private Map<Harvestable, ScheduledExecutorService> harvestables;*/

	private ScheduledExecutorService harvestExecutor;
	

	private HarvesterTask harvesterTask;

	@Inject
	public Harvester(HarvesterDatastore harvesterDatastore) {
		this.harvesterDatastore = harvesterDatastore;
		/*this.harvestables = this.buildHarvestableMap();*/
		this.harvesterTask = harvesterTask;
		this.harvestExecutor = Executors.newSingleThreadScheduledExecutor();
		this.harvestExecutor.scheduleAtFixedRate(new HarvesterTask(this.harvesterDatastore),0,70,TimeUnit.SECONDS);
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
		logger.info("Endpoint " + harvestable.getHarvest().getEndpoint() + " has been registered successfully");
	}

	public void unregister(String endpoint) {
		this.harvesterDatastore.unregisterHarvest(endpoint);
	}

	public void unregister(Harvestable harvestable) {
		harvesterDatastore.unregisterHarvest(harvestable.getHarvest().getId());
	}

	public void harvest() {
//		List <Harvest> harvests = this.harvesterDatastore.getHarvests();
		List <Harvest> harvests = this.harvesterDatastore.getHarvestsToBeHarvested();

		for (Harvest harvest : harvests) {
			WCSHarvestable harvestable = new WCSHarvestable();
			harvestable.setHarvest(harvest);
			harvestable.setWcsAdapter(new WCSAdapter("http://localhost:8081/femme-application"));
			try {
				harvestable.harvest();
			} catch (FemmeDatastoreException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}

	public void harvest(String id) {
		this.harvesterDatastore.updateHarvestStatus(id, Status.RUNNING);
	}

	public void stopHarvest(String id) {
		this.harvesterDatastore.updateHarvestStatus(id, Status.STOPPED);
	}

	public List<Harvest> getHarvests(Integer limit, Integer offset) {
		return this.harvesterDatastore.getHarvests(limit, offset);
	}

	/*private Map<Harvestable, ScheduledExecutorService> buildHarvestableMap() {
		return new HashMap<>();
	}*/
}
