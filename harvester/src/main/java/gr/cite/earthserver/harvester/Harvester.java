package gr.cite.earthserver.harvester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Harvester {
	private static final Logger logger = LoggerFactory.getLogger(Harvester.class);

	private Map<Harvestable, ScheduledExecutorService> harvestables;

	public Harvester(Map<Harvestable, ScheduledExecutorService> harvestables) {
		this.harvestables = harvestables;
	}

	public Harvester() {
		this(new HashMap<Harvestable, ScheduledExecutorService>());
	}

	/**
	 * 
	 * @param harvestable
	 *            it is recommended to implement an {@code equals} and
	 *            {@code hash} method in the implementation of
	 *            {@linkplain Harvestable}
	 */
	public void register(Harvestable harvestable) {
		harvestables.put(harvestable, null);
	}

	/**
	 * registers a {@link Harvestable} for independent periodic execution,
	 * according to it's given {@link Schedule}
	 * 
	 * @param harvestable
	 *            it is recommended to implement an {@code equals} and
	 *            {@code hash} method in the implementation of
	 *            {@linkplain Harvestable}
	 * @param schedule
	 */
	public void register(Harvestable harvestable, Schedule schedule) {

		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

		executorService.scheduleAtFixedRate(new HarvestableTask(harvestable), schedule.getInitialDelay(),
				schedule.getPeriod(), schedule.getTimeUnit());

		harvestables.put(harvestable, executorService);
	}

	public void unregister(Harvestable harvestable) {

		if (harvestables.containsKey(harvestable)) {

			ScheduledExecutorService executorService = harvestables.get(harvestable);

			if (executorService != null) {
				executorService.shutdown();
			}

			harvestables.remove(harvestable);

		} else {
			logger.warn("harvestable " + harvestable.toString() + " was not found in harvester.");
		}
	}

	/**
	 * harvest now, all registered {@link Harvestable harvestables}
	 */
	public void harvest() {
		logger.info("Harvesting all registered Harvestable sources...");

		List<Future<String>> futures = new ArrayList<Future<String>>();
		ExecutorService executor = Executors.newFixedThreadPool(5);

		for (Harvestable harvestable : harvestables.keySet()) {
			futures.add(executor.submit(new Callable<String>() {

				@Override
				public String call() throws Exception {
					return harvestable.harvest();
				}
			}));
		}
		executor.shutdown();

		for (Future<String> future : futures) {
			try {
				String collectionId = future.get();
				logger.info("Collection " + collectionId + " successfully harvested");
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			} catch (ExecutionException e) {
				logger.error(e.getMessage(), e);
			}
		}

		logger.info("Harvested all registered Harvestable sources");

	}

}
