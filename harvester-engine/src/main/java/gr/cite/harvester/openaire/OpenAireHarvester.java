package gr.cite.harvester.openaire;

import gr.cite.harvester.core.HarvestException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

public class OpenAireHarvester {
	private Path directory;
	
	public OpenAireHarvester(String directory) {
		this.directory = Paths.get(directory);
	}
	
	public Iterator<List<String>> getAllFiles() throws HarvestException {
		return new OpenAireRecordBatchIterator(this.directory);
	}
	
}
