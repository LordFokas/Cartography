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
import com.eerussianguy.blazemap.api.mapping.Layer;
import com.eerussianguy.blazemap.api.markers.MapLabel;
import com.eerussianguy.blazemap.engine.BlazeMapEngine;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.data.ClusterStore;
import lordfokas.cartography.data.IClusterConsumer;
import lordfokas.cartography.utils.Colors;
import lordfokas.cartography.utils.ImageHandler;
import lordfokas.cartography.utils.TFCBlockTypes;

public class DiscoveryClusterStore extends ClusterStore {
    private static final HashMap<ResourceKey<Level>, HashMap<String, DiscoveryDataPool>> NUGGETS = new HashMap<>();

    private static final DiscoveryConsumer NUGGET_CONSUMER = new DiscoveryConsumer(CartographyReferences.Layers.GEOLOGY, "nugget");

    @SubscribeEvent
    public static void onServerJoined(ServerJoinedEvent event) {
        NUGGETS.clear();
    }

    @SubscribeEvent
    public static void onDimensionChanged(DimensionChangedEvent event) {
        foreach(ClusterType.NUGGET, nugget -> BlazeMapEngine.async().runOnDataThread(() -> getNuggetPool(event.dimension, nugget)));
    }

    public static synchronized DiscoveryDataPool getNuggetPool(ResourceKey<Level> dimension, String nugget) {
        return NUGGETS
            .computeIfAbsent(dimension, $ -> new HashMap<>())
            .computeIfAbsent(nugget, $ -> new DiscoveryDataPool(
                storage(), getClusterNode(ClusterType.NUGGET, nugget),
                new DiscoveryClusterRealm(BlazeMapEngine.cruncher().getThreadAsserter(), NUGGET_CONSUMER),
                nugget
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
            ImageHandler.DynamicLabel dynamicLabel = ImageHandler.getLabel(pretty(name), icon);
            MapLabel label = new MapLabel(
                clusterID(cluster, type),
                Minecraft.getInstance().level.dimension(),
                center,
                layer,
                name,
                dynamicLabel.path,
                dynamicLabel.image.getWidth(),
                dynamicLabel.image.getHeight(),
                Colors.NO_TINT,
                0,
                true
            );
            BlazeMapEngine.async().runOnGameThread(() -> {
                var labels = labels();
                if(labels.has(label)) {
                    labels.remove(label);
                }
                labels.add(label);
            });
        }

        @Override
        public void dropCluster(DiscoveryCluster cluster) {
            BlazeMapEngine.async().runOnGameThread(() -> labels().remove(clusterID(cluster, type), layer));
        }
    }
}
