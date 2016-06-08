package gr.cite.earthserver.harvester;

public interface Harvestable {
	
	public String getEndpoint();
	
	public String harvest() throws Exception;

	
}
