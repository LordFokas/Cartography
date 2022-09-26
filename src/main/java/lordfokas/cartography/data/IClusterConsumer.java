package lordfokas.cartography.data;

import java.util.Collection;

public interface IClusterConsumer<K extends Cluster<?, ?>> {
    void pushCluster(K cluster);

    default void pushClusters(Collection<K> clusters) {
        clusters.forEach(this::pushCluster);
    }

    void dropCluster(K cluster);

    default void dropClusters(Collection<K> clusters) {
        clusters.forEach(this::dropCluster);
    }
}
