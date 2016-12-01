package gr.cite.earthserver.harvester.datastore.mongodb;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
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
	public String registerHarvest(Harvest harvest) {
		this.harvestCollection.insertOne(harvest);
		return harvest.getId();
	}
	
	@Override
	public String unregisterHarvest(String id) {
		return this.harvestCollection.findOneAndDelete(Filters.eq("_id", new ObjectId(id)),
				new FindOneAndDeleteOptions().projection(Projections.include("_id"))).getId();
	}

	@Override
	public String updateHarvest(Harvest harvest) throws OperationNotSupportedException {
		throw new OperationNotSupportedException("Update harvest not supported yet");
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
		FindIterable<Harvest> harvestIterable = this.harvestCollection.find();
		harvestIterable.into(harvests);
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
//		FindIterable<Harvest> harvestIterable = this.harvestCollection.find(Filters.or(Filters.eq("status", "finished"),Filters.eq("status", "starting"),
//				                                                            Filters.eq("status", "pending"),Filters.eq("status", "stopped")));
		FindIterable<Harvest> harvestIterable = this.harvestCollection.find(Filters.not(Filters.eq("status", "error")));
		harvestIterable.into(harvests);
		
		return harvests;
	}
	
	@Override
	synchronized public Harvest updateHarvestStatus(String id, Status status) {
		
		Harvest harvest = null;
		
		if("running".equals(status.getStatusCode())){
			return this.harvestCollection.findOneAndUpdate(
					Filters.eq("_id", new ObjectId(id)),
					new Document().append("$set", new Document().append("status", status.getStatusCode()).append("startTime", new Date())));
		}
		else if("stopped".equals(status.getStatusCode())){
			return this.harvestCollection.findOneAndUpdate(
					Filters.eq("_id", new ObjectId(id)),
					new Document().append("$set", new Document().append("status", status.getStatusCode())));
		}
		else if("pending".equals(status.getStatusCode())){
			return this.harvestCollection.findOneAndUpdate(
					Filters.eq("_id", new ObjectId(id)),
					new Document().append("$set", new Document().append("status", status.getStatusCode())));
		}
		else if("error".equals(status.getStatusCode())){
			return this.harvestCollection.findOneAndUpdate(
					Filters.eq("_id", new ObjectId(id)),
					new Document().append("$set", new Document().append("status", status.getStatusCode())));
		}
		else if("finished".equals(status.getStatusCode())){
			return this.harvestCollection.findOneAndUpdate(
					Filters.eq("_id", new ObjectId(id)),
					new Document().append("$set", new Document().append("status", status.getStatusCode()).append("endTime", new Date())));
		}
		else {
			return harvest;
		}
	}
}
