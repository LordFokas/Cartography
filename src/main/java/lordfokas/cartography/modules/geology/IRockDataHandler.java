package lordfokas.cartography.modules.geology;

import lordfokas.cartography.core.data.ThreadHandler;
import lordfokas.cartography.core.mapping.IChunkData;

import java.util.Set;

public interface IRockDataHandler {
    void setRocksInChunk(IChunkData chunk, Set<String> rocks);
    void setMarkersVisible(boolean visible);
    boolean getMarkersVisible();

    interface Async extends IRockDataHandler{
        ThreadHandler<Void, Void> setRocksInChunkAsyncChain(IChunkData chunk, Set<String> rocks);
        ThreadHandler<Void, Boolean> setMarkersVisibleAsyncChain(boolean visible);
    }
}
