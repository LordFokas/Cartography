package lordfokas.cartography.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public abstract class ClusterRealm<C, D, R, K extends Cluster<C, D, R>>{
    private final Collection<K> clusters = new ArrayList<>(64);
    private boolean deploy = false;

    protected abstract boolean isInRange(C coordinate, Collection<C> cluster);
    protected abstract boolean isIncluded(C coordinate, Collection<C> cluster);
    protected abstract K merge(K added, Iterable<K> existing);
    protected abstract K split(K removed, K existing);
    protected abstract void undeploy(K cluster);
    protected abstract void deploy(K cluster);
    protected abstract D summarize(Collection<C> cluster);
    protected abstract K make(C coordinate, D data);

    public K getClusterAt(C coordinate){
        for(K cluster : clusters){
            if(isIncluded(coordinate, cluster.getCoordinates())){
                return cluster;
            }
        }
        return null;
    }

    public void setDeployStatus(boolean deploy){
        AsyncDataCruncher.assertIsOnDataCruncherThread();
        if(this.deploy == deploy) return;
        else this.deploy = deploy;

        if(this.deploy){
            for(K cluster : clusters){
                deploy(cluster);
            }
        }else{
            for(K cluster : clusters){
                undeploy(cluster);
            }
        }
    }

    public void addData(C coordinate, D data){
        AsyncDataCruncher.assertIsOnDataCruncherThread();
        K cluster = make(coordinate, data);
        Collection<K> neighbors = new LinkedList<>();
        for(K target : clusters) {
            if (isInRange(coordinate, target.getCoordinates())) {
                neighbors.add(target);
            }
        }
        if(!neighbors.isEmpty()){
            if(deploy){
                for(K target : neighbors){
                    undeploy(target);
                }
            }
            clusters.removeAll(neighbors);
            cluster = merge(cluster, neighbors);
        }
        clusters.add(cluster);
        if(deploy) deploy(cluster);
    }

    public void removeData(C coordinate, D data){
        AsyncDataCruncher.assertIsOnDataCruncherThread();
        K cluster = getClusterAt(coordinate);
        if(cluster == null) return;
        K removed = make(coordinate, data);
        if(deploy) undeploy(cluster);
        clusters.remove(cluster);
        cluster = split(removed, cluster);
        if(cluster == null) return;
        clusters.add(cluster);
        if(deploy) deploy(cluster);
    }

    public void refreshClusterAt(C coordinate){
        AsyncDataCruncher.assertIsOnDataCruncherThread();
        K cluster = getClusterAt(coordinate);
        if(deploy) undeploy(cluster);
        cluster.setData(summarize(cluster.getCoordinates()));
        if(deploy) deploy(cluster);
    }
}
