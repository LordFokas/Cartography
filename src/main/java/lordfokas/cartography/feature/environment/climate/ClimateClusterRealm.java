package lordfokas.cartography.feature.environment.climate;

import java.util.Collection;

import net.minecraft.world.level.ChunkPos;

import com.eerussianguy.blazemap.engine.async.AsyncDataCruncher;
import lordfokas.cartography.data.ClusterRealm;
import lordfokas.cartography.data.IClusterConsumer;

public class ClimateClusterRealm extends ClusterRealm<ChunkPos, Isoline, ClimateCluster> {
    private static final int SHIFT = 5;

    protected ClimateClusterRealm(AsyncDataCruncher.IThreadAsserter dataCruncherThread, IClusterConsumer<ClimateCluster> consumer) {
        super(dataCruncherThread, consumer);
    }

    @Override
    protected boolean isInRange(ChunkPos coordinate, Collection<ChunkPos> cluster) {
        ChunkPos other = cluster.iterator().next();
        if(coordinate.x >> SHIFT == other.x >> SHIFT && coordinate.z >> SHIFT == other.z >> SHIFT) {
            for(ChunkPos target : cluster) {
                if(coordinate.getChessboardDistance(target) <= 3) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isIncluded(ChunkPos coordinate, Collection<ChunkPos> cluster) {
        for(ChunkPos target : cluster) {
            if(target.equals(coordinate)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected ClimateCluster merge(ClimateCluster added, Iterable<ClimateCluster> existing) {
        ClimateCluster copy = new ClimateCluster(added.getData().copy());
        existing.forEach(c -> copy.getData().curves.putAll(c.getData().curves));
        return copy;
    }

    @Override
    protected ClimateCluster split(ClimateCluster removed, ClimateCluster existing) {
        ClimateCluster copy = new ClimateCluster(existing.getData().copy());
        removed.getCoordinates().forEach(c -> copy.getData().curves.remove(c));
        return copy;
    }

    @Override
    protected ClimateCluster make(ChunkPos coordinate, Isoline data) {
        return new ClimateCluster(data);
    }
}
