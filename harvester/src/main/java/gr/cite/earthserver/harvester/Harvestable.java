package gr.cite.earthserver.harvester;

public interface Harvestable {
	
	public void setEndpoint(String endpoint);
	
	public String getEndpoint();
	
	public void harvest() throws Exception;

	
}
