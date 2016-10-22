package gr.cite.earthserver.harvester.datastore.mongodb;

import java.util.List;

import gr.cite.earthserver.harvester.datastore.model.Harvest;
import gr.cite.earthserver.harvester.datastore.model.Status;

public interface HarvesterDatastore {
	
	public String registerHarvest(Harvest harvest);
	
	public String unregisterHarvest(String id);
	
	public String updateHarvest(Harvest harvest);
	
	public Harvest getHarvestById(String id);
	
	public Harvest getHarvestByEndpoint(String endpoint);
	
	public List<Harvest> getHarvests(Integer limit, Integer offset);
	
	public List<Harvest> updateHarvestStatus(Status status);
	
	public Harvest updateHarvestStatus(String id, Status status);
	
	
}
