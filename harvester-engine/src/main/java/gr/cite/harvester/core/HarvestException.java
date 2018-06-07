package gr.cite.harvester.core;

public class HarvestException extends Exception {
	private static final long serialVersionUID = 1474149125975041240L;
	
	public HarvestException() {
		super();
	}
	
	public HarvestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public HarvestException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public HarvestException(String message) {
		super(message);
	}
	
	public HarvestException(Throwable cause) {
		super(cause);
	}
}
