package gr.cite.earthserver.harvester.datastore.model;

import java.time.temporal.ChronoUnit;

public class Schedule {
	
	private String id;

	private Long period;
	
	private ChronoUnit timeUnit;

	public Schedule(Long period, ChronoUnit timeUnit) {
		this.period = period;
		this.timeUnit = timeUnit;
	}
	
	public Schedule(String id, Long period, ChronoUnit timeUnit) {
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

	public ChronoUnit getTimeUnit() {
		return timeUnit;
	}
	
	
}
