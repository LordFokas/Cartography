package lordfokas.cartography.modules.biology;

import lordfokas.cartography.core.data.Cluster;
import net.minecraft.world.level.ChunkPos;

import java.util.Collection;

public class TreeCluster extends Cluster<ChunkPos, Collection<ITreeDataHandler.TreeSummary>, String> {

    public TreeCluster(Collection<ChunkPos> coordinates, Collection<ITreeDataHandler.TreeSummary> data, Collection<String> keys) {
        super(coordinates, data, keys);
    }
}
