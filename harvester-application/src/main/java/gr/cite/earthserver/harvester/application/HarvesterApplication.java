package gr.cite.earthserver.harvester.application;

import org.glassfish.jersey.server.ResourceConfig;

public class HarvesterApplication extends ResourceConfig {
	
	public HarvesterApplication() {
		register(new HarvesterApplicationBinder());
	}
	
}
