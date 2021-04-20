package lordfokas.cartography.modules.geology;

import lordfokas.cartography.core.mapping.IChunkData;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class RockDataHandler implements IRockDataHandler{
    private final HashMap<RegistryKey<World>, HashMap<ChunkPos, String>> data = new HashMap<>();
    private final HashMap<RegistryKey<World>, HashMap<String, RockClusterRealm>> clusters = new HashMap<>();
    private volatile boolean visible = true;

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
        for(HashMap<String, RockClusterRealm> realms : clusters.values()){
            for(RockClusterRealm realm : realms.values()){
                realm.setDeployStatus(visible);
            }
        }
        this.visible = visible;
    }

    @Override
    public boolean getMarkersVisible() {
        return visible;
    }

    private RockClusterRealm getClusterRealm(IChunkData chunk, String rock){
        return clusters
                .computeIfAbsent(chunk.getDimension(), $ -> new HashMap<>())
                .computeIfAbsent(rock, $ -> {
                    RockClusterRealm realm = new RockClusterRealm(chunk.getDimension(), rock);
                    realm.setDeployStatus(visible);
                    return realm;
                });
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
