package gr.cite.earthserver.harvester.wcs;

import java.util.List;

import javax.xml.xpath.XPathFactoryConfigurationException;

import gr.cite.scarabaeus.utils.xml.XMLConverter;
import gr.cite.scarabaeus.utils.xml.XPathEvaluator;

public class WCSParseUtils {

	/**
	 * 
	 * @param value
	 * @return the list of coverage IDs in the specified get capabilities
	 *         response
	 * @throws ParseException 
	 */
	public static List<String> parseGetCapabilities(String getCapabilitiesXML) throws ParseException {
		try {
			XPathEvaluator xPathEvaluator = new XPathEvaluator(XMLConverter.stringToNode(getCapabilitiesXML, true));

			return xPathEvaluator.evaluate("//*[local-name='CoverageSummary']/*[local-name='CoverageId']/text()");

		} catch (XPathFactoryConfigurationException e) {
			throw new ParseException(e);
		}

	}
}
