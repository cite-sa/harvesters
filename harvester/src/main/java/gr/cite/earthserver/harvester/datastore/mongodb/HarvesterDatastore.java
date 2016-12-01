package gr.cite.earthserver.harvester.datastore.mongodb;

import java.util.List;

import javax.naming.OperationNotSupportedException;

import gr.cite.earthserver.harvester.datastore.model.Harvest;
import gr.cite.earthserver.harvester.datastore.model.Status;

public interface HarvesterDatastore {
	
	public String registerHarvest(Harvest harvest);
	
	public String unregisterHarvest(String id);
	
	public String updateHarvest(Harvest harvest) throws OperationNotSupportedException;
	
	public Harvest getHarvestById(String id);
	
	public Harvest getHarvestByEndpoint(String endpoint);
	
	public Harvest getHarvestByEndpointAlias(String endpointAlias);
	
	public List<Harvest> getHarvests();
	
	public List<Harvest> getHarvests(Integer limit, Integer offset);
	
	public List<Harvest> getHarvestsToBeHarvested();
	
	public Harvest updateHarvestStatus(String id, Status status);
	
}
