package lordfokas.cartography.modules.geology;

import lordfokas.cartography.core.MapTypeRegistry;
import lordfokas.cartography.core.data.ThreadHandler;
import lordfokas.cartography.core.mapping.IChunkData;
import lordfokas.cartography.modules.Module;

import java.util.Set;

public class Geology {
    public static final MapTypeRegistry MAP_TYPE_REGISTRY = new MapTypeRegistry(Module.GEOLOGY);
    private static final IRockDataHandler.Async ASYNC_ROCK_DATA_PROXY = new AsyncRockDataProxy();
    private static IRockDataHandler rockDataHandler = new RockDataHandler.Dummy();

    public static IRockDataHandler.Async getAsyncRockDataHandler(){
        return ASYNC_ROCK_DATA_PROXY;
    }

    public static void init(){
        MAP_TYPE_REGISTRY.dumpToMaster();
        rockDataHandler = new RockDataHandler();
    }

    private static class AsyncRockDataProxy implements IRockDataHandler.Async{
        @Override
        public ThreadHandler<Void, Void> setRocksInChunkAsyncChain(IChunkData chunk, Set<String> rocks) {
            return ThreadHandler.startOnDataThread(v -> {
                rockDataHandler.setRocksInChunk(chunk, rocks);
                return null;
            });
        }

        @Override
        public ThreadHandler<Void, Boolean> setMarkersVisibleAsyncChain(boolean visible) {
            return ThreadHandler.startOnDataThread(v -> {
                rockDataHandler.setMarkersVisible(visible);
                return rockDataHandler.getMarkersVisible();
            });
        }

        @Override
        public void setRocksInChunk(IChunkData chunk, Set<String> rocks) {
            ThreadHandler.runOnDataThread(() -> rockDataHandler.setRocksInChunk(chunk, rocks));
        }

        @Override
        public void setMarkersVisible(boolean visible) {
            ThreadHandler.runOnDataThread(() -> rockDataHandler.setMarkersVisible(visible));
        }

        @Override
        public boolean getMarkersVisible() {
            return rockDataHandler.getMarkersVisible();
        }
    }
}
