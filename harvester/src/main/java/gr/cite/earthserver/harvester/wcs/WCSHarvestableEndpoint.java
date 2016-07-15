package gr.cite.earthserver.harvester.wcs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.earthserver.harvester.Harvestable;
import gr.cite.earthserver.wcs.adaper.api.WCSAdapterAPI;
import gr.cite.earthserver.wcs.adapter.WCSAdapter;
import gr.cite.earthserver.wcs.core.WCSRequestBuilder;
import gr.cite.earthserver.wcs.core.WCSRequestException;
import gr.cite.earthserver.wcs.core.WCSResponse;
import gr.cite.earthserver.wcs.utils.ParseException;
import gr.cite.earthserver.wcs.utils.WCSParseUtils;
import gr.cite.femme.client.FemmeClient;
import gr.cite.femme.exceptions.DatastoreException;

public class WCSHarvestableEndpoint implements Harvestable {
	private static final Logger logger = LoggerFactory.getLogger(WCSHarvestableEndpoint.class);

	private UUID id;

	private String endpoint;

	private WCSAdapterAPI adapter;

	public WCSHarvestableEndpoint() {
		
	}

	public WCSHarvestableEndpoint(String endpoint, WCSAdapter adapter) {
		this.endpoint = endpoint;
		this.adapter = adapter;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	@Override
	public String getEndpoint() {
		return endpoint;
	}

	@Override
	public String harvest() throws DatastoreException {
		String collectionId = null;
		
		try {
			WCSRequestBuilder wcsRequestBuilder = new WCSRequestBuilder().endpoint(endpoint);

			WCSResponse getCapabilities = wcsRequestBuilder.getCapabilities().build().get();
			
			List<String> coverageIds = WCSParseUtils.getCoverageIds(getCapabilities.getResponse());
			
			collectionId = adapter.insertServer(endpoint, getCapabilities);

			List<Future<String>> futures = new ArrayList<Future<String>>();
			ExecutorService executor = Executors.newFixedThreadPool(40);
			
			for (String coverageId : coverageIds) {
				futures.add(executor.submit(new RetrieveAndStoreCoverageCallable(wcsRequestBuilder, adapter, collectionId, coverageId)));
				/*WCSResponse describeCoverage = wcsRequestBuilder.describeCoverage().coverageId(coverageId).build().get();
				femmeClient.addToCollection(WCSFemmeMapper.fromCoverage(describeCoverage), collectionId);*/
			}
			
			executor.shutdown();
			
			for(Future<String> future : futures) {
				try {
					future.get();
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				} catch (ExecutionException e) {
					logger.error(e.getMessage(), e);
				}
			}
			
		} catch (WCSRequestException e) {
			logger.error(e.getMessage(), e);
		} catch (ParseException e1) {
			logger.error(e1.getMessage(), e1);
		}
		
		return collectionId;
	}
}
