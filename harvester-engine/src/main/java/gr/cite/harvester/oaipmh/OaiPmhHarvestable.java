package gr.cite.harvester.oaipmh;

import gr.cite.femme.client.FemmeException;
import gr.cite.harvester.core.Harvestable;
import gr.cite.harvester.datastore.model.Harvest;
import gr.cite.harvester.datastore.model.HarvestCycle;
import gr.cite.harvester.datastore.mongodb.HarvesterDatastore;
import gr.cite.commons.oaipmh.harvester.OaiPmhHarvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OaiPmhHarvestable implements Harvestable {
	private static final Logger logger = LoggerFactory.getLogger(OaiPmhHarvestable.class);
	
	private OaiPmhHarvester oaiPmhHarvester;
	private HarvesterDatastore harvesterDatastore;
	private Harvest harvest;
	private OaiPmhAdapter oaiPmhAdapter;
	
	private String metadataPrefix;
	
	@Inject
	public OaiPmhHarvestable(HarvesterDatastore harvesterDatastore, OaiPmhAdapter oaiPmhAdapter) {
		this.harvesterDatastore = harvesterDatastore;
		this.oaiPmhAdapter = oaiPmhAdapter;
	}
	
	/*@Override
	public HarvesterDatastore getHarvesterDatastore() {
		return this.harvesterDatastore;
	}

	@Override
	public void setHarvesterDatastore(HarvesterDatastore harvesterDatastore) {
		this.harvesterDatastore = harvesterDatastore;
	}*/
	
	@Override
	public HarvesterDatastore getHarvesterDatastore() {
		return this.harvesterDatastore;
	}
	
	public Harvest getHarvest() {
		return this.harvest;
	}
	
	public void setHarvest(Harvest harvest) {
		this.harvest = harvest;
		this.oaiPmhHarvester =  new OaiPmhHarvester(harvest.getEndpoint());
	}
	
	public void setMetadataPrefix(String metadataPrefix) {
		this.metadataPrefix = metadataPrefix;
	}
	
	@Override
	public Harvest harvest() throws FemmeException {
		String importId;
		String setId;
		ExecutorService executor = Executors.newFixedThreadPool(15);
		
		try {
			OaiPmhHarvester oaiPmhHarvester = new OaiPmhHarvester(this.harvest.getEndpoint());
			this.metadataPrefix = "oai_dc";
			
			List<String> recordIdentifiers = oaiPmhHarvester.listIdentifiers(this.metadataPrefix);
			
			importId = this.oaiPmhAdapter.beginImport(this.harvest.getEndpointAlias(), this.harvest.getEndpoint());
			
			//collectionId = this.wcsAdapter.insertServer(this.harvest.getEndpoint(), this.harvest.getEndpointAlias(), getCapabilities);
			setId = this.oaiPmhAdapter.importSet(importId, this.harvest.getEndpoint(), this.harvest.getEndpointAlias(), "");

			List<Future<String>> futures = new ArrayList<>();

			logger.info("Total coverages to be inserted: " + recordIdentifiers.size());
			
			for (String recordIdentifier: recordIdentifiers) {
				futures.add(executor.submit(new RetrieveAndStoreOaiPmhRecordCallable(this.oaiPmhHarvester, this.oaiPmhAdapter, importId, setId, recordIdentifier, this.metadataPrefix)));
			}

			HarvestCycle countElementsHarvestCycle = this.harvest.getCurrentHarvestCycle();

			AtomicInteger harvestedElements = new AtomicInteger(0);
			AtomicInteger total = new AtomicInteger(0);
			
			for (Future<String> future : futures) {
				synchronized (this) {
					try {
						String coverageId = future.get();

						total.incrementAndGet();
						if (coverageId != null) {
							countElementsHarvestCycle.incrementNewElements();
						} else {
							countElementsHarvestCycle.incrementUpdatedElements();
						}

						logger.info("Coverage " + coverageId + " added to server " + setId);
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
			
			this.harvest = this.harvesterDatastore.updateHarvestedCyCle(harvest.getId(), countElementsHarvestCycle);
			this.oaiPmhAdapter.endImport(importId);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new FemmeException(e.getMessage(), e);
		} finally {
			executor.shutdown();
		}

		return this.harvest;
	}
}
