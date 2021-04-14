package lordfokas.cartography.modules.biology;

import lordfokas.cartography.core.mapping.IChunkData;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

class TreeDataHandler implements ITreeDataHandler {
    private final HashMap<RegistryKey<World>, HashMap<ChunkPos, Collection<TreeSummary>>> summaries = new HashMap<>();
    private final HashMap<RegistryKey<World>, TreeClusterRealm> clusters = new HashMap<>();
    private volatile boolean visible = true;

    @Override
    public void setTreesInChunk(IChunkData chunk, Collection<TreeSummary> summaries) {
        Collection<TreeSummary> current = getSummaryFor(chunk);
        if(!areSummariesEqual(current, summaries)){
            if(current.isEmpty()){
                current.addAll(summaries);
                addToCluster(chunk, current);
            }else{
                if(summaries.isEmpty()){
                    removeFromCluster(chunk, current);
                    current.clear();
                    current.addAll(summaries);
                }else{
                    current.clear();
                    current.addAll(summaries);
                    updateCluster(chunk);
                }
            }
        }
    }

    @Override
    public void setMarkersVisible(boolean visible){
        for(TreeClusterRealm realm : clusters.values()){
            realm.setDeployStatus(visible);
        }
        this.visible = visible;
    }

    @Override
    public boolean getMarkersVisible(){
        return visible;
    }

    private void addToCluster(IChunkData chunk, Collection<TreeSummary> data){
        TreeClusterRealm realm = getClusterRealm(chunk);
        realm.addData(chunk.getPos(), data);
    }

    private void removeFromCluster(IChunkData chunk, Collection<TreeSummary> data){
        TreeClusterRealm realm = getClusterRealm(chunk);
        realm.removeData(chunk.getPos(), data);
    }

    private void updateCluster(IChunkData chunk){
        getClusterRealm(chunk).refreshClusterAt(chunk.getPos());
    }

    private TreeClusterRealm getClusterRealm(IChunkData chunk){
        return this.clusters.computeIfAbsent(chunk.getDimension(), w -> {
            TreeClusterRealm realm = new TreeClusterRealm(w, getWorldData(chunk));
            realm.setDeployStatus(this.visible);
            return realm;
        });
    }

    private HashMap<ChunkPos, Collection<TreeSummary>> getWorldData(IChunkData chunk){
        return summaries.computeIfAbsent(chunk.getDimension(), w -> new HashMap<>());
    }

    private Collection<TreeSummary> getSummaryFor(IChunkData chunk){
       return getWorldData(chunk).computeIfAbsent(chunk.getPos(), p -> new ArrayList<>(8));
    }

    private boolean areSummariesEqual(Collection<TreeSummary> a, Collection<TreeSummary> b){
        if(a.size() != b.size()) return false;
        HashMap<String, TreeSummary> index = new HashMap<>();
        for(TreeSummary sa : a){
            index.put(sa.tree, sa);
        }
        for(TreeSummary sb : b){
            TreeSummary sa = index.get(sb.tree);
            if(sa == null){
                return false;
            }
            if(sa.count != sb.count){
                return false;
            }
        }
        return true;
    }

    static class Dummy implements ITreeDataHandler {
        @Override public void setTreesInChunk(IChunkData chunk, Collection<TreeSummary> summaries){}
        @Override public void setMarkersVisible(boolean visible){}
        @Override public boolean getMarkersVisible(){ return false; }
    }
}
