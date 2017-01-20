package gr.cite.earthserver.harvester.datastore.mongodb.codecs;

import gr.cite.earthserver.harvester.datastore.model.HarvestCycle;
import org.bson.*;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

import java.time.Instant;

public class HarvestCycleCodec implements CollectibleCodec<HarvestCycle> {

    private static final String HARVEST_CYCLE_ID_KEY = "_id";
    private static final String HARVEST_CYCLE_START_TIME_KEY = "startTime";
    private static final String HARVEST_CYCLE_END_TIME_KEY = "endTime";
    private static final String HARVEST_CYCLE_TOTAL_ELEMENTS_KEY = "totalElements";
    private static final String HARVEST_CYCLE_NEW_ELEMENTS_KEY = "newElements";
    private static final String HARVEST_CYCLE_UPDATED_ELEMENTS_KEY = "updatedElements";
    private static final String HARVEST_CYCLE_FAILED_ELEMENTS_KEY = "failedElements";
    private static final String HARVEST_CYCLE_ERROR_MESSAGE_KEY = "errorMessage";

    private CodecRegistry codecRegistry;

    public HarvestCycleCodec(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    @Override
    public void encode(BsonWriter writer, HarvestCycle value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        generateIdIfAbsentFromDocument(value);

        writer.writeObjectId(HarvestCycleCodec.HARVEST_CYCLE_ID_KEY, new ObjectId(value.getId()));
        if (value.getStartTime() != null) {
            writer.writeDateTime(HarvestCycleCodec.HARVEST_CYCLE_START_TIME_KEY, value.getStartTime().toEpochMilli());
        }
        if (value.getEndTime() != null) {
            writer.writeDateTime(HarvestCycleCodec.HARVEST_CYCLE_END_TIME_KEY, value.getEndTime().toEpochMilli());
        }
        if (value.getTotalElements() != null) {
            writer.writeInt64(HarvestCycleCodec.HARVEST_CYCLE_TOTAL_ELEMENTS_KEY, value.getTotalElements());
        }
        if (value.getNewElements() != null) {
            writer.writeInt64(HarvestCycleCodec.HARVEST_CYCLE_NEW_ELEMENTS_KEY, value.getNewElements());
        }
        if (value.getUpdatedElements() != null) {
            writer.writeInt64(HarvestCycleCodec.HARVEST_CYCLE_UPDATED_ELEMENTS_KEY, value.getUpdatedElements());
        }
        if (value.getFailedElements() != null) {
            writer.writeInt64(HarvestCycleCodec.HARVEST_CYCLE_FAILED_ELEMENTS_KEY, value.getFailedElements());
        }
        if (value.getErrorMessage() != null) {
            writer.writeString(HarvestCycleCodec.HARVEST_CYCLE_ERROR_MESSAGE_KEY, value.getErrorMessage());
        }

        writer.writeEndDocument();

    }

    @Override
    public Class<HarvestCycle> getEncoderClass() {
        return HarvestCycle.class;
    }

    @Override
    public HarvestCycle decode(BsonReader reader, DecoderContext decoderContext) {
        String id = null, error = null;
        Instant startTime = null, endTime = null;
        Long totalElements = null, newElements = null, updatedElements = null, failedElements = null;

        reader.readStartDocument();

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();

            if (fieldName.equals(HarvestCycleCodec.HARVEST_CYCLE_ID_KEY)) {
                id = reader.readObjectId().toString();
            } else if (fieldName.equals(HarvestCycleCodec.HARVEST_CYCLE_START_TIME_KEY)) {
                startTime = Instant.ofEpochMilli(reader.readDateTime());
            } else if (fieldName.equals(HarvestCycleCodec.HARVEST_CYCLE_END_TIME_KEY)) {
                endTime = Instant.ofEpochMilli(reader.readDateTime());
            } else if (fieldName.equals(HarvestCycleCodec.HARVEST_CYCLE_TOTAL_ELEMENTS_KEY)) {
                totalElements = reader.readInt64();
            } else if (fieldName.equals(HarvestCycleCodec.HARVEST_CYCLE_NEW_ELEMENTS_KEY)) {
                newElements = reader.readInt64();
            } else if (fieldName.equals(HarvestCycleCodec.HARVEST_CYCLE_UPDATED_ELEMENTS_KEY)) {
                updatedElements = reader.readInt64();
            } else if (fieldName.equals(HarvestCycleCodec.HARVEST_CYCLE_FAILED_ELEMENTS_KEY)) {
                failedElements = reader.readInt64();
            } else if (fieldName.equals(HarvestCycleCodec.HARVEST_CYCLE_ERROR_MESSAGE_KEY)) {
                error = reader.readString();
            }
        }

        reader.readEndDocument();

        HarvestCycle harvestCycle = new HarvestCycle();
        harvestCycle.setId(id);
        harvestCycle.setStartTime(startTime);
        harvestCycle.setEndTime(endTime);
        harvestCycle.setTotalElements(totalElements);
        harvestCycle.setNewElements(newElements);
        harvestCycle.setUpdatedElements(updatedElements);
        harvestCycle.setFailedElements(failedElements);
        harvestCycle.setErrorMessage(error);

        return harvestCycle;
    }

    @Override
    public HarvestCycle generateIdIfAbsentFromDocument(HarvestCycle harvestCycle) {
        if (!documentHasId(harvestCycle)) {
            harvestCycle.setId(new ObjectId().toString());
        }
        return harvestCycle;
    }

    @Override
    public boolean documentHasId(HarvestCycle harvestCycle) {
        return harvestCycle.getId() != null;
    }

    @Override
    public BsonValue getDocumentId(HarvestCycle harvestCycle) {
        if (!documentHasId(harvestCycle)) {
            throw new IllegalStateException("The harvest cycle does not contain an _id");
        }
        return new BsonString(harvestCycle.getId());
    }
}
