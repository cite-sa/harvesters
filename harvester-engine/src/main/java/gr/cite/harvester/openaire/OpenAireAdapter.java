package gr.cite.harvester.openaire;

import gr.cite.commons.utils.xml.XMLConverter;
import gr.cite.commons.utils.xml.XPathEvaluator;
import gr.cite.commons.utils.xml.exceptions.XMLConversionException;
import gr.cite.commons.utils.xml.exceptions.XPathEvaluationException;
import gr.cite.femme.client.FemmeException;
import gr.cite.femme.client.api.FemmeClientAPI;
import gr.cite.femme.core.model.Collection;
import gr.cite.femme.core.model.DataElement;
import gr.cite.femme.core.model.ElementType;
import gr.cite.femme.core.model.Metadatum;
import gr.cite.harvester.core.Adapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.xml.xpath.XPathFactoryConfigurationException;
import java.util.Collections;

@Component
public class OpenAireAdapter extends Adapter {
	private static final Logger logger = LoggerFactory.getLogger(OpenAireAdapter.class);
	
	@Inject
	public OpenAireAdapter(FemmeClientAPI femmeClient) {
		super(femmeClient);
	}
	
	public String importCollection(String importId, String endpoint, String name) throws FemmeException {
		Collection collection = new Collection();
		
		collection.setEndpoint("https://www.openaire.eu/");
		collection.setName(name);
		
		return this.getFemmeClient().importCollection(importId, collection);
	}
	
	public String importRecord(String importId, String collectionId, String record) throws FemmeException, XMLConversionException, XPathFactoryConfigurationException, XPathEvaluationException {
		DataElement dataElement = new DataElement();
		
		dataElement.setType(ElementType.OPENAIRE);
		dataElement.setName(OpenAireRecordUtils.getObjectIdentifier(record));
		
		XPathEvaluator evaluator = new XPathEvaluator(XMLConverter.stringToNode(record));
		
		Metadatum metadatum = new Metadatum();
		metadatum.setContentType(MediaType.APPLICATION_XML);
		metadatum.setValue(record);
		
		dataElement.setMetadata(Collections.singletonList(metadatum));
		
		String dataElementId = this.getFemmeClient().importInCollection(importId, dataElement);
		
		dataElement.setId(dataElementId);
		
		return dataElementId;
	}
	
}
