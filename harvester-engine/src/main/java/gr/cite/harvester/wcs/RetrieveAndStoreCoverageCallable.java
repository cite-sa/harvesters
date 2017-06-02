package gr.cite.harvester.wcs;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.earthserver.wcs.adapter.api.WCSAdapterAPI;
import gr.cite.earthserver.wcs.core.WCSRequestBuilder;
import gr.cite.earthserver.wcs.core.WCSRequestException;
import gr.cite.earthserver.wcs.core.WCSResponse;
import gr.cite.earthserver.wcs.utils.ParseException;
import gr.cite.femme.client.FemmeDatastoreException;

public class RetrieveAndStoreCoverageCallable implements Callable<String>{
	
	private static final Logger logger = LoggerFactory.getLogger(RetrieveAndStoreCoverageCallable.class);
	
	private WCSRequestBuilder wcsRequestBuilder;
	private WCSAdapterAPI adapter;
	private String importId;
	private String collectionId;
	private String coverageId;
	
	public RetrieveAndStoreCoverageCallable(WCSRequestBuilder wcsRequestBuilder, WCSAdapterAPI adapter, String importId, String collectionId, String coverageId) {
		this.wcsRequestBuilder = wcsRequestBuilder;
		this.adapter = adapter;
		this.importId = importId;
		this.collectionId = collectionId;
		this.coverageId = coverageId;
	}
		
	@Override
	public String call() throws FemmeDatastoreException, WCSRequestException, ParseException {
		WCSResponse describeCoverage = wcsRequestBuilder.describeCoverage().coverageId(coverageId).build().get();
		return this.adapter.importCoverage(this.importId, describeCoverage);

		/*if (collectionId != null) {
			logger.info("CoverageId to be added: " + coverageId);
			return adapter.addCoverage(describeCoverage, collectionId);
		} else {
			logger.info("CoverageId to be inserted: " + coverageId);
			return adapter.insertCoverage(describeCoverage);
		}*/
	}
}
