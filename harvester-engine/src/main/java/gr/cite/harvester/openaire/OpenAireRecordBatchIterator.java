package gr.cite.harvester.openaire;

import gr.cite.harvester.core.HarvestException;
import gr.cite.harvester.obis.OccurrencesIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OpenAireRecordBatchIterator implements Iterator<List<String>> {
	private static final Logger logger = LoggerFactory.getLogger(OccurrencesIterator.class);
	
	private Iterator<Path> fileIterator;
	
	public OpenAireRecordBatchIterator(Path directory) throws HarvestException {
		try (Stream<Path> paths = Files.walk(directory)) {
			this.fileIterator = paths.filter(Files::isRegularFile).iterator();
		} catch (Exception e) {
			throw new HarvestException("Could not list files", e);
		}
	}
	
	@Override
	public boolean hasNext() {
		return this.fileIterator.hasNext();
	}
	
	@Override
	public List<String> next() {
		try {
			return OpenAireRecordUtils.getRecords(Files.lines(this.fileIterator.next()).collect(Collectors.joining()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return Collections.emptyList();
		}
	}
	
}
