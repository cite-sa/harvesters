package gr.cite.earthserver.harvester;

import org.junit.Test;

import gr.cite.earthserver.harvester.wcs.WCSHarvestableEndpoint;
import gr.cite.earthserver.wcs.femme.client.WCSFemmeClient;
import gr.cite.femme.datastore.exceptions.DatastoreException;

public class WCSHarvesterTest {
	
	@Test
	public void harvest() {
		try {
			new WCSHarvestableEndpoint("https://rsg.pml.ac.uk/rasdaman/ows", new WCSFemmeClient()).harvest();
		} catch (DatastoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
