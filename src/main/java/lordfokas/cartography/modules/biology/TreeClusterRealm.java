package lordfokas.cartography.modules.biology;

import lordfokas.cartography.core.AsyncImageProxy;
import lordfokas.cartography.core.ImageHandler;
import lordfokas.cartography.core.MapType;
import lordfokas.cartography.core.data.ClusterRealm;
import lordfokas.cartography.core.data.ThreadHandler;
import lordfokas.cartography.core.markers.IMarkerHandler;
import lordfokas.cartography.core.markers.Marker;
import lordfokas.cartography.integration.journeymap.JMPlugin;
import lordfokas.cartography.integration.terrafirmacraft.TFCBlockTypes;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.awt.image.BufferedImage;
import java.util.*;

class TreeClusterRealm extends ClusterRealm<ChunkPos, Collection<ITreeDataHandler.TreeSummary>, String, TreeCluster> {
    private final HashMap<ChunkPos, Collection<ITreeDataHandler.TreeSummary>> data;
    private final RegistryKey<World> dim;

    public TreeClusterRealm(RegistryKey<World> dim, HashMap<ChunkPos, Collection<ITreeDataHandler.TreeSummary>> data){
        this.dim = dim;
        this.data = data;
    }

    @Override
    protected boolean isInRange(ChunkPos coordinate, Collection<ChunkPos> cluster) {
        int x = coordinate.x / 16;
        int z = coordinate.z / 16;
        ChunkPos pos = cluster.iterator().next();
        return pos.x / 16 == x && pos.z / 16 == z;
    }

    @Override
    protected boolean isIncluded(ChunkPos coordinate, Collection<ChunkPos> cluster) {
        for(ChunkPos target : cluster){
            if(target.equals(coordinate)){
                return true;
            }
        }
        return false;
    }

    @Override
    protected TreeCluster merge(TreeCluster added, Iterable<TreeCluster> existing) {
        ArrayList<ChunkPos> coordinates = new ArrayList<>(64);
        TreeCounter counter = new TreeCounter();
        ArrayList<String> keys = new ArrayList<>(8);

        coordinates.addAll(added.getCoordinates());
        counter.add(added.getData());
        keys.addAll(added.getKeys());

        for(TreeCluster cluster : existing){
            coordinates.addAll(cluster.getCoordinates());
            counter.add(cluster.getData());
            keys.addAll(cluster.getKeys());
        }

        return new TreeCluster(coordinates, counter.summarize(), keys);
    }

    @Override
    protected TreeCluster split(TreeCluster removed, TreeCluster existing) {
        ArrayList<ChunkPos> coordinates = new ArrayList<>(64);
        TreeCounter counter = new TreeCounter();
        ArrayList<String> keys = new ArrayList<>(8);

        coordinates.addAll(existing.getCoordinates());
        counter.add(existing.getData());
        keys.addAll(existing.getKeys());

        coordinates.removeAll(removed.getCoordinates());
        counter.remove(removed.getData());
        keys.removeAll(removed.getKeys());

        return new TreeCluster(coordinates, counter.summarize(), keys);
    }

    @Override
    protected void undeploy(TreeCluster cluster) {
        IMarkerHandler markers = JMPlugin.instance();
        for(String key : cluster.getKeys()){
            markers.delete(key);
        }
        cluster.getKeys().clear();
    }

    @Override
    protected void deploy(TreeCluster cluster) {
        IMarkerHandler handler = JMPlugin.instance();
        Collection<ITreeDataHandler.TreeSummary> data = cluster.getData();
        if(data.isEmpty()) return;
        BlockPos center = calculateCenterOfMass(cluster);

        int SCALE = 4;
        int s = (data.size()-1)*10;
        int o = 0;
        for(ITreeDataHandler.TreeSummary summary : data){
            final int offset = o;

            BufferedImage image = AsyncImageProxy.getImage(TFCBlockTypes.getTexturePath(summary.tree), SCALE);

            String marker_key = "TreeCluster_"+summary.tree.replace("SAPLING:", "")+"_"+center.getX()+"_"+center.getZ();
            BlockPos marker_pos = center.offset(s-offset, 0, 0);
            Marker marker = new Marker(marker_key, dim, marker_pos.getX(), marker_pos.getZ(), image, SCALE, 1, MapType.BIOGEOGRAPHICAL);
            cluster.getKeys().add(marker_key);

            String label_key = "Label"+marker_key;
            BlockPos label_pos = marker_pos.offset(0, 0, 12);
            Marker label = new Marker(label_key, dim, label_pos.getX(), label_pos.getZ(), String.valueOf(summary.count), 0x00A000, SCALE, 2, 0, MapType.BIOGEOGRAPHICAL);
            cluster.getKeys().add(label_key);

            ThreadHandler.runOnGameThreadBlocking(() -> {
                handler.place(marker);
                handler.place(label);
            });

            o += 20;
        }
    }

    @Override
    protected Collection<ITreeDataHandler.TreeSummary> summarize(Collection<ChunkPos> cluster) {
        TreeCounter counter = new TreeCounter();
        for(ChunkPos chunk : cluster){
            counter.add(this.data.get(chunk));
        }
        return counter.summarize();
    }

    protected TreeCluster make(ChunkPos coordinate, Collection<ITreeDataHandler.TreeSummary> data){
        Collection<ChunkPos> coordinates = new ArrayList<>(1);
        coordinates.add(coordinate);
        return new TreeCluster(coordinates, data, new ArrayList<>(16));
    }

    private BlockPos calculateCenterOfMass(TreeCluster cluster){
        ChunkPos pos = cluster.getCoordinates().iterator().next();
        int x = pos.x / 16;
        int z = pos.z / 16;
        return new BlockPos((x*256)+128, 0, (z*256)+128);
    }
}
