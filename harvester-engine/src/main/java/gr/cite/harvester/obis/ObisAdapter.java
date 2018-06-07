package gr.cite.harvester.obis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.cite.femme.client.FemmeException;
import gr.cite.femme.client.api.FemmeClientAPI;
import gr.cite.femme.core.model.Collection;
import gr.cite.femme.core.model.DataElement;
import gr.cite.femme.core.model.ElementType;
import gr.cite.femme.core.model.Metadatum;
import gr.cite.harvester.core.Adapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Map;

@Component
public class ObisAdapter extends Adapter {
	private static final Logger logger = LoggerFactory.getLogger(ObisAdapter.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public ObisAdapter(FemmeClientAPI femmeClient) {
		super(femmeClient);
	}
	
	public String importCollection(String importId, String endpoint, String name) throws FemmeException {
		Collection collection = new Collection();
		
		collection.setEndpoint(endpoint);
		collection.setName(name);
		
		return this.getFemmeClient().importCollection(importId, collection);
	}
	
	public String importOccurrence(String importId, Map<String, Object> occurrence) throws JsonProcessingException, FemmeException {
		DataElement dataElement = new DataElement();
		
		dataElement.setType(ElementType.OBIS);
		dataElement.setName(occurrence.get("scientificName") + "_" + occurrence.get("id"));
		
		dataElement.setEndpoint(buildEndpoint(occurrence.get("id").toString()));
		
		Metadatum metadatum = new Metadatum();
		metadatum.setContentType(MediaType.APPLICATION_JSON);
		metadatum.setValue(mapper.writeValueAsString(occurrence));
		
		dataElement.setMetadata(Collections.singletonList(metadatum));
		
		String dataElementId = this.getFemmeClient().importInCollection(importId, dataElement);
		
		dataElement.setId(dataElementId);
		
		return dataElementId;
	}
	
	private String buildEndpoint(String occurrenceId) {
		return "http://api.iobis.org/occurrence/" + occurrenceId;
	}
}
