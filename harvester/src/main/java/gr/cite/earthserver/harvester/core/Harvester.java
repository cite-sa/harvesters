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

	
	@Inject
	public Harvester(HarvesterDatastore harvesterDatastore) {
		this.harvesterDatastore = harvesterDatastore;
	}
	
	public String register(Harvestable harvestable) {
		return this.harvesterDatastore.insertHarvest(harvestable.getHarvest());
	}

	public String unregister(String id) {
		return this.harvesterDatastore.deleteHarvest(id);
	}
	
	public Harvest updateHarvestStatus(String id, Status status) {
		return this.harvesterDatastore.updateHarvestStatus(id, status);
	}

	public Harvest getHarvest(String id) {
		return this.harvesterDatastore.getHarvestById(id);
	}

	public List<Harvest> getHarvests(Integer limit, Integer offset) {
		return this.harvesterDatastore.getHarvests(limit, offset);
	}
	
	public void startAllHarvests() {
		
		/*List <Harvest> harvests = this.harvesterDatastore.getHarvestsToBeHarvested();

		for (Harvest harvest : harvests) {
			WCSHarvestable harvestable = new WCSHarvestable();
			harvestable.setHarvest(harvest);
			harvestable.setWcsAdapter(new WCSAdapter("http://localhost:8081/femme-application"));
			try {
				harvestable.harvest();
			} catch (FemmeDatastoreException e) {
				logger.error(e.getMessage(),e);
			}
		}*/
		
	}

	/*public Harvest startHarvest(String id) {
		return this.harvesterDatastore.updateHarvestStatus(id, Status.RUNNING);
	}

	public Harvest stopHarvest(String id) {
		return this.harvesterDatastore.updateHarvestStatus(id, Status.STOPPED);
	}*/
	
}
