package lordfokas.cartography.feature.environment.climate;

import java.util.Collections;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.event.DimensionChangedEvent;
import com.eerussianguy.blazemap.api.event.ServerJoinedEvent;
import com.eerussianguy.blazemap.api.maps.Layer;
import com.eerussianguy.blazemap.api.markers.MapLabel;
import com.eerussianguy.blazemap.engine.BlazeMapAsync;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.data.ClusterStore;
import lordfokas.cartography.data.IClusterConsumer;
import lordfokas.cartography.feature.mapping.climate.RainfallIsolinesLayer;
import lordfokas.cartography.feature.mapping.climate.TemperatureIsolinesLayer;
import lordfokas.cartography.utils.Colors;
import lordfokas.cartography.utils.ImageHandler;

public class ClimateClusterStore extends ClusterStore {
    private static final HashMap<ResourceKey<Level>, HashMap<String, ClimateDataPool>> RAINFALL = new HashMap<>();
    private static final HashMap<ResourceKey<Level>, HashMap<String, ClimateDataPool>> TEMPERATURE = new HashMap<>();
    private static final ClimateConsumer RAIN_CONSUMER = new ClimateConsumer(CartographyReferences.Layers.RAINFALL_ISO, Colors.argb2abgr(RainfallIsolinesLayer.RAINFALL_BLUE), "rainfall");
    private static final ClimateConsumer TEMP_CONSUMER = new ClimateConsumer(CartographyReferences.Layers.TEMPERATURE_ISO, Colors.argb2abgr(TemperatureIsolinesLayer.TEMPERATURE_RED), "temperature");

    @SubscribeEvent
    public static void onServerJoined(ServerJoinedEvent event) {
        RAINFALL.clear();
        TEMPERATURE.clear();
    }

    @SubscribeEvent
    public static void onDimensionChanged(DimensionChangedEvent event) {
        foreach(ClusterType.RAINFALL, value -> BlazeMapAsync.instance().clientChain.runOnDataThread(() -> getRainfallPool(event.dimension, value)));
        foreach(ClusterType.TEMPERATURE, value -> BlazeMapAsync.instance().clientChain.runOnDataThread(() -> getTemperaturePool(event.dimension, value)));
    }

    public static synchronized ClimateDataPool getRainfallPool(ResourceKey<Level> dimension, String value) {
        return RAINFALL
            .computeIfAbsent(dimension, $ -> new HashMap<>())
            .computeIfAbsent(value, $ -> new ClimateDataPool(
                storage(), getClusterNode(ClusterType.RAINFALL, value),
                new ClimateClusterRealm(BlazeMapAsync.instance().cruncher.getThreadAsserter(), RAIN_CONSUMER),
                value, "mm"));
    }

    public static synchronized ClimateDataPool getTemperaturePool(ResourceKey<Level> dimension, String value) {
        return TEMPERATURE
            .computeIfAbsent(dimension, $ -> new HashMap<>())
            .computeIfAbsent(value, $ -> new ClimateDataPool(
                storage(), getClusterNode(ClusterType.TEMPERATURE, value),
                new ClimateClusterRealm(BlazeMapAsync.instance().cruncher.getThreadAsserter(), TEMP_CONSUMER),
                value, "*C"));
    }

    private static final class ClimateConsumer implements IClusterConsumer<ClimateCluster> {
        private final BlazeRegistry.Key<Layer> layer;
        private final int color;
        private final String type;

        private ClimateConsumer(BlazeRegistry.Key<Layer> layer, int color, String type) {
            this.layer = layer;
            this.color = color;
            this.type = type;
        }

        @Override
        public void pushCluster(ClimateCluster cluster) {
            Isoline.Curve center = cluster.getCenterPoint();
            if(center == null) return;
            ResourceKey<Level> dimension = Minecraft.getInstance().level.dimension();
            Isoline isoline = cluster.getData();

            String text = String.format(" %s %s ", isoline.value, isoline.unit);
            ImageHandler.DynamicLabel dynamicLabel = ImageHandler.getLabel(text);
            MapLabel label = new MapLabel(
                clusterID(cluster, type),
                dimension,
                center.chunk.getBlockAt(center.mx, 0, center.my),
                layer,
                dynamicLabel.path,
                dynamicLabel.image.getWidth(),
                dynamicLabel.image.getHeight(),
                color,
                center.angle,
                true,
                Collections.EMPTY_SET
            );
            BlazeMapAsync.instance().clientChain.runOnGameThread(() -> {
                var labels = labels();
                if(labels.has(label)) {
                    labels.remove(label);
                }
                labels.add(label);
            });
        }

        @Override
        public void dropCluster(ClimateCluster cluster) {
            BlazeMapAsync.instance().clientChain.runOnGameThread(() -> labels().remove(clusterID(cluster, type), layer));
        }
    }
}
