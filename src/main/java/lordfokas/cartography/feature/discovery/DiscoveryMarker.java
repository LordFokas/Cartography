package lordfokas.cartography.feature.discovery;

import java.util.Collection;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.maps.Layer;
import com.eerussianguy.blazemap.api.markers.MapLabel;
import com.eerussianguy.blazemap.api.markers.ObjectRenderer;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.utils.Colors;

public class DiscoveryMarker extends MapLabel {
    final byte[] offsets;
    final ResourceLocation item;
    final boolean depleted;

    public DiscoveryMarker(ResourceLocation id, DiscoveryCluster cluster, ResourceKey<Level> dimension, BlockPos position, BlazeRegistry.Key<Layer> layerID, ResourceLocation item, ResourceLocation icon, int width, int height, Set<String> tags) {
        super(id, dimension, position, layerID, icon, width, height, Colors.NO_TINT, 0, false, tags);
        this.depleted = cluster.getData().isDepleted();
        this.item = item;
        Collection<BlockPos> dots = cluster.getCoordinates();
        this.offsets = new byte[dots.size() * 2];
        int idx = 0;
        for(BlockPos dot : dots) {
            offsets[idx++] = (byte) (dot.getX() - position.getX());
            offsets[idx++] = (byte) (dot.getZ() - position.getZ());
        }
    }

    @Override
    public BlazeRegistry.Key<ObjectRenderer<?>> getRenderer() {
        return CartographyReferences.Renderers.DISCOVERY_MARKER;
    }
}
