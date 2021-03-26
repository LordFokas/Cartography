package lordfokas.cartography.integration.minecraft;

import lordfokas.cartography.integration.journeymap.DataType;
import lordfokas.cartography.integration.journeymap.IChunkData;
import lordfokas.cartography.integration.journeymap.continuous.IDataSource;

public class TerrainHeightDataSource implements IDataSource {
    private static final int STEP = 4;

    @Override
    public DataType getDataType() {
        return DataType.TERRAIN_HEIGHT;
    }

    @Override
    public TerrainDatum getDatum(IChunkData chunk, int x, int y) {
        float worldHeight = chunk.getWorldHeight();
        int h = chunk.getTerrainHeight(x, y);
        float value = ((float)h) / worldHeight;
        boolean boundary = false, water = false;
        int depth = chunk.getWaterDepth(x, y);

        if(depth > 0){
            water = true;
        }else{
            h = h - (h % STEP);
            int a = chunk.getTerrainHeight(x+1, y);
            int b = chunk.getTerrainHeight(x-1, y);
            int c = chunk.getTerrainHeight(x, y+1);
            int d = chunk.getTerrainHeight(x, y-1);
            boundary = h > a || h > b || h > c || h > d;
        }

        return new TerrainDatum(value, boundary, water, depth);
    }
}
