package lordfokas.cartography.modules.biology;

import lordfokas.cartography.Cartography;
import lordfokas.cartography.core.GameContainer;
import lordfokas.cartography.core.data.SerializableDataPool;
import lordfokas.cartography.core.mapping.IChunkData;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.*;

public class TreeDataHandler implements ITreeDataHandler {
    private static final TreeDataSerializer CODEC = new TreeDataSerializer();
    private static final UUID UUID_ZERO = new UUID(0L,0L);

    private final HashMap<RegistryKey<World>, SerializableDataPool<ChunkPos, Collection<TreeSummary>>> summaries = new HashMap<>();
    private final HashMap<RegistryKey<World>, TreeClusterRealm> clusters = new HashMap<>();
    private final HashMap<RegistryKey<World>, TreeClusterViewer> viewers = new HashMap<>();
    private final GameContainer container;
    private volatile boolean visible = true;

    public TreeDataHandler(GameContainer container){
        this.container = container;
    }

    @Override
    public void setTreesInChunk(IChunkData chunk, Collection<TreeSummary> summaries) {
        SerializableDataPool<ChunkPos, Collection<TreeSummary>> pool = getDataPool(chunk);
        pool.addData(chunk.getPos(), summaries);
        pool.save();

        /*Collection<TreeSummary> current = getSummaryFor(chunk);
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
        }*/
    }

    @Override
    public void setMarkersVisible(boolean visible){
        for(TreeClusterViewer viewer : viewers.values()){
            viewer.setVisible(visible);
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

    private TreeClusterViewer getClusterViewer(IChunkData chunk){
        return viewers.computeIfAbsent(chunk.getDimension(), w -> new TreeClusterViewer(w));
    }

    private TreeClusterRealm getClusterRealm(IChunkData chunk){
        return this.clusters.computeIfAbsent(chunk.getDimension(), w -> new TreeClusterRealm(container.getAsyncDataCruncher().getThreadAsserter(), getClusterViewer(chunk), getDataPool(chunk)));
    }

    private SerializableDataPool<ChunkPos, Collection<TreeSummary>> getDataPool(IChunkData chunk){
        return summaries.computeIfAbsent(chunk.getDimension(), $ -> {
            SerializableDataPool<ChunkPos, Collection<TreeSummary>> pool = new SerializableDataPool<>(CODEC, container.getDataStoreManager().getDataStore(UUID_ZERO, Cartography.MOD_ID), "trees.bin");
            TreeClusterRealm clusters = this.clusters.computeIfAbsent(chunk.getDimension(), w -> new TreeClusterRealm(container.getAsyncDataCruncher().getThreadAsserter(), getClusterViewer(chunk), pool));
            pool.addConsumer(clusters);
            pool.load();
            return pool;
        });
    }

    private Collection<TreeSummary> getSummaryFor(IChunkData chunk){
       return getDataPool(chunk).computeIfAbsent(chunk.getPos(), () -> new ArrayList<>(8));
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

    public static class Dummy implements ITreeDataHandler {
        @Override public void setTreesInChunk(IChunkData chunk, Collection<TreeSummary> summaries){}
        @Override public void setMarkersVisible(boolean visible){}
        @Override public boolean getMarkersVisible(){ return false; }
    }
}
