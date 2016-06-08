package gr.cite.earthserver.harvester;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Harvester {
	private static final Logger logger = LoggerFactory.getLogger(Harvester.class);

	private Set<Harvestable> harvestables;

	public Harvester(Set<Harvestable> harvestables) {
		this.harvestables = harvestables;
	}

	public Harvester() {
		this(new HashSet<Harvestable>());
	}

	public void register(Harvestable harvestable) {
		harvestables.add(harvestable);
	}

	public void unregister(Harvestable harvestable) {
		harvestables.remove(harvestable);
	}

	public void harvest() {
		List<Future<String>> futures = new ArrayList<Future<String>>();
		ExecutorService executor = Executors.newFixedThreadPool(3);

		for (Harvestable harvestable : harvestables) {
			futures.add(executor.submit(new Callable<String>() {

				@Override
				public String call() throws Exception {
					return harvestable.harvest();
				}
			}));
		}
		executor.shutdown();
		
		for(Future<String> future : futures) {
			try {
				String collectionId = future.get();
				logger.info("Collection " + collectionId + " successfully harvested");
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			} catch (ExecutionException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}
