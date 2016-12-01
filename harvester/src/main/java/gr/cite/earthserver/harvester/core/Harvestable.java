package gr.cite.earthserver.harvester.core;

import gr.cite.earthserver.harvester.datastore.model.Harvest;

/**
 * 
 * The {@code Harvestable} interface should be implemented by any class whose
 * instances are intended to be registered and executed by the {@link Harvester}
 * 
 * @author Ioannis Kavvouras
 *
 */
public interface Harvestable {
	
	//private Harvest harvest;

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
	public String harvest() throws Exception;
	

}
