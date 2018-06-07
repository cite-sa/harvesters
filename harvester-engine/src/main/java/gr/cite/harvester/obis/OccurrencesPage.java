package gr.cite.harvester.obis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OccurrencesPage {
	
	@JsonProperty("count")
	private Long count;
	
	@JsonProperty("offset")
	private Integer offset;
	
	@JsonProperty("limit")
	private Integer limit;
	
	@JsonProperty("lastpage")
	private boolean lastpage;
	
	@JsonProperty("results")
	private List<Map<String, Object>> results;
	
	public Long getCount() {
		return count;
	}
	
	public void setCount(Long count) {
		this.count = count;
	}
	
	public Integer getOffset() {
		return offset;
	}
	
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	
	public Integer getLimit() {
		return limit;
	}
	
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	
	public boolean isLastpage() {
		return lastpage;
	}
	
	public void setLastpage(boolean lastpage) {
		this.lastpage = lastpage;
	}
	
	public List<Map<String, Object>> getResults() {
		return results;
	}
	
	public void setResults(List<Map<String, Object>> results) {
		this.results = results;
	}
}
