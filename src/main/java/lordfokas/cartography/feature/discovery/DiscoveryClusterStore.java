package lordfokas.cartography.feature.discovery;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.event.DimensionChangedEvent;
import com.eerussianguy.blazemap.api.event.ServerJoinedEvent;
import com.eerussianguy.blazemap.api.pipeline.Layer;
import com.eerussianguy.blazemap.api.markers.MapLabel;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.data.ClusterStore;
import lordfokas.cartography.data.IClusterConsumer;
import lordfokas.cartography.utils.BMEngines;
import lordfokas.cartography.utils.Colors;
import lordfokas.cartography.utils.TFCBlockTypes;

public class DiscoveryClusterStore extends ClusterStore {
    private static final HashMap<ResourceKey<Level>, HashMap<String, DiscoveryDataPool>> NUGGETS = new HashMap<>();
    private static final HashMap<ResourceKey<Level>, HashMap<String, DiscoveryDataPool>> FRUITS = new HashMap<>();
    private static final HashMap<ResourceKey<Level>, HashMap<String, DiscoveryDataPool>> CROPS = new HashMap<>();

    private static final DiscoveryConsumer NUGGET_CONSUMER = new DiscoveryConsumer(CartographyReferences.Layers.GEOLOGY, "nugget");
    private static final DiscoveryConsumer FRUIT_CONSUMER = new DiscoveryConsumer(CartographyReferences.Layers.ECOSYSTEM, "fruit");
    private static final DiscoveryConsumer CROP_CONSUMER = new DiscoveryConsumer(CartographyReferences.Layers.ECOSYSTEM, "crop");

    @SubscribeEvent
    public static void onServerJoined(ServerJoinedEvent event) {
        NUGGETS.clear();
        FRUITS.clear();
        CROPS.clear();
    }

    @SubscribeEvent
    public static void onDimensionChanged(DimensionChangedEvent event) {
        foreach(ClusterType.NUGGET, nugget -> BMEngines.async().runOnDataThread(() -> getNuggetPool(event.dimension, nugget)));
        foreach(ClusterType.FRUIT, fruit -> BMEngines.async().runOnDataThread(() -> getFruitPool(event.dimension, fruit)));
        foreach(ClusterType.CROP, crop -> BMEngines.async().runOnDataThread(() -> getCropPool(event.dimension, crop)));
    }

    public static synchronized DiscoveryDataPool getNuggetPool(ResourceKey<Level> dimension, String nugget) {
        return NUGGETS
            .computeIfAbsent(dimension, $ -> new HashMap<>())
            .computeIfAbsent(nugget, $ -> new DiscoveryDataPool(
                storage(), getClusterNode(ClusterType.NUGGET, nugget),
                new DiscoveryClusterRealm(BMEngines.cruncher().getThreadAsserter(), NUGGET_CONSUMER),
                nugget
            ));
    }

    public static synchronized DiscoveryDataPool getFruitPool(ResourceKey<Level> dimension, String fruit) {
        return FRUITS
            .computeIfAbsent(dimension, $ -> new HashMap<>())
            .computeIfAbsent(fruit, $ -> new DiscoveryDataPool(
                storage(), getClusterNode(ClusterType.FRUIT, fruit),
                new DiscoveryClusterRealm(BMEngines.cruncher().getThreadAsserter(), FRUIT_CONSUMER),
                fruit
            ));
    }

    public static synchronized DiscoveryDataPool getCropPool(ResourceKey<Level> dimension, String crop) {
        return CROPS
            .computeIfAbsent(dimension, $ -> new HashMap<>())
            .computeIfAbsent(crop, $ -> new DiscoveryDataPool(
                storage(), getClusterNode(ClusterType.CROP, crop),
                new DiscoveryClusterRealm(BMEngines.cruncher().getThreadAsserter(), CROP_CONSUMER),
                crop
            ));
    }

    private static final class DiscoveryConsumer implements IClusterConsumer<DiscoveryCluster> {
        private final BlazeRegistry.Key<Layer> layer;
        private final String type;

        private DiscoveryConsumer(BlazeRegistry.Key<Layer> layer, String type) {
            this.layer = layer;
            this.type = type;
        }

        @Override
        public void pushCluster(DiscoveryCluster cluster) {
            BlockPos center = cluster.centerOfMass();
            if(center == null) return;
            String name = cluster.getData();

            ResourceLocation icon = switch(type) {
                case "nugget" -> TFCBlockTypes.getNuggetTexturePath(name);
                case "fruit" -> TFCBlockTypes.getFruitTexturePath(name);
                case "crop" -> TFCBlockTypes.getCropTexturePath(name);
                default -> null;
            };
            // ImageHandler.DynamicLabel dynamicLabel = ImageHandler.getLabel(pretty(name), icon);
            MapLabel label = new MapLabel(
                clusterID(cluster, type),
                Minecraft.getInstance().level.dimension(),
                center,
                layer,
                name,
                icon, //dynamicLabel.path,
                32, //dynamicLabel.image.getWidth(),
                32, //dynamicLabel.image.getHeight(),
                Colors.NO_TINT,
                0,
                true
            );
            BMEngines.async().runOnGameThread(() -> {
                var labels = labels();
                if(labels.has(label)) {
                    labels.remove(label);
                }
                labels.add(label);
            });
        }

        @Override
        public void dropCluster(DiscoveryCluster cluster) {
            BMEngines.async().runOnGameThread(() -> labels().remove(clusterID(cluster, type), layer));
        }
    }
}
