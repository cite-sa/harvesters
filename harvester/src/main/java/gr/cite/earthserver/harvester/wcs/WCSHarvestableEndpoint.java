package gr.cite.earthserver.harvester.wcs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.earthserver.harvester.Harvestable;
import gr.cite.earthserver.wcs.client.WCSRequestBuilder;
import gr.cite.earthserver.wcs.client.WCSRequestException;
import gr.cite.earthserver.wcs.client.WCSResponse;
import gr.cite.earthserver.wcs.femme.client.WCSFemmeClient;
import gr.cite.earthserver.wcs.utils.ParseException;
import gr.cite.earthserver.wcs.utils.WCSParseUtils;
import gr.cite.femme.client.FemmeClient;
import gr.cite.femme.core.Collection;
import gr.cite.femme.core.DataElement;
import gr.cite.femme.datastore.exceptions.DatastoreException;

public class WCSHarvestableEndpoint implements Harvestable {
	private static final Logger logger = LoggerFactory.getLogger(WCSHarvestableEndpoint.class);

	private UUID id;
	
	private String endpoint;

	private FemmeClient femmeClient;
	
	public WCSHarvestableEndpoint(FemmeClient femmeClient) {
		this.femmeClient = femmeClient;
	}
	
	public WCSHarvestableEndpoint(String endpoint, FemmeClient femmeClient) {
		this.endpoint = endpoint;
		this.femmeClient = femmeClient;
	}
	
	@Override
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	@Override
	public String getEndpoint() {
		return endpoint;
	}

	@Override
	public void harvest() throws DatastoreException {
		try {
			WCSRequestBuilder wcsRequestBuilder = new WCSRequestBuilder().endpoint(endpoint);
			
			WCSResponse getCapabilities = wcsRequestBuilder.getCapabilities().build().get();
			List<String> coverageIds = WCSParseUtils.getCoverageIds(getCapabilities.getResponse());
			Collection serverCollection = WCSFemmeClient.toCollection(getCapabilities);
			String collectionId = femmeClient.insert(serverCollection);

			for (String id : coverageIds) {
				WCSResponse describeCoverage = wcsRequestBuilder.describeCoverage().coverageId(id).build().get();
				DataElement coverageDataElement = WCSFemmeClient.toDataElement(describeCoverage);
				femmeClient.addToCollection(coverageDataElement, collectionId);
			}

		} catch (WCSRequestException e) {
			logger.error(e.getMessage(), e);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}
