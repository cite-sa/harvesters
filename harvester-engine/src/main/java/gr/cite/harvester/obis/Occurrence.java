package gr.cite.harvester.obis;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Occurrence {
	
	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("decimalLongitude")
	private Double decimalLongitude;
	
	@JsonProperty("decimalLatitude")
	private Double decimalLatitude;
	
	@JsonProperty("eventDate")
	private String eventDate;
	
	@JsonProperty("institutionCode")
	private String institutionCode;
	
	@JsonProperty("collectionCode")
	private String collectionCode;
	
	@JsonProperty("catalogNumber")
	private String catalogNumber;
	
	@JsonProperty("individualCount")
	private Integer individualCount;
	
	@JsonProperty("datasetName")
	private String datasetName;
	
	@JsonProperty("phylum")
	private String phylum;
	
	@JsonProperty("order")
	private String order;
	
	@JsonProperty("family")
	private String family;
	
	@JsonProperty("genus")
	private String genus;
	
	@JsonProperty("scientificName")
	private String scientificName;
	
	@JsonProperty("originalScientificName")
	private String originalScientificName;
	
	@JsonProperty("scientificNameAuthorship")
	private String scientificNameAuthorship;
	
	@JsonProperty("obisID")
	private Integer obisID;
	
	@JsonProperty("resourceID")
	private Integer resourceID;
	
	@JsonProperty("yearcollected")
	private Integer yearcollected;
	
	@JsonProperty("species")
	private String species;
	
	@JsonProperty("qc")
	private Integer qc;
	
	@JsonProperty("aphiaID")
	private Integer aphiaID;
	
	@JsonProperty("speciesID")
	private Integer speciesID;
	
	@JsonProperty("scientificNameID")
	private String scientificNameID;
	
	@JsonProperty("class")
	private String clazz;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Double getDecimalLongitude() {
		return decimalLongitude;
	}
	
	public void setDecimalLongitude(Double decimalLongitude) {
		this.decimalLongitude = decimalLongitude;
	}
	
	public Double getDecimalLatitude() {
		return decimalLatitude;
	}
	
	public void setDecimalLatitude(Double decimalLatitude) {
		this.decimalLatitude = decimalLatitude;
	}
	
	public String getEventDate() {
		return eventDate;
	}
	
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
	
	public String getInstitutionCode() {
		return institutionCode;
	}
	
	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}
	
	public String getCollectionCode() {
		return collectionCode;
	}
	
	public void setCollectionCode(String collectionCode) {
		this.collectionCode = collectionCode;
	}
	
	public String getCatalogNumber() {
		return catalogNumber;
	}
	
	public void setCatalogNumber(String catalogNumber) {
		this.catalogNumber = catalogNumber;
	}
	
	public Integer getIndividualCount() {
		return individualCount;
	}
	
	public void setIndividualCount(Integer individualCount) {
		this.individualCount = individualCount;
	}
	
	public String getDatasetName() {
		return datasetName;
	}
	
	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}
	
	public String getPhylum() {
		return phylum;
	}
	
	public void setPhylum(String phylum) {
		this.phylum = phylum;
	}
	
	public String getOrder() {
		return order;
	}
	
	public void setOrder(String order) {
		this.order = order;
	}
	
	public String getFamily() {
		return family;
	}
	
	public void setFamily(String family) {
		this.family = family;
	}
	
	public String getGenus() {
		return genus;
	}
	
	public void setGenus(String genus) {
		this.genus = genus;
	}
	
	public String getScientificName() {
		return scientificName;
	}
	
	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}
	
	public String getOriginalScientificName() {
		return originalScientificName;
	}
	
	public void setOriginalScientificName(String originalScientificName) {
		this.originalScientificName = originalScientificName;
	}
	
	public String getScientificNameAuthorship() {
		return scientificNameAuthorship;
	}
	
	public void setScientificNameAuthorship(String scientificNameAuthorship) {
		this.scientificNameAuthorship = scientificNameAuthorship;
	}
	
	public Integer getObisID() {
		return obisID;
	}
	
	public void setObisID(Integer obisID) {
		this.obisID = obisID;
	}
	
	public Integer getResourceID() {
		return resourceID;
	}
	
	public void setResourceID(Integer resourceID) {
		this.resourceID = resourceID;
	}
	
	public Integer getYearcollected() {
		return yearcollected;
	}
	
	public void setYearcollected(Integer yearcollected) {
		this.yearcollected = yearcollected;
	}
	
	public String getSpecies() {
		return species;
	}
	
	public void setSpecies(String species) {
		this.species = species;
	}
	
	public Integer getQc() {
		return qc;
	}
	
	public void setQc(Integer qc) {
		this.qc = qc;
	}
	
	public Integer getAphiaID() {
		return aphiaID;
	}
	
	public void setAphiaID(Integer aphiaID) {
		this.aphiaID = aphiaID;
	}
	
	public Integer getSpeciesID() {
		return speciesID;
	}
	
	public void setSpeciesID(Integer speciesID) {
		this.speciesID = speciesID;
	}
	
	public String getScientificNameID() {
		return scientificNameID;
	}
	
	public void setScientificNameID(String scientificNameID) {
		this.scientificNameID = scientificNameID;
	}
	
	public String getClazz() {
		return clazz;
	}
	
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
}
