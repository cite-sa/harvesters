package gr.cite.earthserver.harvester;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import gr.cite.earthserver.harvester.datastore.model.HarvestCycle;
import gr.cite.earthserver.harvester.datastore.model.Status;
import gr.cite.earthserver.harvester.datastore.mongodb.HarvesterDatastore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.earthserver.harvester.core.Harvester;
import gr.cite.earthserver.harvester.datastore.model.Harvest;
import gr.cite.earthserver.harvester.datastore.model.Schedule;
import gr.cite.earthserver.harvester.datastore.mongodb.HarvesterDatastoreMongo;
import gr.cite.earthserver.harvester.wcs.WCSHarvestable;
import gr.cite.earthserver.wcs.adapter.WCSAdapter;
import gr.cite.earthserver.wcs.core.WCSRequestException;
import gr.cite.earthserver.wcs.utils.ParseException;
import gr.cite.femme.client.FemmeDatastoreException;

public class WCSHarvesterTest {

	private Harvester harvester;

	private HarvesterDatastore harvesterDatastore;

	private static final Logger logger = LoggerFactory.getLogger(WCSHarvesterTest.class);

	@Before
	public void init() {
		/*this.harvester = new Harvester(new HarvesterDatastoreMongo("localhost:27017", "harvester-db", 5));*/
		harvesterDatastore = new HarvesterDatastoreMongo("localhost:27017", "harvester-db", 5);
	}

	@After
	public void close() {
		harvesterDatastore.close();
	}

//	@Test
	public void insertHarvest() throws InterruptedException {
		Harvest harvest = new Harvest();

		HarvestCycle harvestCycle1 = new HarvestCycle();
		TimeUnit.SECONDS.sleep(1);
		HarvestCycle harvestCycle2 = new HarvestCycle();
		TimeUnit.SECONDS.sleep(1);
		HarvestCycle harvestCycle3 = new HarvestCycle();

		harvest.setStatus(Status.PENDING);
		harvest.getPreviousHarvestCycles().addAll(Arrays.asList(harvestCycle1, harvestCycle2, harvestCycle3));
		harvest.setCurrentHarvestCycle(new HarvestCycle());

		harvesterDatastore.insertHarvest(harvest);
	}

//	@Test
	public void updateHarvest() {
		harvesterDatastore.updateHarvestStatus("58789c820a873470e741f39c", Status.FINISHED);
	}

//	@Test
	public void harvest() throws WCSRequestException, ParseException, FemmeDatastoreException {

//		WCSAdapter wcsAdapter = new WCSAdapter("http://es-devel1.local.cite.gr:8080/femme-application");
//		WCSAdapter wcsAdapter = new WCSAdapter("http://localhost:8081/femme-application");
		
		Harvest harvest = new Harvest();

		harvest.setEndpoint("http://access.planetserver.eu:8080/rasdaman/ows");
		harvest.setEndpointAlias("PlanetServer");
		Schedule schedule = new Schedule(new Long(96), ChronoUnit.HOURS);
		harvest.setSchedule(schedule);
		
		/*harvest.setEndpoint("http://incubator.ecmwf.int/2e/rasdaman/ows");*/
//		harvest.setEndpoint("http://earthserver.ecmwf.int/rasdaman/ows");
//		harvest.setEndpointAlias("ECMWF");
		

//		harvest.setEndpoint("https://rsg.pml.ac.uk/rasdaman/ows");
//		harvest.setEndpointAlias("PML");
//		Schedule schedule = new Schedule(new Long(90), ChronoUnit.SECONDS);
//		harvest.setSchedule(schedule);
		

		WCSHarvestable wcsHarvestable = new WCSHarvestable();
		wcsHarvestable.setHarvest(harvest);

		this.harvester.register(wcsHarvestable);
		
	}
}
