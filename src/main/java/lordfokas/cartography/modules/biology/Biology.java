package lordfokas.cartography.modules.biology;

import lordfokas.cartography.core.MapTypeRegistry;
import lordfokas.cartography.core.data.ThreadHandler;
import lordfokas.cartography.core.mapping.IChunkData;
import lordfokas.cartography.modules.Module;

import java.util.Collection;

public class Biology {
    public static final MapTypeRegistry MAP_TYPE_REGISTRY = new MapTypeRegistry(Module.BIOLOGY);
    private static ITreeDataHandler treeDataHandler = new TreeDataHandler.Dummy();

    public static ITreeDataHandler.Async getAsyncTreeDataHandler(){
        return ASYNC_TREE_DATA_PROXY;
    }

    public static void init(){
        MAP_TYPE_REGISTRY.dumpToMaster();
        treeDataHandler = new TreeDataHandler();
    }

    private static final ITreeDataHandler.Async ASYNC_TREE_DATA_PROXY = new ITreeDataHandler.Async() {
        @Override
        public ThreadHandler<Void, Void> setTreesInChunkAsyncChain(IChunkData chunk, Collection<TreeSummary> summaries) {
            return ThreadHandler.startOnDataThread(v -> {
                treeDataHandler.setTreesInChunk(chunk, summaries);
                return null;
            });
        }

        @Override
        public ThreadHandler<Void, Boolean> setMarkersVisibleAsyncChain(boolean visible) {
            return ThreadHandler.startOnDataThread(v -> {
                treeDataHandler.setMarkersVisible(visible);
                return treeDataHandler.getMarkersVisible();
            });
        }

        @Override
        public void setTreesInChunk(IChunkData chunk, Collection<TreeSummary> summaries) {
            ThreadHandler.runOnDataThread(() -> treeDataHandler.setTreesInChunk(chunk, summaries));
        }

        @Override
        public void setMarkersVisible(boolean visible) {
            ThreadHandler.runOnDataThread(() -> treeDataHandler.setMarkersVisible(visible));
        }

        @Override
        public boolean getMarkersVisible() {
            return treeDataHandler.getMarkersVisible();
        }
    };
}
