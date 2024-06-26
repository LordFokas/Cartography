package lordfokas.cartography.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import com.eerussianguy.blazemap.engine.async.AsyncDataCruncher;

public abstract class ClusterRealm<C, D, K extends Cluster<C, D>> implements DataFlow.IDataConsumer<C, D> {
    protected final Collection<K> clusters = new ArrayList<>(64);
    private final AsyncDataCruncher.IThreadAsserter dataCruncherThread;
    private final IClusterConsumer<K> consumer;

    protected ClusterRealm(AsyncDataCruncher.IThreadAsserter dataCruncherThread, IClusterConsumer<K> consumer) {
        this.dataCruncherThread = dataCruncherThread;
        this.consumer = consumer;
    }

    protected abstract boolean isInRange(C coordinate, Collection<C> cluster);

    protected abstract boolean isIncluded(C coordinate, Collection<C> cluster);

    protected abstract K merge(K added, Iterable<K> existing);

    protected abstract K split(K removed, K existing);

    protected abstract K make(C coordinate, D data);

    public synchronized K getClusterAt(C coordinate) {
        for(K cluster : clusters) {
            if(isIncluded(coordinate, cluster.getCoordinates())) {
                return cluster;
            }
        }
        return null;
    }

    protected void onClusterChanged(K cluster) {
        consumer.dropCluster(cluster);
        consumer.pushCluster(cluster);
    }

    public synchronized void add(C coordinate, D data, boolean notify) {
        dataCruncherThread.assertCurrentThread();
        K cluster = make(coordinate, data);
        Collection<K> neighbors = new LinkedList<>();
        for(K target : clusters) {
            if(isInRange(coordinate, target.getCoordinates())) {
                neighbors.add(target);
            }
        }
        if(!neighbors.isEmpty()) {
            if(notify) consumer.dropClusters(neighbors);
            clusters.removeAll(neighbors);
            cluster = merge(cluster, neighbors);
        }
        clusters.add(cluster);
        if(notify) consumer.pushCluster(cluster);
    }

    @Override
    public synchronized void addData(C coordinate, D data) {
        dataCruncherThread.assertCurrentThread();
        add(coordinate, data, true);
    }

    @Override
    public synchronized void removeData(C coordinate, D data) {
        dataCruncherThread.assertCurrentThread();
        K cluster = getClusterAt(coordinate);
        if(cluster == null) return;
        K removed = make(coordinate, data);
        consumer.dropCluster(cluster);
        clusters.remove(cluster);
        cluster = split(removed, cluster);
        if(cluster == null) return;
        clusters.add(cluster);
        consumer.pushCluster(cluster);
    }

    @Override
    public synchronized void setData(Map<C, D> pool) {
        dataCruncherThread.assertCurrentThread();
        consumer.dropClusters(clusters);
        clusters.clear();
        for(Map.Entry<C, D> datum : pool.entrySet()) {
            add(datum.getKey(), datum.getValue(), false);
        }
        consumer.pushClusters(clusters);
    }
}
