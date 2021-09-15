package lordfokas.cartography.modules.biology;

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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.awt.image.BufferedImage;
import java.util.Collection;

public class TreeClusterViewer implements IClusterViewer<TreeCluster> {
    private final ResourceKey<Level> dim;

    public TreeClusterViewer(ResourceKey<Level> dim){
        this.dim = dim;
    }

    @Override
    public void setVisible(boolean visible) {
        // TODO: implement this
    }

    @Override
    public void pushCluster(TreeCluster cluster) {
        IMarkerHandler handler = JMPlugin.instance();
        Collection<ITreeDataHandler.TreeSummary> data = cluster.getData();
        if(data.isEmpty()) return;
        BlockPos center = calculateCenterOfMass(cluster);

        int SCALE = 4;
        int s = (data.size()-1)*7;
        int o = 0;
        for(ITreeDataHandler.TreeSummary summary : data){
            String tree = summary.tree.replace("SAPLING:", "");
            String text = tree + " x" + summary.count;

            // TODO: exorcise cursed code
            // BufferedImage image = AsyncImageProxy.getImage(TFCBlockTypes.getTexturePath(summary.tree), SCALE);
            BufferedImage image = GameContainerClient.instance().getThreadHandler().getOnGameThreadBlocking($ -> ImageHandler.getLabel(text, TFCBlockTypes.getTexturePath(summary.tree), SCALE));

            String marker_key = "TreeCluster_"+tree+"_"+center.getX()+"_"+center.getZ();
            BlockPos marker_pos = center.offset(0, 0, s-o);
            Marker marker = new Marker(marker_key, dim, marker_pos.getX(), marker_pos.getZ(), image, SCALE, 2, MapType.BIOGEOGRAPHICAL);
            cluster.getKeys().add(marker_key);

            GameContainerClient.instance().getThreadHandler().runOnGameThreadBlocking(() -> handler.place(marker));

            o += 14;
        }
    }

    @Override
    public void dropCluster(TreeCluster cluster) {
        IMarkerHandler markers = JMPlugin.instance();
        GameContainerClient.instance().getThreadHandler().runOnGameThreadBlocking(() -> {
            for(String key : cluster.getKeys()){
                markers.delete(key);
            }
        });
        cluster.getKeys().clear();
    }

    private BlockPos calculateCenterOfMass(TreeCluster cluster){
        ChunkPos pos = cluster.getCoordinates().iterator().next();
        int x = pos.x >> 4;
        int z = pos.z >> 4;
        return new BlockPos((x<<8)+128, 0, (z<<8)+128);
    }
}
