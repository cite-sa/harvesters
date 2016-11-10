package gr.cite.earthserver.harvester;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import gr.cite.earthserver.harvester.core.Harvestable;
import gr.cite.earthserver.harvester.core.Harvester;
import gr.cite.earthserver.harvester.datastore.model.Harvest;
import gr.cite.earthserver.harvester.datastore.model.Schedule;
import gr.cite.earthserver.harvester.datastore.mongodb.HarvesterDatastoreMongo;
import gr.cite.earthserver.harvester.wcs.WCSHarvestable;
import gr.cite.earthserver.wcs.adapter.WCSAdapter;
import gr.cite.earthserver.wcs.core.WCSRequestBuilder;
import gr.cite.earthserver.wcs.core.WCSRequestException;
import gr.cite.earthserver.wcs.core.WCSResponse;
import gr.cite.earthserver.wcs.utils.ParseException;
import gr.cite.femme.client.FemmeDatastoreException;

public class WCSHarvesterTest {
	private Harvester harvester;
	
	@Before
	public void init() {
		this.harvester = new Harvester(new HarvesterDatastoreMongo("localhost:27017", "harvester-db"));
	}
	
	@Test
	public void harvest() throws WCSRequestException, ParseException, FemmeDatastoreException {
		
		WCSAdapter wcsAdapter = new WCSAdapter("http://es-devel1.local.cite.gr:8080/femme-application");
//		WCSAdapter wcsAdapter = new WCSAdapter("http://localhost:8081/femme-application");
		Harvest harvest = new Harvest();
//		harvest.setEndpoint("http://access.planetserver.eu:8080/rasdaman/ows");
//		harvest.setEndpoint("http://incubator.ecmwf.int/2e/rasdaman/ows");
		harvest.setEndpoint("https://rsg.pml.ac.uk/rasdaman/ows");
		harvest.setSchedule(new Schedule(new Long(60), TimeUnit.DAYS));
		
		WCSHarvestable wcsHarvestable = new WCSHarvestable();
		wcsHarvestable.setWcsAdapter(wcsAdapter);
		wcsHarvestable.setHarvest(harvest);
		
		
		this.harvester.register(wcsHarvestable);
		this.harvester.harvest();
		
		
		/*WCSRequestBuilder wcsRequestBuilder = new WCSRequestBuilder().endpoint("http://access.planetserver.eu:8080/rasdaman/ows");
		WCSResponse describeCoverage = null;
		describeCoverage = wcsRequestBuilder.describeCoverage().coverageId("frt0000cc22_07_if165l_trr3").build().get();
		wcsAdapter.addCoverage(describeCoverage, "57dc1256d745904314b13b8b");*/
	}
}
