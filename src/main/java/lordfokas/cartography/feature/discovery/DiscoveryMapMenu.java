package lordfokas.cartography.feature.discovery;

import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.eerussianguy.blazemap.api.BlazeMapReferences;
import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.event.DimensionChangedEvent;
import com.eerussianguy.blazemap.api.event.MapMenuSetupEvent;
import com.eerussianguy.blazemap.api.event.MapMenuSetupEvent.*;
import com.eerussianguy.blazemap.api.maps.Layer;
import com.eerussianguy.blazemap.api.markers.IMarkerStorage;
import com.eerussianguy.blazemap.api.markers.Waypoint;
import lordfokas.cartography.Cartography;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.utils.ImageHandler;
import lordfokas.cartography.utils.TFCBlockTypes;

public class DiscoveryMapMenu {
    private static IMarkerStorage<Waypoint> waypointStore;
    private static ResourceKey<Level> dimension;

    @SubscribeEvent
    public static void trackDimensionChanges(DimensionChangedEvent event) {
        waypointStore = event.waypoints;
        dimension = event.dimension;
    }

    @SubscribeEvent
    public static void enrichMenu(MapMenuSetupEvent event) {
        BlockPos pos = new BlockPos(event.blockPosX, 0, event.blockPosZ);
        enrichMenu(DiscoveryClusterStore::foreachCropPool, TFCBlockTypes::getCropTexturePath, CartographyReferences.Layers.Fake.CROPS, event, pos);
        enrichMenu(DiscoveryClusterStore::foreachFruitPool, TFCBlockTypes::getFruitTexturePath, CartographyReferences.Layers.Fake.FRUIT, event, pos);
        enrichMenu(DiscoveryClusterStore::foreachNuggetPool, TFCBlockTypes::getNuggetTexturePath, CartographyReferences.Layers.Fake.ORES, event, pos);
    }

    private static void enrichMenu(BiConsumer<ResourceKey<Level>, BiConsumer<String, DiscoveryDataPool>> foreach, Function<String, ResourceLocation> iconProvider, BlazeRegistry.Key<Layer> layer, MapMenuSetupEvent event, BlockPos pos) {
        if(!event.layers.contains(layer)) return;
        foreach.accept(dimension, (type, pool) -> {
            pool.asClustered(clusters -> {
                clusters.nearbyXZ(pos, 400, cluster -> {
                    ResourceLocation icon = iconProvider.apply(type);
                    event.root.add(new MenuFolder(Cartography.resource("cluster.discovery."+type), icon, new TextComponent(type),
                        new MenuAction(Cartography.resource("cluster.discovery.waypoint"), BlazeMapReferences.Icons.WAYPOINT, ImageHandler.getColor(icon), new TextComponent("Make Waypoint"), () -> {
                            waypointStore.add(new Waypoint(Cartography.resource("cluster.discovery."+cluster.hashCode()), dimension, pos, type, icon));
                        }),
                        new MenuAction(Cartography.resource("cluster.discovery.depleted"), new TextComponent("Mark Depleted"), null)
                    ));
                });
            });
        });
    }
}
