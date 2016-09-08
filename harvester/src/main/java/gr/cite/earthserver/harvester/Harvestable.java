package gr.cite.earthserver.harvester;

/**
 * 
 * The {@code Harvestable} interface should be implemented by any class whose
 * instances are intended to be registered and executed by the {@link Harvester}
 * 
 * @author Ioannis Kavvouras
 *
 */
public interface Harvestable {

	public String getEndpoint();

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
