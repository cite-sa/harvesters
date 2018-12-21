package gr.cite.harvester.openaire;

import gr.cite.femme.client.FemmeException;
import gr.cite.harvester.core.Harvestable;
import gr.cite.harvester.datastore.model.Harvest;
import gr.cite.harvester.datastore.model.HarvestCycle;
import gr.cite.harvester.datastore.model.HarvestType;
import gr.cite.harvester.datastore.model.Status;
import gr.cite.harvester.datastore.mongodb.HarvesterDatastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OpenAireHarvestable implements Harvestable {
	private static final Logger logger = LoggerFactory.getLogger(OpenAireHarvestable.class);
	
	private OpenAireHarvester openAireHarvester;
	private HarvesterDatastore harvesterDatastore;
	private Harvest harvest;
	private OpenAireAdapter openAireAdapter;
	
	
	@Inject
	public OpenAireHarvestable(HarvesterDatastore harvesterDatastore, OpenAireAdapter openAireAdapter) {
		this.harvesterDatastore = harvesterDatastore;
		this.openAireAdapter = openAireAdapter;
	}
	
	@Override
	public HarvesterDatastore getHarvesterDatastore() {
		return this.harvesterDatastore;
	}
	
	public Harvest getHarvest() {
		return this.harvest;
	}
	
	public void setHarvest(Harvest harvest) {
		this.harvest = harvest;
		this.openAireHarvester = new OpenAireHarvester(harvest.getEndpoint());
	}
	
	
	@Override
	public Harvest harvest() throws FemmeException {
		String importId;
		String collectionId;
		
		ExecutorService executor = Executors.newFixedThreadPool(15);
		
		try {
			OpenAireHarvester openAireHarvester = new OpenAireHarvester(this.harvest.getEndpoint());
			
			importId = this.openAireAdapter.beginImport(this.harvest.getEndpointAlias(), this.harvest.getEndpoint());
			
			collectionId = this.openAireAdapter.importCollection(importId, this.harvest.getEndpoint(), this.harvest.getEndpointAlias());
			
			AtomicInteger harvestedElements = new AtomicInteger(0);
			AtomicInteger total = new AtomicInteger(0);
			HarvestCycle countElementsHarvestCycle = this.harvest.getCurrentHarvestCycle();
			
			Iterator<List<String>> fileRecordIterator = openAireHarvester.getAllFiles();
			do {
				List<Future<String>> futures = new ArrayList<>();
				List<String> fileRecordBatch = fileRecordIterator.next();
				
				for (String record: fileRecordBatch) {
					futures.add(executor.submit(new StoreFileRecordCallable(this.openAireAdapter, importId, collectionId, record)));
				}
				
				for (Future<String> future : futures) {
					synchronized (this) {
						try {
							String recordId = future.get();
							
							total.incrementAndGet();
							if (recordId != null) {
								countElementsHarvestCycle.incrementNewElements();
							} else {
								countElementsHarvestCycle.incrementUpdatedElements();
							}
							
							logger.info("Record " + recordId + " added to collection " + collectionId);
						} catch (InterruptedException | ExecutionException e) {
							countElementsHarvestCycle.incrementFailedElements();
							logger.error(e.getMessage(), e);
						} finally {
							countElementsHarvestCycle.incrementTotalElements();
							harvestedElements.incrementAndGet();
						}
						
						if (harvestedElements.compareAndSet(50, 0)) {
							this.harvest = this.harvesterDatastore.updateHarvestedCyCle(harvest.getId(), countElementsHarvestCycle);
						} else if (harvestedElements.get() > 50) {
							harvestedElements.set(0);
							this.harvest = this.harvesterDatastore.updateHarvestedCyCle(harvest.getId(), countElementsHarvestCycle);
						}
					}
				}
				
			} while (fileRecordIterator.hasNext() && Status.RUNNING.equals(this.harvest.getStatus()));
			
			this.harvest = this.harvesterDatastore.updateHarvestedCyCle(harvest.getId(), countElementsHarvestCycle);
			this.openAireAdapter.endImport(importId);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new FemmeException(e.getMessage(), e);
		} finally {
			executor.shutdown();
		}
		
		return this.harvest;
	}
	
	@Override
	public HarvestType supports() {
		return HarvestType.OPENAIRE;
	}
}
