package gr.cite.harvester.wcs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.StampedLock;

import javax.inject.Inject;

import gr.cite.harvester.datastore.model.HarvestCycle;
import gr.cite.harvester.datastore.mongodb.HarvesterDatastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.harvester.core.Harvestable;
import gr.cite.harvester.datastore.model.Harvest;
import gr.cite.earthserver.wcs.adapter.api.WCSAdapterAPI;
import gr.cite.earthserver.wcs.core.WCSRequestBuilder;
import gr.cite.earthserver.wcs.core.WCSResponse;
import gr.cite.earthserver.wcs.utils.WCSParseUtils;
import gr.cite.femme.client.FemmeException;

public class WCSHarvestable implements Harvestable {
	private static final Logger logger = LoggerFactory.getLogger(WCSHarvestable.class);

	private HarvesterDatastore harvesterDatastore;
	private Harvest harvest;
	private WCSAdapterAPI wcsAdapter;

	/*@Inject
	public WCSHarvestable(WCSAdapterAPI wcsAdapter) {
		this.wcsAdapter = wcsAdapter;
	}*/
	
	@Inject
	public void setWcsAdapter(WCSAdapterAPI wcsAdapter) {
		this.wcsAdapter = wcsAdapter;
	}

	@Override
	public HarvesterDatastore getHarvesterDatastore() {
		return this.harvesterDatastore;
	}

	@Override
	public void setHarvesterDatastore(HarvesterDatastore harvesterDatastore) {
		this.harvesterDatastore = harvesterDatastore;
	}

	public Harvest getHarvest() {
		return this.harvest;
	}
	
	public void setHarvest(Harvest harvest) {
		this.harvest = harvest;
	}
	
	@Override
	public Harvest harvest() throws FemmeException {
		String importId;
		String serverId;
		ExecutorService executor = Executors.newFixedThreadPool(15);
		
		try {
			WCSRequestBuilder wcsRequestBuilder = new WCSRequestBuilder().endpoint(this.harvest.getEndpoint());
			WCSResponse getCapabilities = wcsRequestBuilder.getCapabilities().build().get();
			List<String> coverageIds = WCSParseUtils.getCoverageIds(getCapabilities.getResponse());

			importId = this.wcsAdapter.beginImport(this.harvest.getEndpointAlias(), this.harvest.getEndpoint());

			//collectionId = this.wcsAdapter.insertServer(this.harvest.getEndpoint(), this.harvest.getEndpointAlias(), getCapabilities);
			serverId = this.wcsAdapter.importServer(importId, this.harvest.getEndpoint(), this.harvest.getEndpointAlias(), getCapabilities);

			List<Future<String>> futures = new ArrayList<>();

			logger.info("Total coverages to be inserted: " + coverageIds.size());
			
			for (String coverageId : coverageIds) {
				futures.add(executor.submit(new RetrieveAndStoreCoverageCallable(wcsRequestBuilder, this.wcsAdapter, importId, serverId, coverageId)));
//				WCSResponse describeCoverage = wcsRequestBuilder.describeCoverage().coverageId(coverageId).build().get();
//				femmeClient.addToCollection(WCSFemmeMapper.fromCoverage(describeCoverage), collectionId);
			}

			HarvestCycle countElementsHarvestCycle = this.harvest.getCurrentHarvestCycle();

			AtomicInteger harvestedElements = new AtomicInteger(0);
			AtomicInteger total = new AtomicInteger(0);
			
			//StampedLock lock = new StampedLock();
			for (Future<String> future : futures) {
				//long readStamp = lock.readLock();
				synchronized (this) {
					try {
						String coverageId = future.get();

						total.incrementAndGet();
						if (coverageId != null) {
							countElementsHarvestCycle.incrementNewElements();
						} else {
							countElementsHarvestCycle.incrementUpdatedElements();
						}

						logger.info("Coverage " + coverageId + " added to server " + serverId);
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

				//long writeStamp = lock.writeLock();
				/*synchronized (this) {
					if (harvestedElements.compareAndSet(50, 0)) {
						this.harvest = this.harvesterDatastore.updateHarvestedCyCle(harvest.getId(), countElementsHarvestCycle);
					} else if (harvestedElements.get() > 50) {
						harvestedElements.set(0);
						this.harvest = this.harvesterDatastore.updateHarvestedCyCle(harvest.getId(), countElementsHarvestCycle);
					}
				}*/
				//lock.unlock(writeStamp);
			}
			System.out.println("TOTAL ARE: " + total);
			this.harvest = this.harvesterDatastore.updateHarvestedCyCle(harvest.getId(), countElementsHarvestCycle);
			this.wcsAdapter.endImport(importId);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new FemmeException(e.getMessage(), e);
		} finally {
			executor.shutdown();
		}
		/*} catch (WCSRequestException e) {
			logger.error(e.getMessage(), e);

		} catch (ParseException e1) {
			logger.error(e1.getMessage(), e1);
		} catch (ProcessingException e2) {
			logger.error(e2.getMessage(), e2);
		}*/
		return this.harvest;
	}
}
