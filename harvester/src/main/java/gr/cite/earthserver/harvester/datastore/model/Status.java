package gr.cite.earthserver.harvester.datastore.model;

public enum Status {
	PENDING("pending"), RUNNING("running"), FINISHED("finished"), ERROR("error");

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
			return PENDING;
		case "running":
			return RUNNING;
		case "finished":
			return FINISHED;
		case "error":
			return ERROR;
		default:
			return null;
		}
	}
}
