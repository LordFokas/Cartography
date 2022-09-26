package lordfokas.cartography.feature.data;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.eerussianguy.blazemap.api.event.DimensionChangedEvent;
import com.eerussianguy.blazemap.api.markers.IMarkerStorage;
import com.eerussianguy.blazemap.api.markers.MapLabel;
import com.eerussianguy.blazemap.engine.BlazeMapEngine;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.mapping.RainfallIsolinesLayer;
import lordfokas.cartography.feature.mapping.TemperatureIsolinesLayer;
import lordfokas.cartography.utils.Colors;
import lordfokas.cartography.utils.ImageHandler;

public class ClimateClusterStore {
    private static IMarkerStorage.Layered<MapLabel> labels;

    @SubscribeEvent
    public static void onDimensionChanged(DimensionChangedEvent event) {
        labels = event.labels;
    }

    public static void add(ResourceKey<Level> dimension, ChunkPos chunk, int mx, int my, float angle, int value, String unit) {
        if((chunk.x + chunk.z) % 5 != 0) return;
        boolean isRainfall = unit.equals("mm");
        String text = String.format(" %d %s ", value, unit);
        ImageHandler.DynamicLabel dynamicLabel = ImageHandler.getLabel(text);
        MapLabel label = new MapLabel(
            Cartography.resource("clusters/climate/" + System.nanoTime()),
            dimension,
            chunk.getBlockAt(mx, 0, my),
            isRainfall ? CartographyReferences.Layers.RAINFALL_ISO : CartographyReferences.Layers.TEMPERATURE_ISO,
            text,
            dynamicLabel.path,
            dynamicLabel.image.getWidth(),
            dynamicLabel.image.getHeight(),
            Colors.argb2abgr(isRainfall ? RainfallIsolinesLayer.RAINFALL_BLUE : TemperatureIsolinesLayer.TEMPERATURE_RED),
            angle,
            true
        );
        BlazeMapEngine.async().runOnGameThread(() -> {
            if(labels.has(label)) {
                labels.remove(label);
            }
            labels.add(label);
        });
    }
}
