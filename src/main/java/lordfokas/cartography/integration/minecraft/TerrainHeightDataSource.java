package lordfokas.cartography.integration.minecraft;

import lordfokas.cartography.integration.journeymap.DataType;
import lordfokas.cartography.integration.journeymap.IChunkData;
import lordfokas.cartography.integration.journeymap.continuous.Datum;
import lordfokas.cartography.integration.journeymap.continuous.IDataSource;

public class TerrainHeightDataSource implements IDataSource {
    private static final int STEP = 4;

    @Override
    public DataType getDataType() {
        return DataType.TERRAIN_HEIGHT;
    }

    @Override
    public Datum getDatum(IChunkData chunk, int x, int y) {
        float worldHeight = chunk.getWorldHeight();
        int h = chunk.getPrecipitationHeight(x, y);
        float value = ((float)h) / worldHeight;

        h = h - (h % STEP);
        int a = chunk.getPrecipitationHeight(x+1, y);
        int b = chunk.getPrecipitationHeight(x-1, y);
        int c = chunk.getPrecipitationHeight(x, y+1);
        int d = chunk.getPrecipitationHeight(x, y-1);
        boolean boundary = h > a || h > b || h > c || h > d;

        return new Datum(value, boundary);
    }
}
