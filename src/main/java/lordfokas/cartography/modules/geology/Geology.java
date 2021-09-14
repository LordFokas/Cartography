package lordfokas.cartography.modules.geology;

import lordfokas.cartography.core.GameContainerClient;
import lordfokas.cartography.core.MapTypeRegistry;
import lordfokas.cartography.core.data.ThreadHandler;
import lordfokas.cartography.core.mapping.IChunkData;
import lordfokas.cartography.modules.Module;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Geology {
    public static final MapTypeRegistry MAP_TYPE_REGISTRY = new MapTypeRegistry(Module.GEOLOGY);
    private static final IRockDataHandler.Async ASYNC_ROCK_DATA_PROXY = new AsyncRockDataProxy();
    private static final IRockDataHandler DUMMY = new RockDataHandler.Dummy();

    private static GameContainerClient clientContainer = null;
    private static IRockDataHandler rockDataHandler = DUMMY;

    public static IRockDataHandler.Async getAsyncRockDataHandler(){
        return ASYNC_ROCK_DATA_PROXY;
    }

    public static void init(){
        MAP_TYPE_REGISTRY.dumpToMaster();
        MinecraftForge.EVENT_BUS.register(Geology.class);
    }

    @SubscribeEvent
    public static void onClientContainerLoad(final GameContainerClient.LoadEvent evt) {
        clientContainer = evt.getContainer();
        rockDataHandler = new RockDataHandler(clientContainer);
    }

    @SubscribeEvent
    public static void onClientContainerUnload(final GameContainerClient.UnloadEvent evt) {
        clientContainer = null;
        rockDataHandler = DUMMY;
    }

    private static class AsyncRockDataProxy implements IRockDataHandler.Async{
        @Override
        public ThreadHandler<Void, Void> setRocksInChunkAsyncChain(IChunkData chunk, String rocks) {
            return clientContainer.getThreadHandler().startOnDataThread(v -> {
                rockDataHandler.setRocksInChunk(chunk, rocks);
                return null;
            });
        }

        @Override
        public ThreadHandler<Void, Boolean> setMarkersVisibleAsyncChain(boolean visible) {
            return clientContainer.getThreadHandler().startOnDataThread(v -> {
                rockDataHandler.setMarkersVisible(visible);
                return rockDataHandler.getMarkersVisible();
            });
        }

        @Override
        public void setRocksInChunk(IChunkData chunk, String rocks) {
            clientContainer.getThreadHandler().runOnDataThread(() -> rockDataHandler.setRocksInChunk(chunk, rocks));
        }

        @Override
        public void setMarkersVisible(boolean visible) {
            clientContainer.getThreadHandler().runOnDataThread(() -> rockDataHandler.setMarkersVisible(visible));
        }

        @Override
        public boolean getMarkersVisible() {
            return rockDataHandler.getMarkersVisible();
        }
    }
}
