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
		return harvesterDatastore.insertHarvest(harvestable.getHarvest());
	}

	public String unregister(String id) {
		return harvesterDatastore.deleteHarvest(id);
	}
	
	public Harvest updateHarvestStatus(String id, Status status) {
		return harvesterDatastore.updateHarvestStatus(id, status);
	}

	public Harvest getHarvest(String id) {
		return this.harvesterDatastore.getHarvestById(id);
	}

	public List<Harvest> getHarvests(Integer limit, Integer offset) {
		return harvesterDatastore.getHarvests(limit, offset);
	}
	
}
