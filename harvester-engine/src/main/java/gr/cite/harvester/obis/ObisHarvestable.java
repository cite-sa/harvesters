package gr.cite.harvester.obis;

import gr.cite.femme.client.FemmeException;
import gr.cite.harvester.core.Harvestable;
import gr.cite.harvester.datastore.model.Harvest;
import gr.cite.harvester.datastore.model.HarvestCycle;
import gr.cite.harvester.datastore.model.Status;
import gr.cite.harvester.datastore.mongodb.HarvesterDatastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ObisHarvestable implements Harvestable {
	private static final Logger logger = LoggerFactory.getLogger(ObisHarvestable.class);
	private static final long AREAID = 89;
	
	private HarvesterDatastore harvesterDatastore;
	private ObisAdapter obisAdapter;
	private Harvest harvest;
	
	public ObisHarvestable(HarvesterDatastore harvesterDatastore, ObisAdapter obisAdapter) {
		this.harvesterDatastore = harvesterDatastore;
		this.obisAdapter = obisAdapter;
	}
	
	@Override
	public HarvesterDatastore getHarvesterDatastore() {
		return null;
	}
	
	@Override
	public Harvest getHarvest() {
		return this.harvest;
	}
	
	@Override
	public void setHarvest(Harvest harvest) {
		this.harvest = harvest;
	}
	
	@Override
	public Harvest harvest() throws FemmeException {
		String importId;
		String collectionId;
		
		ExecutorService executor = Executors.newFixedThreadPool(15);
		
		try {
			ObisHarvester obisHarvester = new ObisHarvester(this.harvest.getEndpoint());
			
			importId = this.obisAdapter.beginImport(this.harvest.getEndpointAlias(), this.harvest.getEndpoint());
			
			collectionId = this.obisAdapter.importCollection(importId, this.harvest.getEndpoint(), this.harvest.getEndpointAlias());
			
			AtomicInteger harvestedElements = new AtomicInteger(0);
			AtomicInteger total = new AtomicInteger(0);
			HarvestCycle countElementsHarvestCycle = this.harvest.getCurrentHarvestCycle();
			
			//Iterator<List<Map<String, Object>>> occurrencesIterator = obisHarvester.getOccurencesByArea(AREAID);
			Iterator<List<Map<String, Object>>> occurrencesIterator = obisHarvester.getAllOccurences();
			do {
				List<Future<String>> futures = new ArrayList<>();
				List<Map<String, Object>> occurrencesBatch = occurrencesIterator.next();
				
				for (Map<String, Object> occurrences: occurrencesBatch) {
					futures.add(executor.submit(new StoreObisOccurrenceCallable(this.obisAdapter, importId, occurrences)));
				}
				
				for (Future<String> future : futures) {
					synchronized (this) {
						try {
							String occurrenceId = future.get();
							
							total.incrementAndGet();
							if (occurrenceId != null) {
								countElementsHarvestCycle.incrementNewElements();
							} else {
								countElementsHarvestCycle.incrementUpdatedElements();
							}
							
							logger.info("Occurrence " + occurrenceId + " added to collection " + collectionId);
						} catch (InterruptedException | ExecutionException e) {
							countElementsHarvestCycle.incrementFailedElements();
							logger.error(e.getMessage(), e);
						} finally {
							countElementsHarvestCycle.incrementTotalElements();
							harvestedElements.incrementAndGet();
							//lock.unlock(readStamp);
						}
						
						if (harvestedElements.compareAndSet(50, 0)) {
							this.harvest = this.harvesterDatastore.updateHarvestedCyCle(harvest.getId(), countElementsHarvestCycle);
						} else if (harvestedElements.get() > 50) {
							harvestedElements.set(0);
							this.harvest = this.harvesterDatastore.updateHarvestedCyCle(harvest.getId(), countElementsHarvestCycle);
						}
					}
				}
				
			} while (occurrencesIterator.hasNext() && Status.RUNNING.equals(this.harvest.getStatus()));
			
			this.harvest = this.harvesterDatastore.updateHarvestedCyCle(harvest.getId(), countElementsHarvestCycle);
			this.obisAdapter.endImport(importId);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new FemmeException(e.getMessage(), e);
		} finally {
			executor.shutdown();
		}
		
		return this.harvest;
	}
}
