package gr.cite.earthserver.harvester.application;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import gr.cite.earthserver.harvester.application.resources.HarvesterResource;

@ApplicationPath("restAPI")
public class HarvesterApplication extends ResourceConfig {
	
	public HarvesterApplication() {
		register(JacksonFeature.class);
		register(HarvesterResource.class);
	}
	
//	public void setAsStopped() {
//		Runtime.getRuntime().addShutdownHook(new Thread(){
//			@Override
//			public void run(){
//				System.out.println("Test on close event");
//			}
//		});
//	}
	
}
