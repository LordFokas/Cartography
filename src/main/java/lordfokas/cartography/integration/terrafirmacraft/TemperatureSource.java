package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.core.DataType;
import lordfokas.cartography.core.IChunkData;
import lordfokas.cartography.core.continuous.ContinuousDatum;
import lordfokas.cartography.core.continuous.IContinuousDataSource;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;

public class TemperatureSource implements IContinuousDataSource {
    private static final float MIN_TEMPERATURE = -20F;
    private static final float MAX_TEMPERATURE = 40F;

    @Override
    public DataType getDataType() {
        return DataType.TEMPERATURE;
    }

    @Override
    public ContinuousDatum getDatum(IChunkData chunk, int x, int y) {
        float t = temperature(chunk, x, y);
        float v = Math.max(Math.min(MAX_TEMPERATURE, t), MIN_TEMPERATURE);
        float value = (v - MIN_TEMPERATURE) / (MAX_TEMPERATURE - MIN_TEMPERATURE);

        float h = (float) Math.floor(t);
        float a = temperature(chunk, x+1, y);
        float b = temperature(chunk, x-1, y);
        float c = temperature(chunk, x, y+1);
        float d = temperature(chunk, x, y-1);
        boolean boundary = (a<h || b<h || c<h || d<h) && Math.abs(t-h)<0.025;

        return new ContinuousDatum(value, boundary, (int)h);
    }

    private float temperature(IChunkData c, int x, int y){
        ChunkData data = c.getChunk(x,y).getCapability(ChunkDataCapability.CAPABILITY).orElse(null);
        return data.getAverageTemp(x, y);
    }
}
