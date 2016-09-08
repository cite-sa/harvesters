package gr.cite.earthserver.harvester.application;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import gr.cite.earthserver.harvester.Harvester;
import gr.cite.earthserver.wcs.adapter.WCSAdapter;
import gr.cite.femme.client.FemmeClient;

public class HarvesterApplicationBinder extends AbstractBinder {

	@Override
	protected void configure() {
		bind(Harvester.class).to(Harvester.class);
		bind(WCSAdapter.class).to(WCSAdapter.class);
	}

}
