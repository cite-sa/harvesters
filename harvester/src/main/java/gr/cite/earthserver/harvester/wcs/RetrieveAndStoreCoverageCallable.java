package gr.cite.earthserver.harvester.wcs;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.earthserver.wcs.adaper.api.WCSAdapterAPI;
import gr.cite.earthserver.wcs.core.WCSRequest;
import gr.cite.earthserver.wcs.core.WCSRequestBuilder;
import gr.cite.earthserver.wcs.core.WCSRequestException;
import gr.cite.earthserver.wcs.core.WCSResponse;
import gr.cite.earthserver.wcs.utils.ParseException;
import gr.cite.femme.client.FemmeDatastoreException;

public class RetrieveAndStoreCoverageCallable implements Callable<String>{
	private static final Logger logger = LoggerFactory.getLogger(RetrieveAndStoreCoverageCallable.class);
	
	private WCSRequestBuilder wcsRequestBuilder;
	
	private WCSAdapterAPI adapter;
	
	private String collectionId;
	
	private String coverageId;
	
	public RetrieveAndStoreCoverageCallable(
			WCSRequestBuilder wcsRequestBuilder,
			WCSAdapterAPI adapter,
			String collectionId,
			String coverageId) {
		this.wcsRequestBuilder = wcsRequestBuilder;
		this.adapter = adapter;
		this.collectionId = collectionId;
		this.coverageId = coverageId;
	}
		
	@Override
	public String call() throws FemmeDatastoreException, WCSRequestException, ParseException {
		WCSResponse describeCoverage = null;
		describeCoverage = wcsRequestBuilder.describeCoverage().coverageId(coverageId).build().get();
	
		if(collectionId != null) {
			return adapter.addCoverage(describeCoverage, collectionId);
		} else {
			return adapter.insertCoverage(describeCoverage);
		}
	}
	
}
