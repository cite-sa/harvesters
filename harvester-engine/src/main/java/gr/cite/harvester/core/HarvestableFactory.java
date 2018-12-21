package gr.cite.harvester.core;

import gr.cite.harvester.datastore.model.HarvestType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class HarvestableFactory {
	private final Map<HarvestType, Harvestable> harvestables;
	
	public HarvestableFactory(List<Harvestable> harvestables) {
		this.harvestables = harvestables.stream().collect(Collectors.toMap(Harvestable::supports, Function.identity()));
	}
	
	/*@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}*/
	
	public Harvestable get(HarvestType type) {
		if (Objects.isNull(type)) throw new IllegalArgumentException("Harvest type must be defined.");
		return Optional.ofNullable(this.harvestables.get(type))
				   .orElseThrow(() -> new IllegalArgumentException("[" + type + "] is not a supported harvest type"));
	}
}
