package gr.cite.harvester.datastore.mongodb.codecs;

import gr.cite.harvester.datastore.model.Schedule;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class ScheduleCodecProvider implements CodecProvider {

	@SuppressWarnings("unchecked")
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		if (clazz == Schedule.class) {
			return (Codec<T>) new ScheduleCodec(registry);
		}
		return null;
	}
}
