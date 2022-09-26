package lordfokas.cartography.feature.data;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.world.level.ChunkPos;

import com.eerussianguy.blazemap.engine.async.AsyncDataCruncher;
import lordfokas.cartography.data.ClusterRealm;
import lordfokas.cartography.data.IClusterConsumer;

public class RockClusterRealm extends ClusterRealm<ChunkPos, String, String, RockCluster> {
    private final String rock;

    public RockClusterRealm(AsyncDataCruncher.IThreadAsserter dataCruncherThread, IClusterConsumer<RockCluster> consumer, String rock) {
        super(dataCruncherThread, consumer);
        this.rock = rock.replace("STONE:", "");
    }

    @Override
    protected boolean isInRange(ChunkPos coordinate, Collection<ChunkPos> cluster) {
        for(ChunkPos target : cluster) {
            if(coordinate.getChessboardDistance(target) <= 1) {
                return true;
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
    protected RockCluster merge(RockCluster added, Iterable<RockCluster> existing) {
        ArrayList<ChunkPos> coordinates = new ArrayList<>(added.getCoordinates());
        for(RockCluster cluster : existing) {
            coordinates.addAll(cluster.getCoordinates());
        }
        return new RockCluster(coordinates, added.getData());
    }

    @Override
    protected RockCluster split(RockCluster removed, RockCluster existing) {
        ArrayList<ChunkPos> coordinates = new ArrayList<>(existing.getCoordinates());
        coordinates.removeAll(removed.getCoordinates());
        return new RockCluster(coordinates, existing.getData());
    }

    @Override
    protected String summarize(Collection<ChunkPos> cluster) {
        return rock;
    }

    @Override
    protected RockCluster make(ChunkPos coordinate, String data) {
        Collection<ChunkPos> coordinates = new ArrayList<>(1);
        coordinates.add(coordinate);
        return new RockCluster(coordinates, rock);
    }
}
