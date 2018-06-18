package gr.cite.harvester.obis;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ObisHarvester {
	private WebTarget obisTarget;
	
	public ObisHarvester(String obisUrl) {
		this.obisTarget = ClientBuilder.newClient().target(obisUrl);
	}
	
	public Iterator<List<Map<String, Object>>> getAllOccurences() {
		return getOccurencesByAreaAndYear(null, null);
	}
	
	public Iterator<List<Map<String, Object>>> getOccurencesByArea(long areaId) {
		return getOccurencesByAreaAndYear(areaId, null);
	}
	
	public long countOccurencesByArea(Long areaId) {
		if (areaId == null) throw new IllegalArgumentException("areaid parameter must have a value");
		return this.obisTarget.queryParam("areaid", areaId).queryParam("limit", 0).request().get(OccurrencesPage.class).getCount();
	}
	
	public Map<String, Object> getOccurrenceByAreaAndOffset(Long areaId, long offset) {
		if (areaId == null) throw new IllegalArgumentException("areaid parameter must have a value");
		
		return this.obisTarget
			.queryParam("areaid", areaId)
			.queryParam("offset", offset)
			.queryParam("limit", 1)
			.request()
			.get(OccurrencesPage.class).getResults().get(0);
	}
	
	public Iterator<List<Map<String, Object>>> getOccurencesByYear(int year) {
		return getOccurencesByAreaAndYear(null, year);
	}
	
	public Iterator<List<Map<String, Object>>> getOccurencesByAreaAndYear(Long areaId, Integer year) {
		WebTarget finalObisTarget = this.obisTarget;
		
		if (areaId != null) finalObisTarget = finalObisTarget.queryParam("areaid", areaId);
		if (year != null) finalObisTarget = finalObisTarget.queryParam("year", year);
		
		return new OccurrencesIterator(finalObisTarget);
	}
}
