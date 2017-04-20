package gr.cite.harvester.application;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import gr.cite.harvester.datastore.mongodb.HarvesterDatastore;

@Component
public class ContextClosedHandler implements ApplicationListener<ContextClosedEvent> {
	
	

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		
		HarvesterDatastore datastore = (HarvesterDatastore) event.getApplicationContext().getBean("harvesterDatastore");
		datastore.close();
		
		System.out.println(event.getApplicationContext().containsBean("harvestScheduler"));
		
		System.out.println("SHUTTING DOWN");
		
	}

}