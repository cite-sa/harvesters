package gr.cite.earthserver.harvester.datastore.mongodb.codecs;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

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
import gr.cite.earthserver.harvester.datastore.model.Schedule;

public class ScheduleCodec implements CollectibleCodec<Schedule> {
	
	private static final Logger logger = LoggerFactory.getLogger(ScheduleCodec.class);

	private static final String SCHEDULE_ID_KEY = "_id";
	private static final String SCHEDULE_PERIOD_KEY = "period";
	private static final String SCHEDULE_TIME_UNIT_KEY = "timeUnit";
	
	private CodecRegistry codecRegistry;
	
	public ScheduleCodec(CodecRegistry codecRegistry) {
		this.codecRegistry = codecRegistry;
	}

	@Override
	public void encode(BsonWriter writer, Schedule value, EncoderContext encoderContext) {
		writer.writeStartDocument();

		if (!documentHasId(value)) {
			generateIdIfAbsentFromDocument(value);
		}
		
		if (value.getId() != null) {
			writer.writeObjectId(ScheduleCodec.SCHEDULE_ID_KEY, new ObjectId(value.getId()));
		}
		
		if (value.getPeriod() != null) {
			writer.writeInt64(ScheduleCodec.SCHEDULE_PERIOD_KEY, value.getPeriod().longValue());
		}
		
		if (value.getTimeUnit() != null) {
			writer.writeString(ScheduleCodec.SCHEDULE_TIME_UNIT_KEY, value.getTimeUnit().name());
		}

		writer.writeEndDocument();
		
	}

	@Override
	public Class<Schedule> getEncoderClass() {
		return Schedule.class;
	}

	@Override
	public Schedule decode(BsonReader reader, DecoderContext decoderContext) {
		String id = null;
		Long period = null;
		ChronoUnit timeUnit = null;
		
		reader.readStartDocument();
		
		while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            
            if (fieldName.equals(ScheduleCodec.SCHEDULE_ID_KEY)) {
            	id = reader.readObjectId().toString();
            } else if (fieldName.equals(ScheduleCodec.SCHEDULE_PERIOD_KEY)) {
            	period = new Long(reader.readInt64());
            } else if (fieldName.equals(ScheduleCodec.SCHEDULE_TIME_UNIT_KEY)) {
            	timeUnit = ChronoUnit.valueOf(reader.readString());
            }
		}
		
		reader.readEndDocument();

		return new Schedule(id, period, timeUnit);
	}

	@Override
	public boolean documentHasId(Schedule schedule) {
		return schedule.getId() != null;
	}

	@Override
	public Schedule generateIdIfAbsentFromDocument(Schedule schedule) {
		if (!documentHasId(schedule)) {
			schedule.setId(new ObjectId().toString());
		}
		return schedule;
	}

	@Override
	public BsonValue getDocumentId(Schedule schedule) {
		if (!documentHasId(schedule)) {
			throw new IllegalStateException("The schedule does not contain an _id");
		}
		return new BsonString(schedule.getId());
	}

}
