package gt.cite.earthserver.harvester.datastore.mongodb.codecs;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import gr.cite.earthserver.harvester.datastore.model.Harvest;

public class HarvestCodecProvider implements CodecProvider {
	@SuppressWarnings("unchecked")
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		if (clazz == Harvest.class) {
			return (Codec<T>) new HarvestCodec(registry);
		}
		return null;
	}
}
