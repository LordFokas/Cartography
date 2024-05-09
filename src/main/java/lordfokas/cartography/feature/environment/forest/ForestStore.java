package lordfokas.cartography.feature.environment.forest;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.eerussianguy.blazemap.api.event.DimensionChangedEvent;
import com.eerussianguy.blazemap.api.markers.IMarkerStorage;
import com.eerussianguy.blazemap.api.markers.MapLabel;
import com.eerussianguy.blazemap.api.util.IStorageAccess;
import com.eerussianguy.blazemap.api.util.RegionPos;
import com.eerussianguy.blazemap.engine.BlazeMapAsync;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.data.IClusterConsumer;
import lordfokas.cartography.feature.TFCContent;
import lordfokas.cartography.utils.Colors;
import lordfokas.cartography.utils.ImageHandler;

public class ForestStore {
    private static final Map<ResourceKey<Level>, ForestStore> STORES = new HashMap<>();
    private static final ForestConsumer CONSUMER = new ForestConsumer();
    private static IMarkerStorage.Layered<MapLabel> labels;
    private static IStorageAccess dimensionStorage;
    private static ResourceKey<Level> dimension;

    private final Map<RegionPos, ForestRegion> stores = new HashMap<>();

    public static synchronized ForestRegion getStore(ResourceKey<Level> dimension, RegionPos region) {
        return STORES.computeIfAbsent(dimension, $ -> new ForestStore()).getRegion(region);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDimensionChanged(DimensionChangedEvent event) {
        labels = event.labels;
        STORES.clear();
        dimensionStorage = event.dimensionStorage;
        dimension = event.dimension;
    }

    public synchronized ForestRegion getRegion(RegionPos region) {
        return stores.computeIfAbsent(region, r -> new ForestRegion(r, CONSUMER));
    }

    public static ResourceLocation forestID(Forest forest) {
        return Cartography.resource("forest/" + forest.key);
    }

    private static class ForestConsumer implements IClusterConsumer<Forest> {

        @Override
        public void pushCluster(Forest forest) {
            ImageHandler.DynamicLabel label = ImageHandler.getLabel(
                forest.pretty + " x" + forest.size(),
                TFCContent.getTreeTexturePath(forest.tree)
            );
            MapLabel marker = new MapLabel(
                forestID(forest),
                dimension,
                BlockPos.ZERO,
                CartographyReferences.Layers.Fake.FOREST,
                label.path,
                label.image.getWidth(),
                label.image.getHeight(),
                Colors.NO_TINT,
                0.0F,
                true,
                TFCContent.getTreeTags(forest.tree)
            );
            marker.setPositionObject(new ForestMarkerPos(forest));
            BlazeMapAsync.instance().clientChain.runOnGameThread(() -> {
                if(labels.has(marker)) {
                    labels.remove(marker);
                }
                labels.add(marker);
            });
        }

        @Override
        public void dropCluster(Forest forest) {
            BlazeMapAsync.instance().clientChain.runOnGameThread(() -> labels.remove(forestID(forest), CartographyReferences.Layers.Fake.FOREST));
        }
    }

    private static class ForestMarkerPos extends BlockPos.MutableBlockPos {
        private final Forest forest;

        private ForestMarkerPos(Forest forest) {
            this.forest = forest;
        }

        @Override
        public int getX() {
            return (forest.region.x << 9) + 256;
        }

        @Override
        public int getZ() {
            return (forest.region.z << 9) + 256 - (forest.total * 16) + (forest.index * 32);
        }
    }
}
