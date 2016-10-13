package gr.cite.earthserver.harvester.datastore.model;

public enum Status {
	PENDING("pending"),
	RUNNING("running"),
	FINISHED("finished"),
	ERROR("error");
	
	private String status;
	
	private Status(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
}
