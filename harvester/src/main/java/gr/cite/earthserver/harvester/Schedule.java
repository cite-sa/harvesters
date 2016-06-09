package gr.cite.earthserver.harvester;

import java.util.concurrent.TimeUnit;

public class Schedule {

	private final long initialDelay;
	private final long period;
	private final TimeUnit timeUnit;

	public Schedule(long initialDelay, long period, TimeUnit timeUnit) {
		super();
		this.initialDelay = initialDelay;
		this.period = period;
		this.timeUnit = timeUnit;
	}
	
	public Schedule(long period, TimeUnit timeUnit) {
		this(0, period, timeUnit);
	}

	public long getInitialDelay() {
		return initialDelay;
	}

	public long getPeriod() {
		return period;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}
}
