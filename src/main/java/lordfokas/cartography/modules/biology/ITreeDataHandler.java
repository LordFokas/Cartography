package lordfokas.cartography.modules.biology;

import lordfokas.cartography.core.data.ThreadHandler;
import lordfokas.cartography.core.mapping.IChunkData;

import java.util.Collection;

public interface ITreeDataHandler {
    void setTreesInChunk(IChunkData chunk, Collection<TreeSummary> summaries);
    void setMarkersVisible(boolean visible);
    boolean getMarkersVisible();

    interface Async extends ITreeDataHandler{
        ThreadHandler<Void, Void> setTreesInChunkAsyncChain(IChunkData chunk, Collection<TreeSummary> summaries);
        ThreadHandler<Void, Boolean> setMarkersVisibleAsyncChain(boolean visible);
    }

    final class TreeSummary {
        public final String tree;
        public final int count;

        public TreeSummary(String tree, int count){
            this.tree = tree;
            this.count = count;
        }
    }
}