package gr.cite.harvester.obis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.WebTarget;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OccurrencesIterator implements Iterator<List<Map<String, Object>>> {
	private static final Logger logger = LoggerFactory.getLogger(OccurrencesIterator.class);
	
	private static final int limit = 1000;
	private WebTarget obisOccurencesTarget;
	
	private OccurrencesPage occurrencesPage;
	private long offset = 0;
	
	public OccurrencesIterator(WebTarget obisOccurencesTarget) {
		this.obisOccurencesTarget = obisOccurencesTarget;
	}
	
	@Override
	public boolean hasNext() {
		return ! this.occurrencesPage.isLastpage();
	}
	
	@Override
	public List<Map<String, Object>> next() {
		logger.info("Before, offset: " + this.offset);
		this.occurrencesPage = retrieveOccurencesPage();
		this.offset = this.offset + limit;
		logger.info("After, offset: " + this.offset);
		
		return this.occurrencesPage.getResults();
	}
	
	private OccurrencesPage retrieveOccurencesPage() {
		return this.obisOccurencesTarget.queryParam("limit", limit).queryParam("offset", this.offset).request().get(OccurrencesPage.class);
	}
}
