package lordfokas.cartography.data;

import java.util.Collection;

public interface IClusterConsumer<K extends Cluster<?, ?, ?>> {
    void pushCluster(K cluster);
    void pushClusters(Collection<K> clusters);

    void dropCluster(K cluster);
    void dropClusters(Collection<K> clusters);
}
