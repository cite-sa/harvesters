package gr.cite.harvester.datastore.mongodb;

import gr.cite.harvester.datastore.mongodb.codecs.HarvestCycleCodecProvider;
import gr.cite.harvester.datastore.model.Harvest;
import gr.cite.harvester.datastore.mongodb.codecs.HarvestCodecProvider;
import gr.cite.harvester.datastore.mongodb.codecs.ScheduleCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class HarvesterDatastoreMongoClient {
	
//	private static final String DATABASE_HOST = "es-devel1.local.cite.gr:27017";
	private static final String DATABASE_HOST = "localhost:27017";
	
//	private static final String DATABASE_NAME = "harvester-db";
	private static final String DATABASE_NAME = "harvester-db-devel";
	private static final String HARVEST_COLLECTION_NAME = "harvests";

	private MongoClient client;
	private MongoDatabase database;
	private MongoCollection<Harvest> harvests;

	public HarvesterDatastoreMongoClient() {
		this(DATABASE_HOST, DATABASE_NAME);
	}

	@Inject
	public HarvesterDatastoreMongoClient(String dbHost, String dbName) {

		this.client = new MongoClient(dbHost);
		this.database = this.client.getDatabase(dbName);

		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(new HarvestCodecProvider(), new ScheduleCodecProvider(), new HarvestCycleCodecProvider()));

		this.harvests = this.database.getCollection(HARVEST_COLLECTION_NAME, Harvest.class).withCodecRegistry(codecRegistry);
	}

	public MongoCollection<Harvest> getHarvestCollection() {
		return this.harvests;
	}

	public void close() {
		this.client.close();
	}
}
