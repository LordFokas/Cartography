package lordfokas.cartography.modules.geology;

import lordfokas.cartography.core.data.ThreadHandler;
import lordfokas.cartography.core.mapping.IChunkData;

public interface IRockDataHandler {
    void setRocksInChunk(IChunkData chunk, String rocks);
    void setMarkersVisible(boolean visible);
    boolean getMarkersVisible();

    interface Async extends IRockDataHandler{
        ThreadHandler<Void, Void> setRocksInChunkAsyncChain(IChunkData chunk, String rocks);
        ThreadHandler<Void, Boolean> setMarkersVisibleAsyncChain(boolean visible);
    }
}
