package lordfokas.cartography.modules.biology;

import lordfokas.cartography.core.data.AsyncDataCruncher;
import lordfokas.cartography.core.data.ClusterRealm;
import lordfokas.cartography.core.data.DataPool;
import lordfokas.cartography.core.data.IClusterConsumer;
import net.minecraft.util.math.ChunkPos;

import java.util.*;

public class TreeClusterRealm extends ClusterRealm<ChunkPos, Collection<ITreeDataHandler.TreeSummary>, String, TreeCluster> {
    private final DataPool<ChunkPos, Collection<ITreeDataHandler.TreeSummary>> data;

    public TreeClusterRealm(AsyncDataCruncher.IThreadAsserter dataCruncherThread, IClusterConsumer<TreeCluster> consumer, DataPool<ChunkPos, Collection<ITreeDataHandler.TreeSummary>> data){
        super(dataCruncherThread, consumer);
        this.data = data;
    }

    @Override
    protected boolean isInRange(ChunkPos coordinate, Collection<ChunkPos> cluster) {
        int x = coordinate.x >> 4;
        int z = coordinate.z >> 4;
        ChunkPos pos = cluster.iterator().next();
        return pos.x >> 4 == x && pos.z >> 4 == z;
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
    protected Collection<ITreeDataHandler.TreeSummary> summarize(Collection<ChunkPos> cluster) {
        TreeCounter counter = new TreeCounter();
        for(ChunkPos chunk : cluster){
            counter.add(this.data.get(chunk));
        }
        return counter.summarize();
    }

    @Override
    protected TreeCluster make(ChunkPos coordinate, Collection<ITreeDataHandler.TreeSummary> data){
        Collection<ChunkPos> coordinates = new ArrayList<>(1);
        coordinates.add(coordinate);
        return new TreeCluster(coordinates, data, new ArrayList<>(16));
    }
}
