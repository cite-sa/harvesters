package gt.cite.earthserver.harvester.datastore.mongodb.codecs;

import java.time.Instant;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

import gr.cite.earthserver.harvester.datastore.model.Harvest;
import gr.cite.earthserver.harvester.datastore.model.Status;
import gr.cite.earthserver.harvester.datastore.model.Schedule;

public class HarvestCodec implements CollectibleCodec<Harvest> {
	
	private static final String HARVEST_ID_KEY = "_id";
	private static final String HARVEST_ENDPOINT_KEY = "endpoint";
	private static final String HARVEST_ENDPOINT_ALIAS_KEY = "endpointAlias";
	private static final String HARVEST_START_TIME_KEY = "startTime";
	private static final String HARVEST_END_TIME_KEY = "endTime";
	private static final String HARVEST_SCHEDULE_KEY = "schedule";
	private static final String HARVEST_STATUS_KEY = "status";
	
	private CodecRegistry codecRegistry;
	
	public HarvestCodec(CodecRegistry codecRegistry) {
		this.codecRegistry = codecRegistry;
	}

	@Override
	public void encode(BsonWriter writer, Harvest value, EncoderContext encoderContext) {
		writer.writeStartDocument();

		if (!documentHasId(value)) {
			generateIdIfAbsentFromDocument(value);
		}
		
		if (value.getId() != null) {
			writer.writeObjectId(HarvestCodec.HARVEST_ID_KEY, new ObjectId(value.getId()));
		}
		if (value.getEndpoint() != null) {
			writer.writeString(HarvestCodec.HARVEST_ENDPOINT_KEY, value.getEndpoint());
		}
		if (value.getEndpointAlias() != null) {
			writer.writeString(HarvestCodec.HARVEST_ENDPOINT_ALIAS_KEY, value.getEndpointAlias());
		}
		if (value.getStartTime() != null) {
			writer.writeDateTime(HarvestCodec.HARVEST_START_TIME_KEY, value.getStartTime().toEpochMilli());
		}
		if (value.getEndTime() != null) {
			writer.writeDateTime(HarvestCodec.HARVEST_END_TIME_KEY, value.getEndTime().toEpochMilli());
		}
		if (value.getSchedule() != null) {
			writer.writeName(HARVEST_SCHEDULE_KEY);
			encoderContext.encodeWithChildContext(codecRegistry.get(Schedule.class), writer, value.getSchedule());
		}
		if (value.getStatus() != null) {
			writer.writeString(HarvestCodec.HARVEST_STATUS_KEY, value.getStatus().name());
		}
		
		writer.writeEndDocument();
	}

	@Override
	public Class<Harvest> getEncoderClass() {
		return Harvest.class;
	}

	@Override
	public Harvest decode(BsonReader reader, DecoderContext decoderContext) {
		String id = null, endpoint = null, endpointAlias = null;
		Instant startTime = null, endTime = null;
		Schedule schedule = null;
		Status status = null;
		
		reader.readStartDocument();
		
		while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            
            if (fieldName.equals(HarvestCodec.HARVEST_ID_KEY)) {
            	id = reader.readObjectId().toString();
            } else if (fieldName.equals(HarvestCodec.HARVEST_ENDPOINT_KEY)) {
            	endpoint = reader.readString();
            } else if (fieldName.equals(HarvestCodec.HARVEST_ENDPOINT_ALIAS_KEY)) {
            	endpointAlias = reader.readString();
            } else if (fieldName.equals(HarvestCodec.HARVEST_START_TIME_KEY)) {
            	startTime = Instant.ofEpochMilli(reader.readDateTime());
            } else if (fieldName.equals(HarvestCodec.HARVEST_END_TIME_KEY)) {
            	endTime = Instant.ofEpochMilli(reader.readDateTime());
            } else if (fieldName.equals(HarvestCodec.HARVEST_SCHEDULE_KEY)) {
            	if (reader.getCurrentBsonType() == BsonType.DOCUMENT) {
            		schedule = codecRegistry.get(Schedule.class).decode(reader, decoderContext);            		
            	}
            } else if (fieldName.equals(HarvestCodec.HARVEST_STATUS_KEY)) {
            	status = Status.valueOf(reader.readString());
            }
		}
		
		reader.readEndDocument();

		Harvest harvest = new Harvest();
		harvest.setId(id);
		harvest.setEndpoint(endpoint);
		harvest.setEndpointAlias(endpointAlias);
		harvest.setStartTime(startTime);
		harvest.setEndTime(endTime);
		harvest.setSchedule(schedule);
		harvest.setStatus(status);

		return harvest;
	}

	@Override
	public Harvest generateIdIfAbsentFromDocument(Harvest harvest) {
		if (!documentHasId(harvest)) {
			harvest.setId(new ObjectId().toString());
		}
		return harvest;
	}

	@Override
	public boolean documentHasId(Harvest harvest) {
		return harvest.getId() != null;
	}

	@Override
	public BsonValue getDocumentId(Harvest harvest) {
		if (!documentHasId(harvest)) {
			throw new IllegalStateException("The harvest does not contain an _id");
		}
		return new BsonString(harvest.getId());
	}
}
