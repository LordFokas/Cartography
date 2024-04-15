package lordfokas.cartography.feature.discovery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;

import com.eerussianguy.blazemap.engine.async.AsyncDataCruncher;
import lordfokas.cartography.data.ClusterRealm;
import lordfokas.cartography.data.IClusterConsumer;

public class DiscoveryClusterRealm extends ClusterRealm<BlockPos, DiscoveryState, DiscoveryCluster> {
    public final String type;

    protected DiscoveryClusterRealm(AsyncDataCruncher.IThreadAsserter dataCruncherThread, IClusterConsumer<DiscoveryCluster> consumer, String type) {
        super(dataCruncherThread, consumer);
        this.type = type;
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
        ArrayList<DiscoveryState> data = new ArrayList<>(16);

        coordinates.addAll(added.getCoordinates());
        data.addAll(added.getMembers());
        existing.forEach(cluster -> {
            coordinates.addAll(cluster.getCoordinates());
            data.addAll(cluster.getMembers());
        });

        return new DiscoveryCluster(coordinates, data, type, this::onClusterChanged);
    }

    @Override
    protected DiscoveryCluster split(DiscoveryCluster removed, DiscoveryCluster existing) {
        existing.getCoordinates().removeAll(removed.getCoordinates());
        return existing;
    }

    @Override
    protected DiscoveryCluster make(BlockPos coordinate, DiscoveryState data) {
        return new DiscoveryCluster(List.of(coordinate), List.of(data), type, this::onClusterChanged);
    }
}
