package lordfokas.cartography.feature.mapping.climate;

import com.eerussianguy.blazemap.api.pipeline.Transformer;
import com.eerussianguy.blazemap.api.util.IDataSource;
import lordfokas.cartography.CartographyReferences;

public class ClimateIsolinesTransformer extends Transformer<ClimateIsolinesMD> {

    public ClimateIsolinesTransformer() {
        super(
            CartographyReferences.Transformers.CLIMATE_ISO,
            CartographyReferences.MasterData.CLIMATE_ISO,
            CartographyReferences.MasterData.CLIMATE
        );
    }

    @Override
    public ClimateIsolinesMD transform(IDataSource data) {
        ClimateMD climate = (ClimateMD) data.get(CartographyReferences.MasterData.CLIMATE);
        float[][] temp = climate.temperature;
        float[][] rain = climate.rainfall;
        int[][] temperature_iso = new int[16][16];
        int[][] rainfall_iso = new int[16][16];

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int temp_iso = ClimateIsolinesMD.NONE;
                float temp_val = value(temp, x, z, 0);
                temp_iso = delta(temp_val, value(temp, x + 1, z, temp_val), temp_iso);
                temp_iso = delta(temp_val, value(temp, x - 1, z, temp_val), temp_iso);
                temp_iso = delta(temp_val, value(temp, x, z + 1, temp_val), temp_iso);
                temp_iso = delta(temp_val, value(temp, x, z - 1, temp_val), temp_iso);
                temperature_iso[x][z] = temp_iso;

                int rain_iso = ClimateIsolinesMD.NONE;
                float rain_val = value(rain, x, z, 0);
                rain_iso = delta(rain_val, value(rain, x + 1, z, rain_val), rain_iso);
                rain_iso = delta(rain_val, value(rain, x - 1, z, rain_val), rain_iso);
                rain_iso = delta(rain_val, value(rain, x, z + 1, rain_val), rain_iso);
                rain_iso = delta(rain_val, value(rain, x, z - 1, rain_val), rain_iso);
                rainfall_iso[x][z] = rain_iso;
            }
        }

        return new ClimateIsolinesMD(rainfall_iso, temperature_iso);
    }

    private static float value(float[][] values, int x, int z, float def) {
        return x >= 0 && z >= 0 && x <= 15 && z <= 15 ? values[x][z] : def;
    }

    private static int delta(float pixel, float neighbor, int prev) {
        if(prev != ClimateIsolinesMD.NONE) return prev;
        int value = (int) Math.floor(pixel);
        return value == Math.ceil(neighbor) ? value : prev;
    }
}
