package gr.cite.earthserver.harvester.wcs;

import java.util.concurrent.Callable;

import gr.cite.earthserver.wcs.adaper.api.WCSAdapterAPI;
import gr.cite.earthserver.wcs.core.WCSRequestBuilder;
import gr.cite.earthserver.wcs.core.WCSResponse;

public class RetrieveAndStoreCoverageCallable implements Callable<String>{
	
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
	public String call() throws Exception {
		WCSResponse describeCoverage = wcsRequestBuilder.describeCoverage().coverageId(coverageId).build().get();
		
		if(collectionId != null) {
			return adapter.addCoverage(describeCoverage, collectionId);
		} else {
			return adapter.insertCoverage(describeCoverage);
		}
	}
	
}
