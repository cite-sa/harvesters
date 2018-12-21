package gr.cite.harvester.openaire;

import gr.cite.commons.utils.xml.exceptions.XMLConversionException;
import gr.cite.commons.utils.xml.exceptions.XPathEvaluationException;
import gr.cite.femme.client.FemmeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.xpath.XPathFactoryConfigurationException;
import java.util.concurrent.Callable;

public class StoreFileRecordCallable implements Callable<String>{
	private static final Logger logger = LoggerFactory.getLogger(StoreFileRecordCallable.class);
	
	private OpenAireAdapter openAireAdapter;
	private String importId;
	private String collectionId;
	private String record;
	
	public StoreFileRecordCallable(OpenAireAdapter openAireAdapter, String importId, String collectionId, String record) {
		this.openAireAdapter = openAireAdapter;
		this.importId = importId;
		this.collectionId = collectionId;
		this.record = record;
	}
		
	@Override
	public String call() throws FemmeException, XPathEvaluationException, XMLConversionException, XPathFactoryConfigurationException {
		return this.openAireAdapter.importRecord(this.importId, this.collectionId, this.record);
	}
}
