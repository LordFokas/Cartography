package lordfokas.cartography.feature.discovery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;

import com.eerussianguy.blazemap.engine.async.AsyncDataCruncher;
import lordfokas.cartography.data.ClusterRealm;
import lordfokas.cartography.data.IClusterConsumer;

public class DiscoveryClusterRealm extends ClusterRealm<BlockPos, String, DiscoveryCluster> {

    protected DiscoveryClusterRealm(AsyncDataCruncher.IThreadAsserter dataCruncherThread, IClusterConsumer<DiscoveryCluster> consumer) {
        super(dataCruncherThread, consumer);
    }

    public synchronized void nearbyXZ(BlockPos pos, int distance, Consumer<DiscoveryCluster> consumer) {
        clusters.forEach(cluster -> {
            if(cluster.centerOfMass().atY(0).distSqr(pos) <= distance) {
                consumer.accept(cluster);
            }
        });
    }

    @Override
    protected boolean isInRange(BlockPos coordinate, Collection<BlockPos> cluster) {
        for(BlockPos existing : cluster) {
            if(existing.distSqr(coordinate) <= 225) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean isIncluded(BlockPos coordinate, Collection<BlockPos> cluster) {
        return cluster.contains(coordinate);
    }

    @Override
    protected DiscoveryCluster merge(DiscoveryCluster added, Iterable<DiscoveryCluster> existing) {
        ArrayList<BlockPos> coordinates = new ArrayList<>(16);
        coordinates.addAll(added.getCoordinates());
        existing.forEach(e -> coordinates.addAll(e.getCoordinates()));
        return new DiscoveryCluster(coordinates, added.getData());
    }

    @Override
    protected DiscoveryCluster split(DiscoveryCluster removed, DiscoveryCluster existing) {
        existing.getCoordinates().removeAll(removed.getCoordinates());
        return existing;
    }

    @Override
    protected DiscoveryCluster make(BlockPos coordinate, String data) {
        return new DiscoveryCluster(Arrays.asList(coordinate), data);
    }
}
