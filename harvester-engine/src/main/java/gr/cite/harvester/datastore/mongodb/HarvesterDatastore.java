package gr.cite.harvester.datastore.mongodb;

import java.util.List;

import gr.cite.harvester.datastore.model.Harvest;
import gr.cite.harvester.datastore.model.HarvestCycle;
import gr.cite.harvester.datastore.model.Status;

public interface HarvesterDatastore {
	
	public void close();
	
	public String insertHarvest(Harvest harvest);
	
	public String deleteHarvest(String id);
	
	public Harvest updateHarvest(Harvest harvest);
	
	public Harvest updateHarvestStatus(String id, Status status);

	public Harvest updateHarvestStatus(String id, Status status, String errorMessage);
	
	public Harvest getHarvestById(String id);
	
	public Harvest getHarvestByEndpoint(String endpoint);
	
	public Harvest getHarvestByEndpointAlias(String endpointAlias);
	
	public List<Harvest> getHarvests();
	
	public List<Harvest> getHarvests(Integer limit, Integer offset);
	
	public List<Harvest> getHarvestsToBeHarvested();

	public Harvest incrementHarvestedElementsCounters(String harvestId, HarvestCycle harvestCycle);

	public Harvest updateHarvestedCyCle(String harvestId, HarvestCycle harvestCycle);

	/*public Harvest incrementHarvestedElementsCounters(String id, Map<String, Integer> incrementValuePerField);*/

}
