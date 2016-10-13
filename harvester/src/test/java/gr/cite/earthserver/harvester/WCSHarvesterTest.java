package gr.cite.earthserver.harvester;

import org.junit.Before;
import org.junit.Test;

import gr.cite.earthserver.harvester.core.Harvester;
import gr.cite.earthserver.harvester.wcs.WCSHarvestable;
import gr.cite.earthserver.wcs.adapter.WCSAdapter;
import gr.cite.earthserver.wcs.core.WCSRequestBuilder;
import gr.cite.earthserver.wcs.core.WCSRequestException;
import gr.cite.earthserver.wcs.core.WCSResponse;
import gr.cite.earthserver.wcs.utils.ParseException;
import gr.cite.femme.client.FemmeDatastoreException;

public class WCSHarvesterTest {
	private Harvester harvester;
	
	/*@Before
	public void init() {
		harvester = new Harvester();
	}*/
	
	@Test
	public void harvest() throws WCSRequestException, ParseException, FemmeDatastoreException {
		WCSAdapter wcsAdapter = new WCSAdapter("http://es-devel1.local.cite.gr:8080/femme-application-0.0.1-SNAPSHOT");
		
		/*harvester.register(new WCSHarvestableEndpoint("https://rsg.pml.ac.uk/rasdaman/ows",*/
		/*harvester.register(new WCSHarvestableEndpoint("http://access.planetserver.eu:8080/rasdaman/ows",
				new WCSAdapter("http://localhost:8081/femme-application/")));*/
		
		//harvester.harvest();
		
		
		WCSRequestBuilder wcsRequestBuilder = new WCSRequestBuilder().endpoint("http://access.planetserver.eu:8080/rasdaman/ows");
		WCSResponse describeCoverage = null;
		describeCoverage = wcsRequestBuilder.describeCoverage().coverageId("frt0000cc22_07_if165l_trr3").build().get();
		wcsAdapter.addCoverage(describeCoverage, "57dc1256d745904314b13b8b");
	}
}
