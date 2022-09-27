package lordfokas.cartography.feature.discovery;

import java.util.Collection;

import net.minecraft.core.BlockPos;

import lordfokas.cartography.data.Cluster;

public class DiscoveryCluster extends Cluster<BlockPos, String> {
    public DiscoveryCluster(Collection<BlockPos> coordinates, String data) {
        super(coordinates, data);
    }

    public BlockPos centerOfMass() {
        int size = getCoordinates().size();
        if(size == 1) {
            return getCoordinates().iterator().next();
        }
        long x = 0, z = 0;
        for(BlockPos pos : getCoordinates()) {
            x += pos.getX();
            z += pos.getZ();
        }
        x /= size;
        z /= size;
        return new BlockPos(x, 0, z);
    }
}
