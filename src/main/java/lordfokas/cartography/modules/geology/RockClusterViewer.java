package lordfokas.cartography.modules.geology;

import lordfokas.cartography.core.GameContainerClient;
import lordfokas.cartography.core.ImageHandler;
import lordfokas.cartography.core.MapType;
import lordfokas.cartography.core.data.IClusterViewer;
import lordfokas.cartography.core.markers.IMarkerHandler;
import lordfokas.cartography.core.markers.Marker;
import lordfokas.cartography.integration.journeymap.JMPlugin;
import lordfokas.cartography.integration.terrafirmacraft.TFCBlockTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.awt.image.BufferedImage;

public class RockClusterViewer implements IClusterViewer<RockCluster> {
    private final ResourceKey<Level> dim;

    public RockClusterViewer(ResourceKey<Level> dim){
        this.dim = dim;
    }

    @Override
    public void setVisible(boolean visible) {
        // TODO: implement this
    }

    @Override
    public void pushCluster(RockCluster cluster) {
        if(cluster.getCoordinates().size() < 10) return;
        String rock = cluster.getData();
        ResourceLocation icon = TFCBlockTypes.getLooseRockTexturePath(rock);

        int SCALE = 4;
        BlockPos center = calculateCenterOfMass(cluster);
        BufferedImage image = GameContainerClient.instance().getThreadHandler().getOnGameThreadBlocking($ -> ImageHandler.getLabel(rock, icon, SCALE));

        String marker_key = "RockCluster_"+rock+"_"+center.getX()+"_"+center.getZ();
        Marker marker = new Marker(marker_key, dim, center.getX(), center.getZ(), image, SCALE, 1, MapType.GEOLOGICAL);
        cluster.getKeys().add(marker_key);

        GameContainerClient.instance().getThreadHandler().runOnGameThreadBlocking(() -> JMPlugin.instance().place(marker));
    }

    @Override
    public void dropCluster(RockCluster cluster) {
        IMarkerHandler markers = JMPlugin.instance();
        GameContainerClient.instance().getThreadHandler().runOnGameThreadBlocking(() -> {
            for(String key : cluster.getKeys()){
                markers.delete(key);
            }
        });
        cluster.getKeys().clear();
    }

    private BlockPos calculateCenterOfMass(RockCluster cluster){
        int sx = 0, sz = 0, c = 0;
        for(ChunkPos pos : cluster.getCoordinates()){
            int x = pos.x << 4;
            int z = pos.z << 4;
            sx += x+8;
            sz += z+8;
            c++;
        }
        return new BlockPos(sx/c, 0, sz/c);
    }
}
