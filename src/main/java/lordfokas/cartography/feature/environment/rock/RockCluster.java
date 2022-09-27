package lordfokas.cartography.feature.environment.rock;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import lordfokas.cartography.data.Cluster;

public class RockCluster extends Cluster<ChunkPos, String> {
    public RockCluster(Collection<ChunkPos> coordinates, String data) {
        super(coordinates, data);
    }

    public BlockPos centerOfMass() {
        int size = getCoordinates().size();
        if(size < 20) {
            return null;
        }
        long x = 0, z = 0;
        for(ChunkPos pos : getCoordinates()) {
            x += pos.getMinBlockX();
            z += pos.getMinBlockZ();
        }
        x /= size;
        z /= size;
        return new BlockPos(x, 0, z);
    }
}