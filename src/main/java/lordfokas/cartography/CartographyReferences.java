package lordfokas.cartography;

import com.eerussianguy.blazemap.api.BlazeRegistry.Key;
import com.eerussianguy.blazemap.api.mapping.*;

import static com.eerussianguy.blazemap.api.BlazeMapAPI.*;

public class CartographyReferences {
    public static final String MOD_ID = "cartography";

    public static class Collectors {
        public static final Key<Collector<MasterDatum>> GROUND_COMPOSITION = new Key<>(COLLECTORS, MOD_ID, "ground_composition");
        public static final Key<Collector<MasterDatum>> CLIMATE = new Key<>(COLLECTORS, MOD_ID, "climate");
    }

    public static class Layers {
        public static final Key<Layer> RAINFALL = new Key<>(LAYERS, MOD_ID, "rainfall");
        public static final Key<Layer> RAINFALL_ISO = new Key<>(LAYERS, MOD_ID, "rainfall_iso");
        public static final Key<Layer> TEMPERATURE = new Key<>(LAYERS, MOD_ID, "temperature");
        public static final Key<Layer> TEMPERATURE_ISO = new Key<>(LAYERS, MOD_ID, "temperature_iso");
        public static final Key<Layer> ECOSYSTEM = new Key<>(LAYERS, MOD_ID, "ecosystem");
        public static final Key<Layer> GEOLOGY = new Key<>(LAYERS, MOD_ID, "geology");
    }

    public static class MapTypes {
        public static final Key<MapType> RAINFALL = new Key<>(MAPTYPES, MOD_ID, "rainfall");
        public static final Key<MapType> TEMPERATURE = new Key<>(MAPTYPES, MOD_ID, "temperature");
        public static final Key<MapType> ECOSYSTEM = new Key<>(MAPTYPES, MOD_ID, "ecosystem");
        public static final Key<MapType> GEOLOGY = new Key<>(MAPTYPES, MOD_ID, "geology");
    }

    public static class Processors {
        public static final Key<Processor> TREE_COUNT = new Key<>(PROCESSORS, MOD_ID, "tree_count");
        public static final Key<Processor> ROCK_LAYER = new Key<>(PROCESSORS, MOD_ID, "rock_layer");
        public static final Key<Processor> CLIMATE = new Key<>(PROCESSORS, MOD_ID, "climate");
    }
}
