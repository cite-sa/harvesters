package gr.cite.earthserver.harvester;

import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.earthserver.harvester.core.Harvester;
import gr.cite.earthserver.harvester.datastore.model.Harvest;
import gr.cite.earthserver.harvester.datastore.model.Schedule;
import gr.cite.earthserver.harvester.datastore.mongodb.HarvesterDatastoreMongo;
import gr.cite.earthserver.harvester.wcs.WCSHarvestable;
import gr.cite.earthserver.wcs.core.WCSRequestException;
import gr.cite.earthserver.wcs.utils.ParseException;
import gr.cite.femme.client.FemmeDatastoreException;

public class WCSHarvesterTest {
	private Harvester harvester;

	private static final Logger logger = LoggerFactory.getLogger(WCSHarvesterTest.class);

	@Before
	public void init() {
		this.harvester = new Harvester(new HarvesterDatastoreMongo("localhost:27017", "harvester-db-devel"));
	}

	@Test
	public void harvest() throws WCSRequestException, ParseException, FemmeDatastoreException {

//		WCSAdapter wcsAdapter = new WCSAdapter("http://es-devel1.local.cite.gr:8080/femme-application");
//		WCSAdapter wcsAdapter = new WCSAdapter("http://localhost:8081/femme-application");
		
		Harvest harvest = new Harvest();
<<<<<<< HEAD
//		harvest.setEndpoint("http://access.planetserver.eu:8080/rasdaman/ows");
//		harvest.setEndpoint("http://incubator.ecmwf.int/2e/rasdaman/ows");
=======
>>>>>>> d09281e4e592bcd2cb0fe9f02b2e9656ac66c2ec
		harvest.setEndpoint("https://rsg.pml.ac.uk/rasdaman/ows");
		harvest.setSchedule(new Schedule(new Long(50), ChronoUnit.SECONDS));

		WCSHarvestable wcsHarvestable = new WCSHarvestable();
		wcsHarvestable.setHarvest(harvest);

		this.harvester.register(wcsHarvestable);
		logger.info(wcsHarvestable.getHarvest().getId()+" ready to be Harvested");
		
//		Harvest harvest2 = new Harvest();
//		harvest2.setEndpoint("http://access.planetserver.eu:8080/rasdaman/ows");
//		harvest2.setSchedule(new Schedule(new Long(60), ChronoUnit.SECONDS));
//		
//		WCSHarvestable wcsHarvestable2 = new WCSHarvestable();
//		wcsHarvestable2.setHarvest(harvest2);
//		logger.info(wcsHarvestable2.getHarvest().getId()+" ready to be Harvested");
//		
//		this.harvester.register(wcsHarvestable2);
//		
//		Harvest harvest3 = new Harvest();
//		harvest3.setEndpoint("http://earthserver.ecmwf.int/rasdaman/ows");
//		harvest3.setSchedule(new Schedule(new Long(20), ChronoUnit.SECONDS));
//
//		WCSHarvestable wcsHarvestable3 = new WCSHarvestable();
//		wcsHarvestable3.setHarvest(harvest3);
//
//		this.harvester.register(wcsHarvestable3);
//		logger.info(wcsHarvestable3.getHarvest().getId()+" ready to be Harvested");
	}
}
