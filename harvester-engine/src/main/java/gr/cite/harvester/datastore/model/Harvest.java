package gr.cite.harvester.datastore.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class Harvest {

	@JsonProperty
	private String id;
	
	@JsonProperty
	private HarvestType type;
	
	@JsonProperty
	private String endpoint;
	
	@JsonProperty
	private String endpointAlias;
	
	@JsonProperty
	private Schedule schedule;
	
	@JsonProperty
	private Status status;

	@JsonProperty
	private HarvestCycle currentHarvestCycle;

	@JsonProperty
	private List<HarvestCycle> previousHarvestCycles;

	@JsonProperty
	private Instant created;

	@JsonProperty
	private Instant modified;
	
	public Harvest() {
		this.id = new ObjectId().toString();
		this.status = Status.PENDING;
		this.previousHarvestCycles = new ArrayList<>();
	}
	
	public Harvest(String endpoint, String endpointAlias, Schedule schedule) {
		this.id = new ObjectId().toString();
		this.endpoint = endpoint;
		this.endpointAlias = endpointAlias;
		this.schedule = schedule;
		this.status = Status.PENDING;
		this.previousHarvestCycles = new ArrayList<>();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public HarvestType getType() {
		return type;
	}
	
	public void setType(HarvestType type) {
		this.type = type;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getEndpointAlias() {
		return endpointAlias;
	}

	public void setEndpointAlias(String endpointAlias) {
		this.endpointAlias = endpointAlias;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public HarvestCycle getCurrentHarvestCycle() {
		return currentHarvestCycle;
	}

	public void setCurrentHarvestCycle(HarvestCycle currentHarvestCycle) {
		this.currentHarvestCycle = currentHarvestCycle;
	}

	public List<HarvestCycle> getPreviousHarvestCycles() {
		return previousHarvestCycles;
	}

	public void setPreviousHarvestCycles(List<HarvestCycle> previousHarvestCycles) {
		this.previousHarvestCycles = previousHarvestCycles;
	}

	public Instant getCreated() {
		return created;
	}

	public void setCreated(Instant created) {
		this.created = created;
	}

	public Instant getModified() {
		return modified;
	}

	public void setModified(Instant modified) {
		this.modified = modified;
	}
}
