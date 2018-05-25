package gr.cite.commons.oaipmh.harvester;

import java.util.List;

public interface Harvester {
	/**
	 * 
	 * @param metadataPrefix
	 * @return list of record elements
	 */
	List<String> listRecords(String metadataPrefix);

	/**
	 * 
	 * @param metadataPrefix
	 * @return list of identifiers
	 */
	List<String> listIdentifiers(String metadataPrefix);

	/**
	 * 
	 * @param metadataPrefix
	 * @param set
	 * @return list of record elements
	 */
	List<String> listRecords(String metadataPrefix, String set);

	/**
	 * 
	 * @param metadataPrefix
	 * @param set
	 * @return list of identifiers
	 */
	List<String> listIdentifiers(String metadataPrefix, String set);

	/**
	 * 
	 * @return list of setSpecs
	 */
	List<String> listSets();
	
	List<String> listMetadataPrefixes();
	
	String getRecord(String id, String metadataPrefix);
}
