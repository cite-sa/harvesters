package gr.cite.earthserver.harvester.datastore.mongodb;

import java.util.ArrayList;
import java.util.List;

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
	public String unregisterHarvest(String endpoint) {
		return this.harvestCollection.findOneAndDelete(Filters.eq("endpoint", endpoint),
				new FindOneAndDeleteOptions().projection(Projections.include("_id"))).getId();
	}

	@Override
	public String updateHarvest(Harvest harvest) {
		// TODO Auto-generated method stub
		return null;
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
	
	@Override
	public Harvest updateHarvestStatus(String id, Status status) {
		return this.harvestCollection.findOneAndUpdate(
				Filters.eq("_id", new ObjectId(id)),
				new Document().append("$set", new Document().append("status", status.getStatusCode())));
	}

}
