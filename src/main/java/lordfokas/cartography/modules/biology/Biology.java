package lordfokas.cartography.modules.biology;

import lordfokas.cartography.core.GameContainerClient;
import lordfokas.cartography.core.MapTypeRegistry;
import lordfokas.cartography.core.data.ThreadHandler;
import lordfokas.cartography.core.mapping.IChunkData;
import lordfokas.cartography.modules.Module;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collection;

public class Biology {
    public static final MapTypeRegistry MAP_TYPE_REGISTRY = new MapTypeRegistry(Module.BIOLOGY);
    private static final ITreeDataHandler.Async ASYNC_TREE_DATA_PROXY = new AsyncTreeDataProxy();
    private static final ITreeDataHandler DUMMY = new TreeDataHandler.Dummy();

    private static GameContainerClient clientContainer = null;
    private static ITreeDataHandler treeDataHandler = DUMMY;

    public static ITreeDataHandler.Async getAsyncTreeDataHandler(){
        return ASYNC_TREE_DATA_PROXY;
    }

    public static void init(){
        MAP_TYPE_REGISTRY.dumpToMaster();
        MinecraftForge.EVENT_BUS.register(Biology.class);
    }

    @SubscribeEvent
    public static void onClientContainerLoad(final GameContainerClient.LoadEvent evt){
        clientContainer = evt.getContainer();
        treeDataHandler = new TreeDataHandler(clientContainer);
    }

    @SubscribeEvent
    public static void onClientContainerUnload(final GameContainerClient.UnloadEvent evt){
        clientContainer = null;
        treeDataHandler = DUMMY;
    }

    private static class AsyncTreeDataProxy implements ITreeDataHandler.Async {
        @Override
        public ThreadHandler<Void, Void> setTreesInChunkAsyncChain(IChunkData chunk, Collection<TreeSummary> summaries) {
            return clientContainer.getThreadHandler().startOnDataThread(v -> {
                treeDataHandler.setTreesInChunk(chunk, summaries);
                return null;
            });
        }

        @Override
        public ThreadHandler<Void, Boolean> setMarkersVisibleAsyncChain(boolean visible) {
            return clientContainer.getThreadHandler().startOnDataThread(v -> {
                treeDataHandler.setMarkersVisible(visible);
                return treeDataHandler.getMarkersVisible();
            });
        }

        @Override
        public void setTreesInChunk(IChunkData chunk, Collection<TreeSummary> summaries) {
            clientContainer.getThreadHandler().runOnDataThread(() -> treeDataHandler.setTreesInChunk(chunk, summaries));
        }

        @Override
        public void setMarkersVisible(boolean visible) {
            clientContainer.getThreadHandler().runOnDataThread(() -> treeDataHandler.setMarkersVisible(visible));
        }

        @Override
        public boolean getMarkersVisible() {
            return treeDataHandler.getMarkersVisible();
        }
    };
}
