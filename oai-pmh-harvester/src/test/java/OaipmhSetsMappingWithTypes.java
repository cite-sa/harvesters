import java.net.URISyntaxException;
import java.util.List;

import javax.xml.xpath.XPathFactoryConfigurationException;

import gr.cite.commons.utils.xml.XMLConverter;
import gr.cite.commons.utils.xml.XPathEvaluator;
import gr.cite.commons.utils.xml.exceptions.XMLConversionException;
import gr.cite.commons.utils.xml.exceptions.XPathEvaluationException;
import gr.cite.commons.oaipmh.harvester.OaiPmhHarvester;

public class OaipmhSetsMappingWithTypes {

	OaiPmhHarvester oaiPmhHarvester = new OaiPmhHarvester(
			"http://repository.argolisculture.gr/oaiprovider-wrapper/oaiprovider/eva");
	
	public OaipmhSetsMappingWithTypes() throws URISyntaxException {
	}
//			"http://dlibrary.ascsa.edu.gr/oaiprovider-wrapper/oaiprovider/asksa");
//			"http://webapps.servers.archetai.gr:8084/oaiprovider-wrapper/oaiprovider/enathines");

	public void map() throws XPathFactoryConfigurationException, XMLConversionException, XPathEvaluationException {

		for (String set : oaiPmhHarvester.listSets()) {
			String id = oaiPmhHarvester.listIdentifiers("oai_dc", set).get(0);

			String record = oaiPmhHarvester.getRecord(id, "oai_dc");

			List<String> types = new XPathEvaluator(XMLConverter.stringToNode(record, true))
					.evaluate("//*[local-name()='type']/text()");

//			if (types.get(0).equals("Φωτογραφία")) {
//				types.remove(0);
//			}
			if (types.size() > 1) {
				System.out.println("WARNING " + set + " -> " + types);
			}

			try {
				System.out.println(set + ", " + types.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) throws XPathFactoryConfigurationException, URISyntaxException, XMLConversionException, XPathEvaluationException {
		new OaipmhSetsMappingWithTypes().map();
	}
}
