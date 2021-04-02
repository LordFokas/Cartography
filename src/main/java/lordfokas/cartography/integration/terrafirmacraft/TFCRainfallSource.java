package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.core.DataType;
import lordfokas.cartography.core.IChunkData;
import lordfokas.cartography.core.continuous.ContinuousDatum;
import lordfokas.cartography.core.continuous.IContinuousDataSource;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class TFCRainfallSource implements IContinuousDataSource {
    private static final float MAX_RAINFALL = 500F;

    @Override
    public DataType getDataType() {
        return DataType.RAINFALL;
    }

    @Override
    public ContinuousDatum getDatum(IChunkData chunk, int x, int y) {
        int h = rainfall(chunk, x, y);
        float value = ((float)h) / MAX_RAINFALL;

        float a = rainfall(chunk, x+1, y);
        float b = rainfall(chunk, x-1, y);
        float c = rainfall(chunk, x, y+1);
        float d = rainfall(chunk, x, y-1);
        boolean boundary = a<h || b<h || c<h || d<h;

        return new ContinuousDatum(value, boundary);
    }

    private int rainfall(IChunkData c, int x, int y){
        ChunkData data = TFCHelper.getChunkData(c.getChunk(x, y));
        return (int) Math.floor(data.getRainfall(x, y));
    }
}
