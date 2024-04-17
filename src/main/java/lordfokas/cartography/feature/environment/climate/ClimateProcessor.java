package lordfokas.cartography.feature.environment.climate;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import com.eerussianguy.blazemap.api.pipeline.PipelineType;
import com.eerussianguy.blazemap.api.pipeline.Processor;
import com.eerussianguy.blazemap.api.util.IDataSource;
import com.eerussianguy.blazemap.api.util.RegionPos;
import lordfokas.cartography.CartographyReferences;
import lordfokas.cartography.feature.mapping.climate.ClimateIsolinesMD;

public class ClimateProcessor extends Processor.Direct {

    public ClimateProcessor() {
        super(
            CartographyReferences.Processors.CLIMATE,
            CartographyReferences.MasterData.CLIMATE_ISO
        );
    }

    @Override
    public boolean shouldExecuteIn(ResourceKey<Level> dimension, PipelineType pipeline) {
        return pipeline.isClient;
    }

    @Override
    public void execute(ResourceKey<Level> dimension, RegionPos region, ChunkPos chunk, IDataSource data) {
        ClimateIsolinesMD isolines = (ClimateIsolinesMD) data.get(CartographyReferences.MasterData.CLIMATE_ISO);

        process(ClimateProcessor::temperature, isolines, 1, ((mx, my, angle, v) -> {
            String value = String.valueOf(v);
            ClimateClusterStore.getTemperaturePool(dimension, value).addData(chunk, Isoline.of(chunk, value, "*C", angle, mx, my));
        }));

        process(ClimateProcessor::rainfall, isolines, 10, ((mx, my, angle, v) -> {
            String value = String.valueOf(v);
            ClimateClusterStore.getRainfallPool(dimension, value).addData(chunk, Isoline.of(chunk, value, "mm", angle, mx, my));
        }));
    }

    private void process(Metric metric, ClimateIsolinesMD isolines, int skip, LabelPlacer labels) {
        int xi = 0, zi = 0, xf = 0, zf = 0;
        boolean initial = true, line = false;

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int value = metric.get(isolines, x, z);
                boolean isBorder = value != ClimateIsolinesMD.NONE && value % skip == 0;

                if(isBorder) {
                    if(initial) {
                        xi = x;
                        zi = z;
                        initial = false;
                    }
                    else {
                        xf = x;
                        zf = z;
                        line = true;
                    }
                }
            }
        }

        if(line) {
            int value = metric.get(isolines, xi, zi);
            int mx = (xi + xf) / 2, mz = (zi + zf) / 2;
            float dx = xf - xi, dz = zf - zi;
            float angle = (float) ((Math.toDegrees(Math.atan(dz / dx)) + 270) % 360) + 90;
            labels.put(mx, mz, angle, value);
        }
    }

    private static int rainfall(ClimateIsolinesMD isolines, int x, int z) {
        return isolines.rainfall[x][z];
    }

    private static int temperature(ClimateIsolinesMD isolines, int x, int z) {
        return isolines.temperature[x][z];
    }

    @FunctionalInterface
    private interface Metric {
        int get(ClimateIsolinesMD isolines, int x, int z);
    }

    @FunctionalInterface
    private interface LabelPlacer {
        void put(int mx, int mz, float angle, int value);
    }
}
