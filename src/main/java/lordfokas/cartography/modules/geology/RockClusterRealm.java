package lordfokas.cartography.modules.geology;

import lordfokas.cartography.core.data.ClusterRealm;
import lordfokas.cartography.core.data.ThreadHandler;
import lordfokas.cartography.core.markers.IMarkerHandler;
import lordfokas.cartography.integration.journeymap.JMPlugin;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class RockClusterRealm extends ClusterRealm<ChunkPos, Set<String>, String, RockCluster> {
    private final HashMap<ChunkPos, Set<String>> data;
    private final String rock;

    public RockClusterRealm(HashMap<ChunkPos, Set<String>> data, String rock){
        this.data = data;
        this.rock = rock;
    }

    @Override
    protected boolean isInRange(ChunkPos coordinate, Collection<ChunkPos> cluster) {
        for(ChunkPos target : cluster){
            if(coordinate.getChessboardDistance(target) <= 1){
                return true;
            }
        }
        return false;
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
    protected RockCluster merge(RockCluster added, Iterable<RockCluster> existing) {
        return null;
    }

    @Override
    protected RockCluster split(RockCluster removed, RockCluster existing) {
        return existing; // TODO: evaluate rock data splitting
    }

    @Override
    protected void undeploy(RockCluster cluster) {
        IMarkerHandler markers = JMPlugin.instance();
        ThreadHandler.runOnGameThreadBlocking(() -> {
            for(String key : cluster.getKeys()){
                markers.delete(key);
            }
        });
        cluster.getKeys().clear();
    }

    @Override
    protected void deploy(RockCluster cluster) {

    }

    @Override
    protected Set<String> summarize(Collection<ChunkPos> cluster) {
        return null;
    }

    @Override
    protected RockCluster make(ChunkPos coordinate, Set<String> data) {
        Collection<ChunkPos> coordinates = new ArrayList<>(1);
        coordinates.add(coordinate);
        return new RockCluster(coordinates, data, new ArrayList<>(16));
    }
}
