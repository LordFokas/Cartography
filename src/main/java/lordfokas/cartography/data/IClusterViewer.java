package lordfokas.cartography.data;

import java.util.Collection;

public interface IClusterViewer<K extends Cluster<?, ?, ?>> extends IClusterConsumer<K> {
    void setVisible(boolean visible);

    default void pushClusters(Collection<K> clusters){
        for(K cluster : clusters) pushCluster(cluster);
    }

    default void dropClusters(Collection<K> clusters){
        for(K cluster : clusters) dropCluster(cluster);
    }
}
