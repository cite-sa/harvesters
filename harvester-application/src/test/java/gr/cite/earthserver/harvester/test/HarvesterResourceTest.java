package gr.cite.earthserver.harvester.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

public class HarvesterResourceTest {

	@Test
	public void testResource() {
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
		formData.add("endpoint", "http://access.planetserver.eu:8080/rasdaman/ows");
		formData.add("period", "60");
		formData.add("timeUnit", "MINUTES");
		formData.add("initialDelay", "0");
		
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target("http://192.168.32.84:8082/harvester-application/harvester");
		
		webTarget
			.path("register")
			.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED));
		
		
		
		
	}
}
