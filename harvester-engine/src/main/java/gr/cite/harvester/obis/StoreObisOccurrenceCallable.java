package gr.cite.harvester.obis;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.cite.femme.client.FemmeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.concurrent.Callable;

public class StoreObisOccurrenceCallable implements Callable<String>{
	private static final Logger logger = LoggerFactory.getLogger(StoreObisOccurrenceCallable.class);
	
	private WebTarget semanticSearchTarget = ClientBuilder.newClient().target("http://localhost:8081/femme-fulltext/admin/taxa/marineSpecies");
	private WebTarget marineSpeciesTaxonomy = ClientBuilder.newClient().target("http://www.marinespecies.org/rest/AphiaClassificationByAphiaID");
	
	private ObisAdapter obisAdapter;
	private String importId;
	private Map<String, Object> occurrence;
	
	
	public StoreObisOccurrenceCallable(ObisAdapter obisAdapter, String importId, Map<String, Object> occurrence) {
		this.obisAdapter = obisAdapter;
		this.importId = importId;
		this.occurrence = occurrence;
	}
		
	@Override
	public String call() throws JsonProcessingException, FemmeException {
		String occurrenceId = this.obisAdapter.importOccurrence(this.importId, this.occurrence);
		importOccurrenceTaxonomy(occurrence);
		
		return occurrenceId;
	}
	
	private void importOccurrenceTaxonomy(Map<String, Object> occurence) {
		Map<String, Object> taxon = this.marineSpeciesTaxonomy.path(occurrence.get("aphiaID").toString()).request().get(new GenericType<Map<String, Object>>(){});
		this.semanticSearchTarget.request().post(Entity.entity(taxon, MediaType.APPLICATION_JSON));
	}
}
