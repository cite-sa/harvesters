package gr.cite.earthserver.harvester.datastore.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class Harvest {

	private enum StatusType
	{
		Pending,
		Running,
		Finished,
		Error
	}
	
	private String id;
	
	private String endpoint;
	
	private String description;
	
	private Date startTime;

	private Date endTime;
	
	private StatusType status;
	
	public Harvest() {
	}
	
	public Harvest(String id, String endpoint, String description) {
		this.id = id;
		this.endpoint = endpoint;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public StatusType getStatus() {
		return status;
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}

	@Override
	public String toString() {
		StringBuilder metadataBuilder = new StringBuilder();
		metadataBuilder.append("\t" + this.id);
		metadataBuilder.append("\n");
		metadataBuilder.append("\t" + this.endpoint);
		metadataBuilder.append("\n");
		metadataBuilder.append("\t" + this.description);
		metadataBuilder.append("\n");
		
		return metadataBuilder.toString();
	}
}
