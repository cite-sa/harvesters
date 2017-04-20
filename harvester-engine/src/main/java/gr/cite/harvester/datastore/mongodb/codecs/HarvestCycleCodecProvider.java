package gr.cite.harvester.datastore.mongodb.codecs;

import gr.cite.harvester.datastore.model.HarvestCycle;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class HarvestCycleCodecProvider implements CodecProvider {
    @SuppressWarnings("unchecked")
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == HarvestCycle.class) {
            return (Codec<T>) new HarvestCycleCodec(registry);
        }
        return null;
    }
}
