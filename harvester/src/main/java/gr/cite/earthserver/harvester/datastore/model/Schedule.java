package gr.cite.earthserver.harvester.datastore.model;

import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class Schedule {
	
	private String id;

	@JsonProperty
	private Long period;
	
	@JsonProperty
	private ChronoUnit periodType;

	public Schedule() {
		
	}
	
	public Schedule(Long period, ChronoUnit periodType) {
		this.period = period;
		this.periodType = periodType;
	}
	
	public Schedule(String id, Long period, ChronoUnit periodType) {
		this.id = id;
		this.period = period;
		this.periodType = periodType;
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

	public ChronoUnit getPeriodType() {
		return periodType;
	}

}
