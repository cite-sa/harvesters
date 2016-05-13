package gr.cite.earthserver.harvester.wcs;

import java.util.List;

import gr.cite.earthserver.harvester.Harvestable;
import gr.cite.earthserver.wcs.client.WCSRequestBuilder;
import gr.cite.earthserver.wcs.client.WCSRequestException;
import gr.cite.femme.core.Collection;
import gr.cite.femme.core.DataElement;
import gr.cite.femme.datastore.exceptions.DatastoreException;

public class WCSHarvestableEndpoint implements Harvestable {

	private String endpoint;

	private FemmeClient femmeClient;

	@Override
	public void harvest() throws Exception {
		try {
			WCSRequestBuilder wcsRequestBuilder = new WCSRequestBuilder().endpoint(endpoint);
			String getCapabilities = wcsRequestBuilder.getCapabilities().build().get();

			List<String> coverageIds = WCSParseUtils.parseGetCapabilities(getCapabilities);

			Collection serverCollection = getCollection();

			for (String id : coverageIds) {
				String describeCoverage = wcsRequestBuilder.describeCoverage().coverageId(id).build().get();

				DataElement coverageDataElement = FemmeClient.coverageToDataElement(describeCoverage);

				coverageDataElement.addCollection(serverCollection);

				femmeClient.store(coverageDataElement);
			}

		} catch (WCSRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getEndpoint() {
		return endpoint;
	}

	private Collection getCollection() throws DatastoreException {

		Collection collection = femmeClient.getCollection(endpoint);

		if (collection == null) {
			collection = FemmeClient.serverToCollection(endpoint);
			femmeClient.store(collection);
		}

		return collection;
	}
}
