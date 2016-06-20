package gr.cite.earthserver.harvester.resources;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import gr.cite.earthserver.harvester.Harvester;
import gr.cite.earthserver.harvester.Schedule;
import gr.cite.earthserver.harvester.wcs.WCSHarvestableEndpoint;
import gr.cite.femme.client.FemmeClient;

@Path("/harvester")
public class HarvesterResource {

	@Inject
	private Harvester harvester;

	@Inject
	private FemmeClient femmeClient;

	@GET
	@Path("ping")
	@Produces(MediaType.TEXT_PLAIN)
	public String ping() {
		return "pong";
	}

	@POST
	@Path("register")
	public Response register(
			@FormParam("endpoint") String endpoint,
			@FormParam("period") Long period,
			@FormParam("timeUnit") String timeUnit,
			@FormParam("initialDelay") Long initialDelay) {
		
		TimeUnit unit = TimeUnit.valueOf(timeUnit);
		
		Schedule schedule = null;
		if (initialDelay == null) {
			schedule = new Schedule(period.longValue(), TimeUnit.valueOf(timeUnit));
		} else {
			schedule = new Schedule(initialDelay, period, TimeUnit.valueOf(timeUnit));
		}
		
		/*Schedule schedule = initialDelay == null ? 
			new Schedule(period.longValue(), TimeUnit.valueOf(timeUnit))
			: new Schedule(initialDelay, period, TimeUnit.valueOf(timeUnit)); */
		
		harvester.register(new WCSHarvestableEndpoint(endpoint, femmeClient), schedule);
		
		return Response.ok().build();
	}
	
	@POST
	@Path("unregister")
	public Response unregister(
			@FormParam("endpoint") String endpoint) {
		
		/*harvester.unregister(harvestable);*/
		
		return Response.ok().build();
	}
}
