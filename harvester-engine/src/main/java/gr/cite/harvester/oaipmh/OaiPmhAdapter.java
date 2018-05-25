package gr.cite.harvester.oaipmh;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.xml.xpath.XPathFactoryConfigurationException;
import java.util.Arrays;
import java.util.Collections;

@Component
public class OaiPmhAdapter {
	private static final Logger logger = LoggerFactory.getLogger(OaiPmhAdapter.class);
	
	private FemmeClientAPI femmeClient;
	
	@Inject
	public OaiPmhAdapter(FemmeClientAPI femmeClient) {
		this.femmeClient = femmeClient;
	}
	
	public String beginImport(String endpointAlias, String endpoint) throws FemmeException {
		return this.femmeClient.beginImport(endpointAlias, endpoint);
	}
	
	public void endImport(String importId) throws FemmeException {
		this.femmeClient.endImport(importId);
	}
	
	public String importSet(String importId, String endpoint, String name, String set) throws FemmeException {
		Collection collection = new Collection();
		
		collection.setEndpoint(endpoint);
		collection.setName(name);
		
		return this.femmeClient.importCollection(importId, collection);
	}
	
	public String importRecord(String importId, String setId, String recordName, String record) throws FemmeException, XMLConversionException, XPathFactoryConfigurationException, XPathEvaluationException {
		DataElement dataElement = new DataElement();
		
		dataElement.setType(ElementType.OAIPMH);
		dataElement.setName(recordName);
		
		XPathEvaluator evaluator = new XPathEvaluator(XMLConverter.stringToNode(record));
		dataElement.setEndpoint(buildEndpoint(evaluator));
		
		Metadatum metadatum = new Metadatum();
		metadatum.setContentType(MediaType.APPLICATION_XML);
		metadatum.setValue(getMetadata(evaluator));
		
		dataElement.setMetadata(Collections.singletonList(metadatum));
		
		String dataElementId = this.femmeClient.importInCollection(importId, dataElement);
		
		dataElement.setId(dataElementId);
		
		return dataElementId;
	}
	
	private String buildEndpoint(XPathEvaluator evaluator) throws XPathEvaluationException {
		//XPathEvaluator evaluator = new XPathEvaluator(XMLConverter.stringToNode(record));
		String verb = evaluator.evaluate("/*[local-name()='OAI-PMH']/*[local-name()='request']/@verb").stream().findFirst().orElse("");
		String identifier = evaluator.evaluate("/*[local-name()='OAI-PMH']/*[local-name()='request']/@identifier").stream().findFirst().orElse("");
		String metadataPrefix = evaluator.evaluate("/*[local-name()='OAI-PMH']/*[local-name()='request']/@metadataPrefix").stream().findFirst().orElse("");
		String requestUrl = evaluator.evaluate("/*[local-name()='OAI-PMH']/*[local-name()='request']/text()").stream().findFirst().orElse("");
		
		return requestUrl + "?verb=" + verb + "&identifier=" + identifier + "&metadataPrefix=" + metadataPrefix;
	}
	
	private String getMetadata(XPathEvaluator evaluator) throws XPathEvaluationException {
		return evaluator.evaluate("/*[local-name()='OAI-PMH']/*[local-name()='GetRecord']/*[local-name()='record']/*[local-name()='metadata']/*[local-name()='dc']").stream().findFirst().orElse("");
	}
}
