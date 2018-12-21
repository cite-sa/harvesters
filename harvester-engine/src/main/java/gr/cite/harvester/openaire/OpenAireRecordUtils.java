package gr.cite.harvester.openaire;

import gr.cite.commons.utils.xml.XMLConverter;
import gr.cite.commons.utils.xml.XPathEvaluator;
import gr.cite.commons.utils.xml.exceptions.XMLConversionException;
import gr.cite.commons.utils.xml.exceptions.XPathEvaluationException;
import gr.cite.harvester.core.HarvestException;

import javax.xml.xpath.XPathFactoryConfigurationException;
import java.util.List;

public class OpenAireRecordUtils {
	
	public static List<String> getRecords(String records) throws XMLConversionException, XPathEvaluationException, XPathFactoryConfigurationException {
		XPathEvaluator evaluator = new XPathEvaluator(XMLConverter.stringToNode(records));
		return evaluator.evaluate("/records/record");
	}
	
	public static String getObjectIdentifier(String record) throws XPathEvaluationException, XMLConversionException, XPathFactoryConfigurationException, HarvestException {
		XPathEvaluator evaluator = new XPathEvaluator(XMLConverter.stringToNode(record));
		return evaluator.evaluate("/result/header/*[local-name()='objIdentifier'/text()").stream().findFirst()
				   .orElseThrow(() -> new HarvestException("Error reading object identifier"));
	}
	
}
