package gr.cite.harvester.oaipmh;

import gr.cite.commons.utils.xml.exceptions.XMLConversionException;
import gr.cite.commons.utils.xml.exceptions.XPathEvaluationException;
import gr.cite.earthserver.wcs.core.WCSRequestException;
import gr.cite.earthserver.wcs.utils.ParseException;
import gr.cite.femme.client.FemmeException;
import gr.cite.commons.oaipmh.harvester.OaiPmhHarvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.xpath.XPathFactoryConfigurationException;
import java.util.concurrent.Callable;

public class RetrieveAndStoreOaiPmhRecordCallable implements Callable<String>{
	private static final Logger logger = LoggerFactory.getLogger(RetrieveAndStoreOaiPmhRecordCallable.class);
	
	private OaiPmhHarvester oaiPmhHarvester;
	private OaiPmhAdapter oaiPmhAdapter;
	private String importId;
	private String setId;
	private String recordId;
	private String metadataPrefix;
	
	public RetrieveAndStoreOaiPmhRecordCallable(OaiPmhHarvester oaiPmhHarvester, OaiPmhAdapter oaiPmhAdapter, String importId, String setId, String recordId, String metadataPrefix) {
		this.oaiPmhHarvester = oaiPmhHarvester;
		this.oaiPmhAdapter = oaiPmhAdapter;
		this.importId = importId;
		this.setId = setId;
		this.recordId = recordId;
		this.metadataPrefix = metadataPrefix;
	}
		
	@Override
	public String call() throws FemmeException, XPathEvaluationException, XMLConversionException, XPathFactoryConfigurationException {
		String record = this.oaiPmhHarvester.getRecord(this.recordId, this.metadataPrefix);
		return this.oaiPmhAdapter.importRecord(this.importId, this.setId, this.recordId, record);
	}
}
