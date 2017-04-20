package gr.cite.harvester.datastore.mongodb;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.mongodb.client.model.*;
import gr.cite.harvester.datastore.model.HarvestCycle;
import gr.cite.harvester.datastore.model.Harvest;
import gr.cite.harvester.datastore.model.Status;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class HarvesterDatastoreMongo implements HarvesterDatastore {
	
	private HarvesterDatastoreMongoClient mongoClient;
	
	private MongoCollection<Harvest> harvestCollection;

	private Integer maxLoggedHarvestCycles;

	public HarvesterDatastoreMongo(String dbHost, String dbName, Integer maxLoggedHarvestCycles) {
		this.mongoClient = new HarvesterDatastoreMongoClient(dbHost, dbName);
		this.harvestCollection = this.mongoClient.getHarvestCollection();
		this.maxLoggedHarvestCycles = maxLoggedHarvestCycles;
	}
	
	@Override
	public void close() {
		this.mongoClient.close();
	}
	
	@Override
	public String insertHarvest(Harvest harvest) {
		harvest.setCreated(Instant.now());
		harvest.setModified(null);

		harvestCollection.insertOne(harvest);
		return harvest.getId();
	}
	
	@Override
	public String deleteHarvest(String id) {
		Harvest harvest = harvestCollection.findOneAndDelete(Filters.eq("_id", new ObjectId(id)),
				new FindOneAndDeleteOptions().projection(Projections.include("_id")));
		
		return harvest != null ? harvest.getId() : null;
		
	}

	@Override
	public Harvest updateHarvest(Harvest harvest) {
		harvest.setCreated(null);
		harvest.setModified(Instant.now());

		return harvestCollection.findOneAndUpdate(
				Filters.eq("_id", new ObjectId(harvest.getId())),
				new Document().append("$set", harvest),
				new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
	}

	@Override
	public Harvest updateHarvestStatus(String harvestId, Status status) {
		return updateHarvestStatus(harvestId, status, null);
	}

	@Override
	public Harvest updateHarvestStatus(String harvestId, Status status, String errorMessage) {

		Harvest harvest = null;
		
		if(Status.RUNNING.equals(status)) {
			harvest = getHarvestById(harvestId);

			harvest.setStatus(status);

			if (harvest.getPreviousHarvestCycles() == null) {
				harvest.setPreviousHarvestCycles(new ArrayList<>());
			}
			List<HarvestCycle> previousHarvestCycles = harvest.getPreviousHarvestCycles();
			if (previousHarvestCycles.size() == maxLoggedHarvestCycles) {
				previousHarvestCycles.stream().sorted(Comparator.comparing(HarvestCycle::getStartTime)).findFirst().ifPresent(oldestHarvestCycle -> {
					previousHarvestCycles.removeIf(harvestCycle -> harvestCycle.getId().equals(oldestHarvestCycle.getId()));
				});

			}
			if (harvest.getCurrentHarvestCycle() != null) {
				previousHarvestCycles.add(harvest.getCurrentHarvestCycle());
			}

			harvest.setCurrentHarvestCycle(new HarvestCycle());
		} else if (Status.STOPPED.equals(status) || Status.PENDING.equals(status)) {
			harvest = new Harvest();
			harvest.setId(harvestId);
			harvest.setStatus(status);
		} else if (Status.ERROR.equals(status)) {
			if (errorMessage == null) {
				throw new IllegalArgumentException("Error message can not be null");
			}
			harvest = getHarvestById(harvestId);
			harvest.setEndpoint(null);
			harvest.setEndpointAlias(null);
			harvest.setSchedule(null);
			harvest.setStatus(status);
			HarvestCycle currentHarvestCycle = harvest.getCurrentHarvestCycle() == null ? new HarvestCycle() : harvest.getCurrentHarvestCycle();
			currentHarvestCycle.setEndTime(Instant.now());
			currentHarvestCycle.setErrorMessage(errorMessage);
			harvest.setPreviousHarvestCycles(null);

		} else if (Status.FINISHED.equals(status)) {
			harvest = getHarvestById(harvestId);

			harvest.setEndpoint(null);
			harvest.setEndpointAlias(null);
			harvest.setSchedule(null);
			harvest.setStatus(status);
			harvest.setPreviousHarvestCycles(null);

			harvest.getCurrentHarvestCycle().setEndTime(Instant.now());
		}

		return updateHarvest(harvest);
	}

	@Override
	public Harvest getHarvestById(String id) {
		return this.harvestCollection.find(Filters.eq("_id", new ObjectId(id))).limit(1).first();
	}
	
	@Override
	public Harvest getHarvestByEndpoint(String endpoint) {
		return this.harvestCollection.find(Filters.eq("endpoint", endpoint)).limit(1).first();
	}
	
	@Override
	public Harvest getHarvestByEndpointAlias(String endpointAlias){
		return this.harvestCollection.find(Filters.eq("endpointAlias", endpointAlias)).limit(1).first();
	}
	
	@Override
	public List<Harvest> getHarvests() {
		List<Harvest> harvests = new ArrayList<>();
		this.harvestCollection.find().into(harvests);
		return harvests;
	}

	@Override
	public List<Harvest> getHarvests(Integer limit, Integer offset) {
		List<Harvest> harvests = new ArrayList<>();
		FindIterable<Harvest> harvestIterable = this.harvestCollection.find();
		if (limit != null) {
			harvestIterable = harvestIterable.limit(limit);
		}
		if (offset != null) {
			harvestIterable = harvestIterable.skip(offset);
		}
		harvestIterable.into(harvests);
		return harvests;
	}
	
	public List<Harvest> getHarvestsToBeHarvested() {
		
		List <Harvest> harvests = new ArrayList<>();
		
		FindIterable<Harvest> harvestIterable = this.harvestCollection.find(
				Filters.or(Filters.eq("status", Status.PENDING.getStatusCode()), Filters.eq("status", Status.FINISHED.getStatusCode()), Filters.eq("status", Status.ERROR.getStatusCode()))
			);
		
		MongoCursor<Harvest> harvestCursor = null;
		
		try {				
			harvestCursor = harvestIterable.iterator();

			while (harvestCursor.hasNext()) {
				
				Harvest harvest = harvestCursor.next();
				
				if (Status.PENDING.equals(harvest.getStatus())) {
					harvests.add(harvest);
				} else if (Status.FINISHED.equals(harvest.getStatus()) || Status.ERROR.equals(harvest.getStatus())) {
					
					Duration period = Duration.of(harvest.getSchedule().getPeriod(), harvest.getSchedule().getPeriodType());
					Instant endtimeOfHarvest = harvest.getCurrentHarvestCycle().getEndTime();
					Instant now = Instant.now();
					Duration timePassed = Duration.between(endtimeOfHarvest, now);
					
					if (timePassed.compareTo(period) > 0) {
						harvests.add(harvest);
					}
				}
				
			}
		} finally {
			if (harvestCursor != null) {
				harvestCursor.close();
			}
		}
		
		return harvests;
	}

	@Override
	public Harvest incrementHarvestedElementsCounters(String harvestId, HarvestCycle harvestCycle) {
		Objects.requireNonNull(harvestId, "Harvest ID can not be null");
		Objects.requireNonNull(harvestCycle, "Harvest cycle can not be null");

		//List<Bson> updates = incrementValuePerField.entrySet().stream().map(fieldAndValue -> Updates.inc(fieldAndValue.getKey(), fieldAndValue.getValue())).collect(Collectors.toList());
		return harvestCollection.findOneAndUpdate(
				Filters.eq("_id", new ObjectId(harvestId)),
				new Document().append("$inc",
						new Document().append("currentHarvestCycle.totalElements", harvestCycle.getTotalElements())
								.append("currentHarvestCycle.newElements",harvestCycle.getNewElements())
								.append("currentHarvestCycle.updatedElements", harvestCycle.getUpdatedElements())
								.append("currentHarvestCycle.failedElements", harvestCycle.getFailedElements())),
				new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
		);
	}


	//@Override
	public Harvest incrementHarvestMetadata(String id, Map<String, Integer> incrementValuePerField) {
		Objects.requireNonNull(id, "ID can not be null");
		Objects.requireNonNull(incrementValuePerField, "Fields and values can not be null");

		List<Bson> updates = incrementValuePerField.entrySet().stream().map(fieldAndValue -> Updates.inc(fieldAndValue.getKey(), fieldAndValue.getValue())).collect(Collectors.toList());
		return harvestCollection.findOneAndUpdate(
				Filters.eq("_id", new ObjectId(id)),
				Updates.combine(updates),
				new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
		);
	}

}
