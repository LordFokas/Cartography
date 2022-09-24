package lordfokas.cartography.feature.data;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import com.eerussianguy.blazemap.api.mapping.Processor;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.eerussianguy.blazemap.api.util.RegionPos;
import lordfokas.cartography.CartographyReferences;

public class ClimateProcessor extends Processor {

    public ClimateProcessor() {
        super(
            CartographyReferences.Processors.CLIMATE,
            CartographyReferences.Collectors.CLIMATE
        );
    }

    @Override
    public boolean execute(ResourceKey<Level> dimension, RegionPos region, ChunkPos chunk, IDataSource data) {
        ClimateMD climate = (ClimateMD) data.get(CartographyReferences.Collectors.CLIMATE);
        process(ClimateProcessor::temperature, climate, 1, ((mx, my, angle, value) -> ClimateClusterStore.add(dimension, chunk, mx, my, angle, value, "*C")));
        process(ClimateProcessor::rainfall, climate, 10, ((mx, my, angle, value) -> ClimateClusterStore.add(dimension, chunk, mx, my, angle, value, "mm")));
        return true;
    }

    private void process(Metric metric, ClimateMD climate, int skip, LabelPlacer labels){
        int xi = 0, yi = 0, xf = 0, yf = 0;
        boolean initial = true, line = false;

        for(int x = 0; x < 16; x++){
            for(int y = 0; y < 16; y++){
                boolean isBorder;
                float value = metric.get(climate, x, y, 0);
                isBorder = delta(value, metric.get(climate, x + 1, y, value), skip, false);
                isBorder = delta(value, metric.get(climate, x - 1, y, value), skip, isBorder);
                isBorder = delta(value, metric.get(climate, x, y + 1, value), skip, isBorder);
                isBorder = delta(value, metric.get(climate, x, y - 1, value), skip, isBorder);

                if(isBorder){
                    if(initial){
                        xi = x;
                        yi = y;
                        initial = false;
                    }else{
                        xf = x;
                        yf = y;
                        line = true;
                    }
                }
            }
        }

        if(line) {
            int value = (int) Math.floor(metric.get(climate, xi, yi, 0));
            int mx = (xi + xf) / 2, my = (yi + yf) / 2;
            float dx = xf - xi, dy = yf - yi;
            float angle = (float) ((Math.toDegrees(Math.atan(dy / dx)) + 270 ) % 360 ) + 90;
            labels.put(mx, my, angle, value);
        }
    }

    private static float rainfall(ClimateMD climate, int x, int z, float def) {
        return x >= 0 && z >= 0 && x <= 15 && z <= 15 ? climate.rainfall[x][z] : def;
    }

    private static float temperature(ClimateMD climate, int x, int z, float def) {
        return x >= 0 && z >= 0 && x <= 15 && z <= 15 ? climate.temperature[x][z] : def;
    }

    private static boolean delta(float pixel, float neighbor, int skip, boolean prev) {
        if(prev) return true;
        int value = (int) Math.floor(pixel);
        return  value % skip == 0 && value == Math.ceil(neighbor);
    }

    @FunctionalInterface
    private interface Metric {
        float get(ClimateMD climate, int x, int z, float def);
    }

    @FunctionalInterface
    private interface LabelPlacer {
        void put(int mx, int my, float angle, int value);
    }
}
