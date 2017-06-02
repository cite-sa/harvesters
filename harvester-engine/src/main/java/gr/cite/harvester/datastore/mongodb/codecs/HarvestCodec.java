package gr.cite.harvester.datastore.mongodb.codecs;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import gr.cite.harvester.datastore.model.HarvestCycle;
import gr.cite.harvester.datastore.model.Harvest;
import gr.cite.harvester.datastore.model.Status;
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

import gr.cite.harvester.datastore.model.Schedule;

public class HarvestCodec implements CollectibleCodec<Harvest> {
	
	private static final String HARVEST_ID_KEY = "_id";
	private static final String HARVEST_ENDPOINT_KEY = "endpoint";
	private static final String HARVEST_ENDPOINT_ALIAS_KEY = "endpointAlias";
	private static final String HARVEST_SCHEDULE_KEY = "schedule";
	private static final String HARVEST_STATUS_KEY = "status";
	private static final String HARVEST_CURRENT_HARVEST_CYCLE_KEY = "currentHarvestCycle";
	private static final String HARVEST_PREVIOUS_HARVEST_CYCLES_KEY = "previousHarvestCycles";
	private static final String HARVEST_CREATED_KEY = "created";
	private static final String HARVEST_MODIFIED_KEY = "modified";
	
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
		if (value.getSchedule() != null) {
			writer.writeName(HARVEST_SCHEDULE_KEY);
			encoderContext.encodeWithChildContext(codecRegistry.get(Schedule.class), writer, value.getSchedule());
		}
		if (value.getStatus() != null) {
			writer.writeString(HarvestCodec.HARVEST_STATUS_KEY, value.getStatus().getStatusCode());
		}
		if (value.getCurrentHarvestCycle() != null) {
			writer.writeName(HarvestCodec.HARVEST_CURRENT_HARVEST_CYCLE_KEY);
			encoderContext.encodeWithChildContext(codecRegistry.get(HarvestCycle.class), writer, value.getCurrentHarvestCycle());
		}
		if (value.getPreviousHarvestCycles() != null && value.getPreviousHarvestCycles().size() > 0) {
			writer.writeStartArray(HarvestCodec.HARVEST_PREVIOUS_HARVEST_CYCLES_KEY);
			for (HarvestCycle harvestCycle: value.getPreviousHarvestCycles()) {
				encoderContext.encodeWithChildContext(codecRegistry.get(HarvestCycle.class), writer, harvestCycle);
			}
			writer.writeEndArray();
		}
		if (value.getCreated() != null) {
			writer.writeDateTime(HarvestCodec.HARVEST_CREATED_KEY, value.getCreated().toEpochMilli());
		}
		if (value.getModified() != null) {
			writer.writeDateTime(HarvestCodec.HARVEST_MODIFIED_KEY, value.getModified().toEpochMilli());
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
		Schedule schedule = null;
		Status status = null;
		HarvestCycle currentHarvestCycle = null;
		List<HarvestCycle> previousHarvestCycles = null;
		Instant created = null, modified = null;
		
		reader.readStartDocument();
		
		while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            
            if (fieldName.equals(HarvestCodec.HARVEST_ID_KEY)) {
            	id = reader.readObjectId().toString();
            } else if (fieldName.equals(HarvestCodec.HARVEST_ENDPOINT_KEY)) {
            	endpoint = reader.readString();
            } else if (fieldName.equals(HarvestCodec.HARVEST_ENDPOINT_ALIAS_KEY)) {
            	endpointAlias = reader.readString();
            } else if (fieldName.equals(HarvestCodec.HARVEST_SCHEDULE_KEY)) {
            	if (reader.getCurrentBsonType() == BsonType.DOCUMENT) {
            		schedule = codecRegistry.get(Schedule.class).decode(reader, decoderContext);            		
            	}
            } else if (fieldName.equals(HarvestCodec.HARVEST_STATUS_KEY)) {
            	status = Status.getEnum(reader.readString());
			} else if (fieldName.equals(HarvestCodec.HARVEST_CURRENT_HARVEST_CYCLE_KEY)) {
				if (reader.getCurrentBsonType() == BsonType.DOCUMENT) {
					currentHarvestCycle = codecRegistry.get(HarvestCycle.class).decode(reader, decoderContext);
				}
            } else if (fieldName.equals(HarvestCodec.HARVEST_PREVIOUS_HARVEST_CYCLES_KEY)) {
				previousHarvestCycles = new ArrayList<>();

				reader.readStartArray();
				while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
					previousHarvestCycles.add(codecRegistry.get(HarvestCycle.class).decode(reader, decoderContext));
				}
				reader.readEndArray();
			} else if (fieldName.equals(HarvestCodec.HARVEST_CREATED_KEY)) {
				created = Instant.ofEpochMilli(reader.readDateTime());
			} else if (fieldName.equals(HarvestCodec.HARVEST_MODIFIED_KEY)) {
				modified = Instant.ofEpochMilli(reader.readDateTime());
			}
		}
		
		reader.readEndDocument();

		Harvest harvest = new Harvest();
		harvest.setId(id);
		harvest.setEndpoint(endpoint);
		harvest.setEndpointAlias(endpointAlias);
		harvest.setSchedule(schedule);
		harvest.setStatus(status);
		harvest.setCurrentHarvestCycle(currentHarvestCycle);
		harvest.setPreviousHarvestCycles(previousHarvestCycles);
		harvest.setCreated(created);
		harvest.setModified(modified);

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
