package lordfokas.cartography.feature.mapping.climate;

import net.minecraft.world.level.Level;

import com.eerussianguy.blazemap.api.pipeline.Collector;
import lordfokas.cartography.CartographyReferences;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;

public class ClimateCollector extends Collector<ClimateMD> {
    public ClimateCollector() {
        super(CartographyReferences.Collectors.CLIMATE, CartographyReferences.MasterData.CLIMATE);
    }

    @Override
    public ClimateMD collect(Level level, int minX, int minZ, int maxX, int maxZ) {
        float[][] rainfall = new float[16][16];
        float[][] temperature = new float[16][16];

        ChunkData data = level.getChunkAt(POS.set(minX, 0, minZ)).getCapability(ChunkDataCapability.CAPABILITY).orElse(null);
        if(data == null) return null;

        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                rainfall[x][z] = data.getRainfall(x, z);
                temperature[x][z] = data.getAverageTemp(x, z);
            }
        }

        return new ClimateMD(rainfall, temperature);
    }
}
