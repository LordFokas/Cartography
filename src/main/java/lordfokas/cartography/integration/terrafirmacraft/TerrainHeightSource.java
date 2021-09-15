package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.core.DataType;
import lordfokas.cartography.core.mapping.IChunkData;
import lordfokas.cartography.core.mapping.continuous.IContinuousDataSource;
import lordfokas.cartography.utils.Pointer;
import net.minecraft.util.Tuple;

public class TerrainHeightSource implements IContinuousDataSource {
    private final int step;

    public TerrainHeightSource(int step){
        this.step = step;
    }

    @Override
    public DataType getDataType() {
        return DataType.TERRAIN_HEIGHT;
    }

    @Override
    public TerrainDatum getDatum(IChunkData chunk, int x, int y) {
        float worldHeight = chunk.getWorldHeight();
        int h = scan(chunk, x, y);
        float value = ((float)h) / worldHeight;
        boolean boundary = false, water = false;
        int depth = chunk.getWaterDepth(x, y);

        if(depth > 0){
            water = true;
        }else {
            h = h - (h % this.step);
            int a = scan(chunk, x + 1, y);
            int b = scan(chunk, x - 1, y);
            int c = scan(chunk, x, y + 1);
            int d = scan(chunk, x, y - 1);
            boundary = h > a || h > b || h > c || h > d;
        }

        return new TerrainDatum(value, boundary, water, depth);
    }

    private int scan(IChunkData c, int x, int z){
        Pointer<Integer> height = new Pointer<>(0);
        c.traverseColumn(x, z, (y, state) -> {
            Tuple<String, TFCBlockTypes.Type> res = TFCBlockTypes.getName(state.getBlock(), TFCBlockTypes.Classification.ROCK, TFCBlockTypes.Classification.SEDIMENT);
            if(res != null){
                height.value = y;
                return true;
            }
            return false;
        });
        return height.value;
    }
}
