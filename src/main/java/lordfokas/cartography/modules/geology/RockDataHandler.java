package lordfokas.cartography.modules.geology;

import lordfokas.cartography.core.mapping.IChunkData;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Set;

public class RockDataHandler implements IRockDataHandler{
    private final HashMap<RegistryKey<World>, HashMap<ChunkPos, Set<String>>> data = new HashMap<>();
    private final HashMap<RegistryKey<World>, RockClusterRealm> clusters = new HashMap<>();

    @Override
    public void setRocksInChunk(IChunkData chunk, Set<String> rocks) {

    }

    @Override
    public void setMarkersVisible(boolean visible) {

    }

    @Override
    public boolean getMarkersVisible() {
        return false;
    }

    public static class Dummy implements IRockDataHandler{
        @Override public void setRocksInChunk(IChunkData chunk, Set<String> rocks){}
        @Override public void setMarkersVisible(boolean visible){}
        @Override public boolean getMarkersVisible(){ return false; }
    }
}
