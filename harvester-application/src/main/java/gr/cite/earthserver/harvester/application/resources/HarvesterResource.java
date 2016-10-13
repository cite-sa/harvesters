package gr.cite.earthserver.harvester.application.resources;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import gr.cite.earthserver.harvester.core.Harvestable;
import gr.cite.earthserver.harvester.core.Harvester;
import gr.cite.earthserver.harvester.datastore.model.Harvest;
import gr.cite.earthserver.harvester.datastore.model.Schedule;
import gr.cite.earthserver.harvester.wcs.WCSHarvestable;
import gr.cite.earthserver.wcs.adapter.WCSAdapter;
import gr.cite.earthserver.wcs.adapter.api.WCSAdapterAPI;

@Component
@Path("harvester")
@Produces(MediaType.APPLICATION_JSON)
public class HarvesterResource {

	private Harvester harvester;
	
	//private WCSAdapterAPI wcsAdapter;
	
	private Harvestable harvestable;

	@Inject
	public HarvesterResource(Harvester harvester) {
		this.harvester = harvester;
	}
	
	@Inject
	public void setHarvestable(Harvestable harvestable) {
		this.harvestable = harvestable;
	}
	
	@GET
	@Path("ping")
	@Produces(MediaType.TEXT_PLAIN)
	public Response ping() {
		return Response.ok("pong").build();
	}

	@POST
	@Path("register")
	/*@Consumes(MediaType.APPLICATION_FORM_URLENCODED)*/
	public Response register(
			@FormParam("endpoint") String endpoint,
			@FormParam("endpointAlias") String endpointAlias,
			@FormParam("period") Long period,
			@FormParam("timeUnit") String timeUnit) {
		
		//harvester.register(new WCSHarvestable(endpoint, schedule));
		endpointAlias = endpointAlias == null ? UUID.randomUUID().toString() : endpointAlias; 
		harvestable.setHarvest(new Harvest(endpoint, endpointAlias, new Schedule(period, TimeUnit.valueOf(timeUnit))));
		//harvester.register(new WCSHarvestable(endpoint, schedule, this.wcsAdapter));
		harvester.register(harvestable);
		
		return Response.ok().build();
	}
	
	@POST
	@Path("unregister")
	public Response unregister(
			@FormParam("endpoint") String endpoint) {
		
		harvester.unregister(endpoint);
		
		return Response.ok().build();
	}
	
	@POST
	@Path("harvest")
	public Response harvest(@QueryParam("id") String id, @QueryParam("endpoint") String endpoint) {
		if (id != null) {			
			harvester.harvest(id);
		} else if (endpoint != null) {
			harvester.harvestEndpoint(endpoint);
		} else {
			harvester.harvest();
		}
		
		return Response.ok().build();
	}
	
	@GET
	@Path("/harvests")
	public Response getHarvests(@QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset) {
		List<Harvest> harvests = harvester.getHarvests(limit, offset);
		
		return Response.ok(harvests).build();
	}
}
