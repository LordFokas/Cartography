package lordfokas.cartography.modules.geology;

import lordfokas.cartography.core.GameContainer;
import lordfokas.cartography.core.mapping.IChunkData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public class RockDataHandler implements IRockDataHandler{
    private final HashMap<ResourceKey<Level>, HashMap<ChunkPos, String>> data = new HashMap<>();
    private final HashMap<ResourceKey<Level>, HashMap<String, RockClusterRealm>> clusters = new HashMap<>();
    private final HashMap<ResourceKey<Level>, RockClusterViewer> viewers = new HashMap<>();
    private final GameContainer container;
    private volatile boolean visible = true;

    public RockDataHandler(GameContainer container){
        this.container = container;
    }

    @Override
    public void setRocksInChunk(IChunkData chunk, String rock) {
        if(rock == null) return;
        HashMap<ChunkPos, String> rocks = getWorldData(chunk);
        if(rocks.containsKey(chunk.getPos())) return;
        rocks.put(chunk.getPos(), rock);
        getClusterRealm(chunk, rock).addData(chunk.getPos(), rock);
    }

    @Override
    public void setMarkersVisible(boolean visible) {
        for(RockClusterViewer viewer : viewers.values()){
            viewer.setVisible(visible);
        }
        this.visible = visible;
    }

    @Override
    public boolean getMarkersVisible() {
        return visible;
    }

    private RockClusterViewer getClusterViewer(IChunkData chunk){
        return viewers.computeIfAbsent(chunk.getDimension(), RockClusterViewer::new);
    }

    private RockClusterRealm getClusterRealm(IChunkData chunk, String rock){
        return clusters
            .computeIfAbsent(chunk.getDimension(), $ -> new HashMap<>())
            .computeIfAbsent(rock, $ -> new RockClusterRealm(container.getAsyncDataCruncher().getThreadAsserter(), getClusterViewer(chunk), rock));
    }

    private HashMap<ChunkPos, String> getWorldData(IChunkData chunk){
        return data.computeIfAbsent(chunk.getDimension(), $ -> new HashMap<>());
    }

    public static class Dummy implements IRockDataHandler{
        @Override public void setRocksInChunk(IChunkData chunk, String rocks){}
        @Override public void setMarkersVisible(boolean visible){}
        @Override public boolean getMarkersVisible(){ return false; }
    }
}
