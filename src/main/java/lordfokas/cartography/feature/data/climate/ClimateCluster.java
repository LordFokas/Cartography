package lordfokas.cartography.feature.data.climate;

import java.util.Collections;

import net.minecraft.world.level.ChunkPos;

import lordfokas.cartography.data.Cluster;

public class ClimateCluster extends Cluster<ChunkPos, Isoline> {
    public ClimateCluster(Isoline data) {
        super(Collections.unmodifiableSet(data.curves.keySet()), data);
    }

    public Isoline.Curve getCenterPoint(){
        if(getCoordinates().size() < 8) return null;

        Isoline.Curve curve = null;
        double min = Double.MAX_VALUE;

        int size = data.curves.size();
        long x = 0, z = 0;
        for(ChunkPos pos : getCoordinates()) {
            x += pos.getMinBlockX();
            z += pos.getMinBlockZ();
        }
        x /= size;
        z /= size;

        for(Isoline.Curve c : data.curves.values()){
            long dx = x - c.chunk.x;
            long dz = z - c.chunk.z;
            double d = Math.sqrt(dx * dx + dz * dz);
            if(d < min){
                min = d;
                curve = c;
            }
        }

        return curve;
    }
}
