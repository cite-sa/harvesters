package gr.cite.earthserver.harvester;

import org.junit.Before;
import org.junit.Test;

import gr.cite.earthserver.harvester.wcs.WCSHarvestableEndpoint;
import gr.cite.earthserver.wcs.femme.client.WCSFemmeClient;
import gr.cite.femme.client.FemmeClient;
import gr.cite.femme.datastore.exceptions.DatastoreException;

public class WCSHarvesterTest {
	private Harvester harvester;
	
	@Before
	public void init() {
		harvester = new Harvester();
	}
	
	@Test
	public void harvest() {
		/*harvester.register(new WCSHarvestableEndpoint("https://rsg.pml.ac.uk/rasdaman/ows", new WCSFemmeClient()));*/
		harvester.register(new WCSHarvestableEndpoint("http://access.planetserver.eu:8080/rasdaman/ows", new WCSFemmeClient()));
		
		harvester.harvest();
	}
}
