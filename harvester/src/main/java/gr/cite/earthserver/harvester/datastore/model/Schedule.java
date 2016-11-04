package gr.cite.earthserver.harvester.datastore.model;

import java.util.concurrent.TimeUnit;

public class Schedule {
	
	private String id;

	private Long period;
	
	private TimeUnit timeUnit;

	public Schedule(Long period, TimeUnit timeUnit) {
		this.period = period;
		this.timeUnit = timeUnit;
	}
	
	public Schedule(String id, Long period, TimeUnit timeUnit) {
		this.id = id;
		this.period = period;
		this.timeUnit = timeUnit;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getPeriod() {
		return period;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}
	
	
}
