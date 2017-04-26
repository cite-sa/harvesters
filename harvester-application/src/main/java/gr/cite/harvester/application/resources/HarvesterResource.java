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
	@Produces(MediaType.APPLICATION_JSON)
	public Response ping() {
		return Response.ok("pong").build();
	}

	@POST
	@Path("harvests")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(Harvest harvest) {

		if (harvest.getEndpointAlias() == null) {
			harvest.setEndpointAlias(UUID.randomUUID().toString());
		}
		harvestable.setHarvest(harvest);
		String id = harvester.register(harvestable);

		return Response.ok(id).build();
	}

	@DELETE
	@Path("harvests")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deregister(Harvest harvest) {
		String id = harvester.unregister(harvest.getId());
		return Response.ok(id).build();
	}

	@POST
	@Path("harvests/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editHarvest(@PathParam("id") String id, Status status) {
		Harvest harvest = null;
		if (Status.STOPPED.equals(status) || Status.PENDING.equals(status)) {
			harvest = harvester.updateHarvestStatus(id, status);
		}

		return Response.ok(harvest).build();
	}

	/*
	 * @POST
	 * 
	 * @Path("harvests/{id}/start") public Response
	 * startHarvest(@PathParam("id") String id) { if (id != null) {
	 * harvester.startHarvest(id); } else { harvester.startAllHarvests(); }
	 * 
	 * return Response.ok().build(); }
	 * 
	 * @POST
	 * 
	 * @Path("harvests/{id}/stop") public Response stopHarvest(@PathParam("id")
	 * String id) { harvester.stopHarvest(id); return Response.ok(id).build(); }
	 */

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
	@Produces(MediaType.APPLICATION_JSON)
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
