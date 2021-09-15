package lordfokas.cartography.modules.geology;

import lordfokas.cartography.core.data.Cluster;
import net.minecraft.world.level.ChunkPos;

import java.util.Collection;

public class RockCluster extends Cluster<ChunkPos, String, String> {
    public RockCluster(Collection<ChunkPos> coordinates, String data, Collection<String> keys) {
        super(coordinates, data, keys);
    }
}
