package lordfokas.cartography.feature.discovery;

import java.util.HashMap;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.eerussianguy.blazemap.api.BlazeRegistry.Key;
import com.eerussianguy.blazemap.api.event.DimensionChangedEvent;
import com.eerussianguy.blazemap.api.event.ServerJoinedEvent;
import com.eerussianguy.blazemap.api.maps.Layer;
import com.eerussianguy.blazemap.engine.BlazeMapAsync;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.data.ClusterStore;
import lordfokas.cartography.data.IClusterConsumer;
import lordfokas.cartography.utils.Colors;
import lordfokas.cartography.utils.ImageHandler;
import lordfokas.cartography.feature.TFCContent;

public class DiscoveryClusterStore extends ClusterStore {
    private static final HashMap<ResourceKey<Level>, HashMap<String, DiscoveryDataPool>> NUGGETS = new HashMap<>();
    private static final HashMap<ResourceKey<Level>, HashMap<String, DiscoveryDataPool>> FRUITS = new HashMap<>();
    private static final HashMap<ResourceKey<Level>, HashMap<String, DiscoveryDataPool>> CROPS = new HashMap<>();
    private static final DiscoveryConsumer NUGGET_CONSUMER = new DiscoveryConsumer(CartographyReferences.Layers.Fake.ORES, "nugget", TFCContent::getNuggetTexturePath, TFCContent::getOreTags);
    private static final DiscoveryConsumer FRUIT_CONSUMER = new DiscoveryConsumer(CartographyReferences.Layers.Fake.FRUIT, "fruit", TFCContent::getFruitTexturePath, TFCContent::getFruitTags);
    private static final DiscoveryConsumer CROP_CONSUMER = new DiscoveryConsumer(CartographyReferences.Layers.Fake.CROPS, "crop", TFCContent::getCropTexturePath, TFCContent::getCropTags);

    @SubscribeEvent
    public static void onServerJoined(ServerJoinedEvent event) {
        NUGGETS.clear();
        FRUITS.clear();
        CROPS.clear();
    }

    @SubscribeEvent
    public static void onDimensionChanged(DimensionChangedEvent event) {
        foreach(ClusterType.NUGGET, nugget -> BlazeMapAsync.instance().clientChain.runOnDataThread(() -> getNuggetPool(event.dimension, nugget)));
        foreach(ClusterType.FRUIT, fruit -> BlazeMapAsync.instance().clientChain.runOnDataThread(() -> getFruitPool(event.dimension, fruit)));
        foreach(ClusterType.CROP, crop -> BlazeMapAsync.instance().clientChain.runOnDataThread(() -> getCropPool(event.dimension, crop)));
    }

    public static synchronized void foreachNuggetPool(ResourceKey<Level> dimension, BiConsumer<String, DiscoveryDataPool> consumer) {
        NUGGETS.computeIfAbsent(dimension, $ -> new HashMap<>()).forEach(consumer);
    }

    public static synchronized void foreachFruitPool(ResourceKey<Level> dimension, BiConsumer<String, DiscoveryDataPool> consumer) {
        FRUITS.computeIfAbsent(dimension, $ -> new HashMap<>()).forEach(consumer);
    }

    public static synchronized void foreachCropPool(ResourceKey<Level> dimension, BiConsumer<String, DiscoveryDataPool> consumer) {
        CROPS.computeIfAbsent(dimension, $ -> new HashMap<>()).forEach(consumer);
    }

    public static synchronized DiscoveryDataPool getNuggetPool(ResourceKey<Level> dimension, String nugget) {
        return NUGGETS
            .computeIfAbsent(dimension, $ -> new HashMap<>())
            .computeIfAbsent(nugget, $ -> new DiscoveryDataPool(
                storage(), getClusterNode(ClusterType.NUGGET, nugget),
                new DiscoveryClusterRealm(BlazeMapAsync.instance().cruncher.getThreadAsserter(), NUGGET_CONSUMER, nugget)
            ));
    }

    public static synchronized DiscoveryDataPool getFruitPool(ResourceKey<Level> dimension, String fruit) {
        return FRUITS
            .computeIfAbsent(dimension, $ -> new HashMap<>())
            .computeIfAbsent(fruit, $ -> new DiscoveryDataPool(
                storage(), getClusterNode(ClusterType.FRUIT, fruit),
                new DiscoveryClusterRealm(BlazeMapAsync.instance().cruncher.getThreadAsserter(), FRUIT_CONSUMER, fruit)
            ));
    }

    public static synchronized DiscoveryDataPool getCropPool(ResourceKey<Level> dimension, String crop) {
        return CROPS
            .computeIfAbsent(dimension, $ -> new HashMap<>())
            .computeIfAbsent(crop, $ -> new DiscoveryDataPool(
                storage(), getClusterNode(ClusterType.CROP, crop),
                new DiscoveryClusterRealm(BlazeMapAsync.instance().cruncher.getThreadAsserter(), CROP_CONSUMER, crop)
            ));
    }

    private static final class DiscoveryConsumer implements IClusterConsumer<DiscoveryCluster> {
        private final Key<Layer> layer;
        private final String type;
        private final Function<String, ResourceLocation> textureSupplier;
        private final Function<String, Set<String>> tagSupplier;

        private DiscoveryConsumer(Key<Layer> layer, String type, Function<String, ResourceLocation> textureSupplier, Function<String, Set<String>> tagSupplier) {
            this.layer = layer;
            this.type = type;
            this.textureSupplier = textureSupplier;
            this.tagSupplier = tagSupplier;
        }

        @Override
        public void pushCluster(DiscoveryCluster cluster) {
            BlockPos center = cluster.centerOfMass();
            if(center == null) return;
            String name = cluster.type;

            ResourceLocation item = textureSupplier.apply(name);
            int tint = cluster.getData().isDepleted() ? 0xFFFF0000 : Colors.NO_TINT;
            ImageHandler.DynamicLabel dynamicLabel = ImageHandler.getLabel(pretty(name), item, tint);
            Set<String> tags = tagSupplier.apply(name);
            DiscoveryMarker marker = new DiscoveryMarker(
                clusterID(cluster, type),
                cluster,
                Minecraft.getInstance().level.dimension(),
                center,
                layer,
                item,
                dynamicLabel.path,
                dynamicLabel.image.getWidth(),
                dynamicLabel.image.getHeight(),
                tags
            );
            BlazeMapAsync.instance().clientChain.runOnGameThread(() -> {
                var labels = labels();
                if(labels.has(marker)) {
                    labels.remove(marker);
                }
                labels.add(marker);
            });
        }

        @Override
        public void dropCluster(DiscoveryCluster cluster) {
            BlazeMapAsync.instance().clientChain.runOnGameThread(() -> labels().remove(clusterID(cluster, type), layer));
        }
    }
}
