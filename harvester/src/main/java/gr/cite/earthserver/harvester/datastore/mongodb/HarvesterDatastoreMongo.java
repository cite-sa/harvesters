package gr.cite.earthserver.harvester.datastore.mongodb;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.Projections;

import gr.cite.earthserver.harvester.datastore.model.Harvest;
import gr.cite.earthserver.harvester.datastore.model.Status;

public class HarvesterDatastoreMongo implements HarvesterDatastore {
	
	private HarvesterDatastoreMongoClient mongoClient;
	
	private MongoCollection<Harvest> harvestCollection;

	public HarvesterDatastoreMongo(String dbHost, String dbName) {
		this.mongoClient = new HarvesterDatastoreMongoClient(dbHost, dbName);
		this.harvestCollection = this.mongoClient.getHarvestCollection();
	}
	
	@Override
	public String insertHarvest(Harvest harvest) {
		this.harvestCollection.insertOne(harvest);
		return harvest.getId();
	}
	
	@Override
	public String deleteHarvest(String id) {
		Harvest harvest = this.harvestCollection.findOneAndDelete(Filters.eq("_id", new ObjectId(id)),
				new FindOneAndDeleteOptions().projection(Projections.include("_id")));
		
		return harvest != null ? harvest.getId() : null;
		
	}

	@Override
	public String updateHarvest(Harvest harvest) throws OperationNotSupportedException {
		throw new OperationNotSupportedException("Update harvest not supported yet");
	}
	
	@Override
	public Harvest updateHarvestStatus(String id, Status status) {
		
		Harvest harvest = null;
		
		if(Status.RUNNING.equals(status)){
			return this.harvestCollection.findOneAndUpdate(
					Filters.eq("_id", new ObjectId(id)),
					new Document().append("$set", new Document().append("status", status.getStatusCode()).append("startTime", new Date())));
		} else if(Status.STOPPED.equals(status)){
			return this.harvestCollection.findOneAndUpdate(
					Filters.eq("_id", new ObjectId(id)),
					new Document().append("$set", new Document().append("status", status.getStatusCode())));
		} else if(Status.PENDING.equals(status)){
			return this.harvestCollection.findOneAndUpdate(
					Filters.eq("_id", new ObjectId(id)),
					new Document().append("$set", new Document().append("status", status.getStatusCode())));
		} else if(Status.ERROR.equals(status)){
			return this.harvestCollection.findOneAndUpdate(
					Filters.eq("_id", new ObjectId(id)),
					new Document().append("$set", new Document().append("status", status.getStatusCode())));
		} else if(Status.FINISHED.equals(status)){
			return this.harvestCollection.findOneAndUpdate(
					Filters.eq("_id", new ObjectId(id)),
					new Document().append("$set", new Document().append("status", status.getStatusCode()).append("endTime", new Date())));
		} else {
			return harvest;
		}
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
	
	public List<Harvest> getHarvestsToBeHarvested(){
		
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
					Instant endtimeOfHarvest = harvest.getEndTime();
					Instant now = Instant.now();
					Duration timePassed = Duration.between(endtimeOfHarvest, now);
					
					if (timePassed.compareTo(period) > 0) {
						harvests.add(harvest);
					}
				}
				
			}
		} finally {
			harvestCursor.close();
		}
		
		return harvests;
	}
	
	
}
