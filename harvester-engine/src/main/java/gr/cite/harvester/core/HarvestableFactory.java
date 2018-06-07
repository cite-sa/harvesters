package gr.cite.harvester.core;

import gr.cite.harvester.datastore.model.HarvestType;
import gr.cite.harvester.oaipmh.OaiPmhHarvestable;
import gr.cite.harvester.obis.ObisHarvestable;
import gr.cite.harvester.wcs.WCSHarvestable;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class HarvestableFactory implements ApplicationContextAware {
	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public Harvestable get(HarvestType type) {
		if (Objects.isNull(type)) throw new IllegalArgumentException("Harvest type must be defined.");
		switch (type) {
			case WCS:
				return this.applicationContext.getBean(WCSHarvestable.class);
			case OAIPMH:
				return this.applicationContext.getBean(OaiPmhHarvestable.class);
			case OBIS:
				return this.applicationContext.getBean(ObisHarvestable.class);
			default:
				throw new IllegalArgumentException("[" + type + "] is not a supported harvest type");
		}
	}
}
