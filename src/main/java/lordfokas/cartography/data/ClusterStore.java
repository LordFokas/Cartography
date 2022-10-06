package lordfokas.cartography.data;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.eerussianguy.blazemap.api.event.DimensionChangedEvent;
import com.eerussianguy.blazemap.api.markers.IMarkerStorage;
import com.eerussianguy.blazemap.api.markers.MapLabel;
import com.eerussianguy.blazemap.api.util.IStorageAccess;
import com.eerussianguy.blazemap.api.util.MinecraftStreams;
import lordfokas.cartography.Cartography;

public class ClusterStore {
    private static final ResourceLocation DIRECTORY = Cartography.resource("clusters.bin");
    private static final HashMap<String, ClusterType> LOOKUP = new HashMap<>();

    private static IStorageAccess dimensionStorage;
    private static IMarkerStorage.Layered<MapLabel> labels;
    private static final EnumMap<ClusterType, List<String>> CLUSTERS = new EnumMap<>(ClusterType.class);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDimensionChanged(DimensionChangedEvent event) {
        labels = event.labels;
        CLUSTERS.clear();
        dimensionStorage = event.dimensionStorage;
        if(!dimensionStorage.exists(DIRECTORY)) return;
        try(MinecraftStreams.Input input = dimensionStorage.read(DIRECTORY)) {
            int types = input.readInt();
            for(int i = 0; i < types; i++) {
                String name = input.readUTF();
                ClusterType type = LOOKUP.get(name);
                List<String> names = new ArrayList<>();
                CLUSTERS.put(type, names);
                int count = input.readInt();
                for(int j = 0; j < count; j++) {
                    names.add(input.readUTF());
                }
            }
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    protected static IMarkerStorage.Layered<MapLabel> labels() {
        return labels;
    }

    protected static IStorageAccess storage() {
        return dimensionStorage;
    }

    protected static ResourceLocation getClusterNode(ClusterType type, String name) {
        List<String> names = CLUSTERS.computeIfAbsent(type, $ -> new ArrayList<>());
        if(!names.contains(name)) {
            names.add(name);
            try(MinecraftStreams.Output output = dimensionStorage.write(DIRECTORY)) {
                output.writeInt(CLUSTERS.size());
                for(Map.Entry<ClusterType, List<String>> entry : CLUSTERS.entrySet()) {
                    ClusterType k = entry.getKey();
                    List<String> v = entry.getValue();
                    output.writeUTF(k.name);
                    output.writeInt(v.size());
                    for(String cluster : v) {
                        output.writeUTF(cluster);
                    }
                }
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        return Cartography.resource(type.name + "/" + name + ".bin");
    }

    protected static void foreach(ClusterType type, Consumer<String> consumer) {
        CLUSTERS.computeIfAbsent(type, $ -> new ArrayList<>()).forEach(consumer);
    }

    protected static ResourceLocation clusterID(Cluster<?, ?> cluster, String type) {
        return Cartography.resource("clusters/" + type + "/" + cluster.hashCode());
    }

    protected static String pretty(String str) {
        return Arrays.stream(str.split("_")).map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1)).collect(Collectors.joining(" "));
    }

    protected enum ClusterType {
        RAINFALL("cluster_rainfall"),
        TEMPERATURE("cluster_temperature"),
        ROCKS("cluster_rock"),
        NUGGET("cluster_nugget"),
        FRUIT("cluster_fruit"),
        CROP("cluster_crop");

        private final String name;

        ClusterType(String name) {
            this.name = name;
            LOOKUP.put(name, this);
        }
    }
}
