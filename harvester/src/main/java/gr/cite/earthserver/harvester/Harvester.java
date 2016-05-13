package gr.cite.earthserver.harvester;

import java.util.HashSet;
import java.util.Set;

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
		for (Harvestable harvestable : harvestables) {
			try {
				harvestable.harvest();
			} catch (Exception e) {
				logger.warn("failed to harvest " + harvestable.getEndpoint(), e);
			}
		}
	}

}
