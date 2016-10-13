package gr.cite.earthserver.harvester.datastore.mongodb;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import gr.cite.earthserver.harvester.datastore.model.Harvest;
import gt.cite.earthserver.harvester.datastore.mongodb.codecs.HarvestCodecProvider;
import gt.cite.earthserver.harvester.datastore.mongodb.codecs.ScheduleCodecProvider;

public class HarvesterDatastoreMongoClient {
	private static final String DATABASE_HOST = "es-devel1.local.cite.gr:27017";
	// private static final String DATABASE_HOST = "localhost:27017";
	private static final String DATABASE_NAME = "harvester-db";
	private static final String HARVEST_COLLECTION_NAME = "harvests";

	private MongoClient client;
	private MongoDatabase database;
	private MongoCollection<Harvest> harvests;

	public HarvesterDatastoreMongoClient() {
		this(DATABASE_HOST, DATABASE_NAME);
	}

	public HarvesterDatastoreMongoClient(String dbHost, String dbName) {

		this.client = new MongoClient(dbHost);
		this.database = this.client.getDatabase(dbName);

		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(new HarvestCodecProvider(), new ScheduleCodecProvider()));

		this.harvests = this.database.getCollection(HARVEST_COLLECTION_NAME, Harvest.class)
				.withCodecRegistry(codecRegistry);
	}

	public MongoCollection<Harvest> getHarvestCollection() {
		return this.harvests;
	}

	public void close() {
		this.client.close();
	}
}
