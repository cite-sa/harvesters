package gr.cite.harvester.core;

import gr.cite.femme.client.FemmeException;
import gr.cite.femme.client.api.FemmeClientAPI;

public class Adapter {
	private FemmeClientAPI femmeClient;
	
	public Adapter(FemmeClientAPI femmeClient) {
		this.femmeClient = femmeClient;
	}
	
	public String beginImport(String endpointAlias, String endpoint) throws FemmeException {
		return this.femmeClient.beginImport(endpointAlias, endpoint);
	}
	
	public void endImport(String importId) throws FemmeException {
		this.femmeClient.endImport(importId);
	}
	
	protected FemmeClientAPI getFemmeClient() {
		return this.femmeClient;
	}
}
