package gr.cite.earthserver.harvester.datastore.model;

public enum Status {
	PENDING("pending"),
	RUNNING("running"),
	FINISHED("finished"),
	STOPPED("stopped"),
	ERROR("error");

	private String status;

	private Status(String status) {
		this.status = status;
	}

	public String getStatusCode() {
		return status;
	}

	public static Status getEnum(String code) {
		switch (code) {
		case "pending":
			return Status.PENDING;
		case "running":
			return Status.RUNNING;
		case "finished":
			return Status.FINISHED;
		case "stopped":
			return Status.STOPPED;
		case "error":
			return Status.ERROR;
		default:
			return null;
		}
	}
}
