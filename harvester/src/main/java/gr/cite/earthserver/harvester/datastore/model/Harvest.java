package gr.cite.earthserver.harvester.datastore.model;

import java.time.Instant;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class Harvest {

	@JsonProperty
	private String id;
	
	@JsonProperty
	private String endpoint;
	
	@JsonProperty
	private String endpointAlias;
	
	@JsonProperty
	private Instant startTime;

	@JsonProperty
	private Instant endTime;
	
	@JsonProperty
	private Schedule schedule;
	
	@JsonProperty
	private Status status;
	
	public Harvest() {
		this.id = new ObjectId().toString();
		this.status = Status.PENDING;
	}
	
	public Harvest(String endpoint, String endpointAlias, Schedule schedule) {
		this.id = new ObjectId().toString();
		this.endpoint = endpoint;
		this.endpointAlias = endpointAlias;
		this.schedule = schedule;
		this.status = Status.PENDING;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Instant getStartTime() {
		return startTime;
	}

	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}

	public Instant getEndTime() {
		return endTime;
	}

	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
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
}
