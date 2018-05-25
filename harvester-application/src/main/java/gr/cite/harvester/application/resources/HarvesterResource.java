package gr.cite.harvester.application.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import gr.cite.harvester.core.HarvestableFactory;
import gr.cite.harvester.datastore.model.Harvest;
import gr.cite.harvester.datastore.model.Status;
import org.springframework.stereotype.Component;

import gr.cite.harvester.core.Harvestable;
import gr.cite.harvester.core.Harvester;

@Component
@Path("harvester")
@Produces(MediaType.APPLICATION_JSON)
public class HarvesterResource {
	private Harvester harvester;
	private HarvestableFactory harvestableFactory;
	//private Harvestable harvestable;

	@Inject
	public HarvesterResource(Harvester harvester, HarvestableFactory harvestableFactory) {
		this.harvester = harvester;
		this.harvestableFactory = harvestableFactory;
	}

	/*@Inject
	public void setHarvestable(Harvestable harvestable) {
		this.harvestable = harvestable;
	}*/

	@GET
	@Path("ping")
	public Response ping() {
		return Response.ok("pong").build();
	}

	@POST
	@Path("harvests")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response register(Harvest harvest) {

		if (harvest.getEndpointAlias() == null) {
			harvest.setEndpointAlias(UUID.randomUUID().toString());
		}
		
		Harvestable harvestable = this.harvestableFactory.get(harvest.getType());
		harvestable.setHarvest(harvest);
		String id = harvester.register(harvestable);

		return Response.created(UriBuilder.fromResource(HarvesterResource.class).path("harvests").path(id).build()).build();
	}
	
	@POST
	@Path("harvests/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") String id, Harvest harvest) {
		harvest.setId(id);
		
		if (harvest.getEndpointAlias() == null) {
			harvest.setEndpointAlias(UUID.randomUUID().toString());
		}
		
		harvest = harvester.updateHarvest(harvest);
		
		return Response.ok(harvest).build();
	}
	
	@POST
	@Path("harvests/{id}/status")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateHarvestStatus(@PathParam("id") String id, Status status) {
		Harvest harvest = null;
		if (Status.STOPPED.equals(status) || Status.PENDING.equals(status)) {
			harvest = harvester.updateHarvestStatus(id, status);
		}
		
		return Response.ok(harvest).build();
	}

	@DELETE
	@Path("harvests")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deregister(Harvest harvest) {
		String id = harvester.unregister(harvest.getId());
		return Response.ok(id).build();
	}

	@GET
	@Path("/harvests/{id}")
	public Response getHarvest(@PathParam("id") String id) {
		Harvest harvest = harvester.getHarvest(id);
		return Response.ok(harvest).build();
	}

	@GET
	@Path("/harvests")
	public Response getHarvests(@QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset) {
		List<Harvest> harvests = harvester.getHarvests(limit, offset);
		return Response.ok(harvests).build();
	}

	@GET
	@Path("getHarvestsUI")
	public Response getHarvestsUI(@QueryParam("request[Size]") Integer limit,
			@QueryParam("request[Offset]") Integer offset) {
		List<Harvest> harvests = harvester.getHarvests(limit, offset);
		Map<String, Object> result = this.serialize(harvests);
		return Response.ok(result).build();
	}

	private Map<String, Object> serialize(List<Harvest> harvests) {

		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> rows = new ArrayList<>();

		for (Harvest harvest : harvests) {
			Map<String, Object> row = new HashMap<>();
			Map<String, Object> rowData = new HashMap<>();

			rowData.put("ID", harvest.getId());
			rowData.put("Endpoint", harvest.getEndpoint());
			rowData.put("EndpointAlias", harvest.getEndpointAlias());
			rowData.put("SchedulePeriod", harvest.getSchedule().getPeriod().toString() + " "
					+ harvest.getSchedule().getPeriodType().toString());
			rowData.put("StartTime",
					harvest.getCurrentHarvestCycle() == null ? ""
							: harvest.getCurrentHarvestCycle().getStartTime() == null ? ""
									: harvest.getCurrentHarvestCycle().getStartTime().toString());
			rowData.put("EndTime",
					harvest.getCurrentHarvestCycle() == null ? ""
							: harvest.getCurrentHarvestCycle().getEndTime() == null ? ""
									: harvest.getCurrentHarvestCycle().getEndTime().toString());
			rowData.put("TotalElements",
					harvest.getCurrentHarvestCycle() == null ? ""
							: harvest.getCurrentHarvestCycle().getTotalElements() == null ? ""
									: harvest.getCurrentHarvestCycle().getTotalElements().toString());
			rowData.put("NewElements",
					harvest.getCurrentHarvestCycle() == null ? ""
							: harvest.getCurrentHarvestCycle().getNewElements() == null ? ""
									: harvest.getCurrentHarvestCycle().getNewElements().toString());
			rowData.put("UpdatedElements",
					harvest.getCurrentHarvestCycle() == null ? ""
							: harvest.getCurrentHarvestCycle().getUpdatedElements() == null ? ""
									: harvest.getCurrentHarvestCycle().getUpdatedElements().toString());
			rowData.put("PreviousHarvests",
					harvest.getPreviousHarvestCycles() == null ? ""
						: Integer.toString(harvest.getPreviousHarvestCycles().size()));

			// rowData.put("EndTime",
			// harvest.getCurrentHarvestCycle().getEndTime() == null ? "" :
			// harvest.getCurrentHarvestCycle().getEndTime().toString());
			rowData.put("Status", harvest.getStatus().toString());

			row.put("Data", rowData);
			rows.add(row);
		}

		result.put("Rows", rows);

		Map<String, Object> context = new HashMap<>();
		context.put("Total", rows.size());
		context.put("Filtered", rows.size());
		result.put("Context", context);

		return result;
	}
}
