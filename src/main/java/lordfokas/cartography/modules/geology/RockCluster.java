package lordfokas.cartography.modules.geology;

import lordfokas.cartography.core.data.Cluster;
import net.minecraft.util.math.ChunkPos;

import java.util.Collection;
import java.util.Set;

public class RockCluster extends Cluster<ChunkPos, Set<String>, String> {
    public RockCluster(Collection<ChunkPos> coordinates, Set<String> data, Collection<String> keys) {
        super(coordinates, data, keys);
    }
}
