package gr.cite.earthserver.harvester;

import java.util.HashSet;
import java.util.Set;

public class Harvester {

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
			harvestable.harvest();
		}
	}

}
