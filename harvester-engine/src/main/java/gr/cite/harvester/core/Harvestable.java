package gr.cite.harvester.core;

import gr.cite.harvester.datastore.model.Harvest;
import gr.cite.harvester.datastore.mongodb.HarvesterDatastore;

/*
 * 
 * The {@code Harvestable} interface should be implemented by any class whose
 * instances are intended to be registered and executed by the {@link Harvester}
 * 
 */
public interface Harvestable {

	public HarvesterDatastore getHarvesterDatastore();
	
	/*public void setHarvesterDatastore(HarvesterDatastore harvesterDatastore);*/

	public Harvest getHarvest();

	public void setHarvest(Harvest harvest);

	/**
	 * The {@link Harvester} in which this {@code Harvestable} is registered
	 * will call this method in order to harvest the source.
	 * 
	 * @return
	 * @throws Exception
	 *             if unable to harvest the source
	 */
	public Harvest harvest() throws Exception;
	

}
