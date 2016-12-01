/*package gr.cite.earthserver.harvester.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.earthserver.harvester.wcs.WCSHarvestable;

class HarvestableTask implements Runnable {

	private final static Logger logger = LoggerFactory.getLogger(HarvestableTask.class);

	private final Harvestable harvestable;

	public HarvestableTask(Harvestable harvestable) {
		super();
		this.harvestable = harvestable;
	}

	@Override
	public void run() {
		try {
			harvestable.harvest();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}*/
