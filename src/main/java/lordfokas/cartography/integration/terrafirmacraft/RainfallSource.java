package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.core.DataType;
import lordfokas.cartography.core.mapping.IChunkData;
import lordfokas.cartography.core.mapping.continuous.ContinuousDatum;
import lordfokas.cartography.core.mapping.continuous.IContinuousDataSource;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;

public class RainfallSource implements IContinuousDataSource {
    private static final float MAX_RAINFALL = 500F;

    @Override
    public DataType getDataType() {
        return DataType.RAINFALL;
    }

    @Override
    public ContinuousDatum getDatum(IChunkData chunk, int x, int y) {
        float r = rainfall(chunk, x, y);
        float value = r / MAX_RAINFALL;

        float h = (float) Math.floor(r);
        float a = rainfall(chunk, x+1, y);
        float b = rainfall(chunk, x-1, y);
        float c = rainfall(chunk, x, y+1);
        float d = rainfall(chunk, x, y-1);
        boolean boundary = (a<h || b<h || c<h || d<h) && Math.abs(r-h)<0.025;

        return new ContinuousDatum(value, boundary, (int)h);
    }

    private float rainfall(IChunkData c, int x, int y){
        ChunkData data = c.getChunk(x,y).getCapability(ChunkDataCapability.CAPABILITY).orElse(null);
        return data.getRainfall(x, y);
    }
}
