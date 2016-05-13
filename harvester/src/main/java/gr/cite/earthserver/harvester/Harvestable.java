package gr.cite.earthserver.harvester;

public interface Harvestable {
	public void harvest() throws Exception;

	public String getEndpoint();
}
