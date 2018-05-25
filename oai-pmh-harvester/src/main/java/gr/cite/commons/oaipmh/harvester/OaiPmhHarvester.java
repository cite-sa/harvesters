package gr.cite.commons.oaipmh.harvester;

import gr.cite.commons.utils.xml.XMLConverter;
import gr.cite.commons.utils.xml.XPathEvaluator;
import gr.cite.commons.utils.xml.exceptions.XMLConversionException;
import gr.cite.commons.utils.xml.exceptions.XPathEvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.xml.xpath.XPathFactoryConfigurationException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OaiPmhHarvester implements Harvester {
	private static final Logger logger = LoggerFactory.getLogger(OaiPmhHarvester.class);
	
	private final WebTarget oaipmhWebTarget;
	
	public OaiPmhHarvester(String oaiPmhEndpoint) {
		this.oaipmhWebTarget = ClientBuilder.newClient().target(oaiPmhEndpoint);
	}
	
	@Override
	public List<String> listRecords(String metadataPrefix) {
		return listRecords(metadataPrefix, null);
	}
	
	@Override
	public List<String> listIdentifiers(String metadataPrefix) {
		return listIdentifiers(metadataPrefix, null);
	}
	
	@Override
	public List<String> listRecords(String metadataPrefix, String set) {
		WebTarget verbTarget = oaipmhWebTarget.queryParam("verb", Verbs.LIST_RECORDS);
		WebTarget finalTarget = verbTarget.queryParam("metadataPrefix", metadataPrefix);
		
		if (set != null) {
			finalTarget = finalTarget.queryParam("set", set);
		}
		
		List<String> records = new ArrayList<>();
		String resumptionToken = null;
		
		do {
			String response = resumptionToken == null ?
								  finalTarget.request().get(String.class) :
								  verbTarget.queryParam("resumptionToken", resumptionToken).request().get(String.class);
			
			try {
				
				XPathEvaluator xPathEvaluator = new XPathEvaluator(XMLConverter.stringToNode(response, true));
				records.addAll(xPathEvaluator.evaluate("//*[local-name()='record']/text()"));
				resumptionToken = getResumptionToken(xPathEvaluator);
				
			} catch (XMLConversionException | XPathFactoryConfigurationException | XPathEvaluationException e) {
				logger.error(e.getMessage(), e);
			}
			
			
		} while (resumptionToken != null);
		
		return records;
	}
	
	@Override
	public List<String> listIdentifiers(String metadataPrefix, String set) {
		WebTarget verbTarget = oaipmhWebTarget.queryParam("verb", Verbs.LIST_IDENTIFIERS);
		WebTarget finalTarget = verbTarget.queryParam("metadataPrefix", metadataPrefix);
		
		if (set != null) {
			finalTarget = finalTarget.queryParam("set", set);
		}
		
		List<String> identifiers = new ArrayList<>();
		String resumptionToken = null;
		
		do {
			String response = resumptionToken == null ?
					  finalTarget.request().get(String.class) :
					  verbTarget.queryParam("resumptionToken", resumptionToken).request().get(String.class);
			
			try {
				
				XPathEvaluator xPathEvaluator = new XPathEvaluator(XMLConverter.stringToNode(response, true));
				identifiers.addAll(xPathEvaluator.evaluate("//*[local-name()='" + Verbs.LIST_IDENTIFIERS + "']//*[local-name()='identifier']/text()"));
				
				resumptionToken = getResumptionToken(xPathEvaluator);
				
			} catch (XMLConversionException | XPathFactoryConfigurationException | XPathEvaluationException e) {
				logger.error(e.getMessage(), e);
			}
			
			
		} while (resumptionToken != null);
		
		return identifiers;
	}
	
	@Override
	public List<String> listSets() {
		WebTarget verbTarget = oaipmhWebTarget.queryParam("verb", Verbs.LIST_SETS);
		List<String> sets = new ArrayList<>();
		String response = verbTarget.request().get(String.class);
		
		try {
			XPathEvaluator xPathEvaluator = new XPathEvaluator(XMLConverter.stringToNode(response, true));
			sets.addAll(xPathEvaluator.evaluate("//*[local-name()='" + Verbs.LIST_SETS + "']//*[local-name()='setSpec']/text()"));
			
		} catch (XMLConversionException | XPathFactoryConfigurationException | XPathEvaluationException e) {
			logger.error(e.getMessage(), e);
		}
		
		return sets;
	}
	
	@Override
	public List<String> listMetadataPrefixes() {
		WebTarget verbTarget = oaipmhWebTarget.queryParam("verb", Verbs.LIST_METADATA_FORMATS);
		List<String> prefixes = new ArrayList<>();
		String response = verbTarget.request().get(String.class);
		
		try {
			XPathEvaluator xPathEvaluator = new XPathEvaluator(XMLConverter.stringToNode(response, true));
			prefixes.addAll(xPathEvaluator.evaluate("//*[local-name()='" + Verbs.LIST_METADATA_FORMATS + "']//*[local-name()='metadataPrefix']/text()"));
		} catch (XMLConversionException | XPathFactoryConfigurationException | XPathEvaluationException e) {
			logger.error(e.getMessage(), e);
		}
		
		return prefixes;
	}
	
	
	@Override
	public String getRecord(String id, String metadataPrefix) {
		return oaipmhWebTarget.queryParam("verb", Verbs.GET_RECORD)
				   .queryParam("metadataPrefix", metadataPrefix).queryParam("identifier", id)
				   .request().get(String.class);
	}
	
	private String getResumptionToken(XPathEvaluator xpath) {
		List<String> resumptionToken = Collections.emptyList();
		
		try {
			resumptionToken = xpath.evaluate("//*[local-name()='resumptionToken']/text()");
		} catch (XPathEvaluationException e) {
			logger.error(e.getMessage(), e);
		}
		
		return resumptionToken.isEmpty() ? null : resumptionToken.get(0);
	}
	
	public static void main(String[] args) throws URISyntaxException {
		
		String organization = "asksa";
		
		/*Set<String> idsFromRepo = Sets.newHashSet(ClientBuilder.newClient().target(
//				"http://webapps.servers.archetai.gr:8083/repository-manager-application/manager/listObjectsIdsJSON/")
				"http://dlibrary.ascsa.edu.gr:8080/repository-manager-application/manager/listObjectsIdsJSON/")
//				"http://ip136.ip-92-222-235.eu:8082/repository-manager-application/manager/listObjectsIdsJSON")
//				"http://dev02.local.cite.gr:8082/repository-manager-application/manager/listObjectsIdsJSON/")
				.path("digital-item:set-" + organization).request().get(IdsList.class).getIds());
		System.out.println("fedora #ids = " + idsFromRepo.size());*/
		
		Harvester harvester = new OaiPmhHarvester(
//				"http://webapps.servers.archetai.gr/oaiprovider-wrapper/oaiprovider/"+ organization +"/");
//				"http://repository.argolisculture.gr/oaiprovider-wrapper/oaiprovider/"+ organization +"/");
			"http://dlibrary.ascsa.edu.gr/oaiprovider-wrapper/oaiprovider/" + organization + "/");
		
		Set<String> allIdentifiers = new HashSet<>();
		Set<String> oaiIdentifiers = new HashSet<>();
		
		for (String prefix : harvester.listMetadataPrefixes()) {
			List<String> identifiers = harvester.listIdentifiers(prefix);
			
			System.out.println(prefix + " : " + identifiers.size());
			
			allIdentifiers.addAll(identifiers);
		}
		
		System.out.println(allIdentifiers);
//
//		
//		System.out.println(Sets.difference(idsFromRepo, oai_identifiers));
//		
//		System.out.println(problematicIds);
	}
	
}
