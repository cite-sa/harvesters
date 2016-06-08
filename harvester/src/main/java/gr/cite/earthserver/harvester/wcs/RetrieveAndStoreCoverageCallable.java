package gr.cite.earthserver.harvester.wcs;

import java.util.concurrent.Callable;

import gr.cite.earthserver.wcs.client.WCSRequestBuilder;
import gr.cite.earthserver.wcs.client.WCSResponse;
import gr.cite.earthserver.wcs.femme.client.WCSFemmeClient;
import gr.cite.femme.client.FemmeClient;
import gr.cite.femme.core.DataElement;

public class RetrieveAndStoreCoverageCallable implements Callable<String>{
	
	private WCSRequestBuilder wcsRequestBuilder;
	
	private FemmeClient femmeClient;
	
	private String collectionId;
	
	private String coverageId;
	
	public RetrieveAndStoreCoverageCallable(
			WCSRequestBuilder wcsRequestBuilder,
			FemmeClient femmeClient,
			String collectionId,
			String coverageId) {
		this.wcsRequestBuilder = wcsRequestBuilder;
		this.femmeClient = femmeClient;
		this.collectionId = collectionId;
		this.coverageId = coverageId;
	}
		
	@Override
	public String call() throws Exception {
		WCSResponse describeCoverage = wcsRequestBuilder.describeCoverage().coverageId(coverageId).build().get();
		DataElement coverageDataElement = WCSFemmeClient.toDataElement(describeCoverage);
		
		if(collectionId != null) {
			return femmeClient.addToCollection(coverageDataElement, collectionId);
		} else {
			return femmeClient.insert(coverageDataElement);
		}
	}
	
	
}
