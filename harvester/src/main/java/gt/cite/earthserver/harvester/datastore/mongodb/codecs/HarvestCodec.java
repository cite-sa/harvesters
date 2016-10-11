package gt.cite.earthserver.harvester.datastore.mongodb.codecs;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.earthserver.harvester.datastore.model.Harvest;

public class HarvestCodec implements CollectibleCodec<Harvest> {
	private static final Logger logger = LoggerFactory.getLogger(HarvestCodec.class);

	private static final String HARVEST_ID_KEY = "_id";
	private static final String HARVEST_ENDPOINT_KEY = "endpoint";
	private static final String HARVEST_DESCRIPTION_KEY = "description";
	private static final String HARVEST_START_TIME_KEY = "startTime";
	private static final String HARVEST_END_TIME_KEY = "endTime";
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
		
		if (value.getDescription() != null) {
			writer.writeString(HarvestCodec.HARVEST_DESCRIPTION_KEY, value.getDescription());
		}

		writer.writeEndDocument();
	}

	@Override
	public Class<Harvest> getEncoderClass() {
		return Harvest.class;
	}

	@Override
	public Harvest decode(BsonReader reader, DecoderContext decoderContext) {
		String id = null, endpoing = null, description = null;
		
		reader.readStartDocument();
		
		while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            
            if (fieldName.equals(HarvestCodec.HARVEST_ID_KEY)) {
            	id = reader.readObjectId().toString();
            } else if (fieldName.equals(HarvestCodec.HARVEST_ENDPOINT_KEY)) {
            	endpoing = reader.readString();
            } else if (fieldName.equals(HarvestCodec.HARVEST_DESCRIPTION_KEY)) {
            	description = reader.readString();
            }
		}
		
		reader.readEndDocument();

		Harvest harvest = new Harvest();
		harvest.setId(id);
		harvest.setEndpoint(endpoing);
		harvest.setDescription(description);

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
