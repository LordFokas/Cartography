package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.integration.journeymap.DataType;
import lordfokas.cartography.integration.journeymap.IChunkData;
import lordfokas.cartography.integration.journeymap.continuous.Datum;
import lordfokas.cartography.integration.journeymap.continuous.IDataSource;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class TFCTemperatureSource implements IDataSource {
    private static final float MIN_TEMPERATURE = -20F;
    private static final float MAX_TEMPERATURE = 40F;

    @Override
    public DataType getDataType() {
        return DataType.TEMPERATURE;
    }

    @Override
    public Datum getDatum(IChunkData chunk, int x, int y) {
        int h = temperature(chunk, x, y);
        float v = Math.max(Math.min(MAX_TEMPERATURE, h), MIN_TEMPERATURE);
        float value = (v - MIN_TEMPERATURE) / (MAX_TEMPERATURE - MIN_TEMPERATURE);

        float a = temperature(chunk, x+1, y);
        float b = temperature(chunk, x-1, y);
        float c = temperature(chunk, x, y+1);
        float d = temperature(chunk, x, y-1);
        boolean boundary = a<h || b<h || c<h || d<h;

        return new Datum(value, boundary);
    }

    private int temperature(IChunkData c, int x, int y){
        ChunkData data = TFCHelper.getChunkData(c.getChunk(x, y));
        return (int) Math.floor(data.getAverageTemp(x, y));
    }
}