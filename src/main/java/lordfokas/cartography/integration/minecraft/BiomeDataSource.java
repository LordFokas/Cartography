package lordfokas.cartography.integration.minecraft;

import lordfokas.cartography.integration.journeymap.DataType;
import lordfokas.cartography.integration.journeymap.IChunkData;
import lordfokas.cartography.integration.journeymap.continuous.ContinuousDatum;
import lordfokas.cartography.integration.journeymap.continuous.IContinuousDataSource;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BiomeDataSource implements IContinuousDataSource {
    private static final Map<ResourceLocation, Float> MAPPING = new HashMap<>();
    private static final float UNKNOWN_BIOME = 330F;

    public static void setBiomeColor(ResourceLocation biome, float color){
        MAPPING.put(biome, color);
    }

    @Override
    public DataType getDataType() {
        return DataType.BIOME;
    }

    @Override
    public ContinuousDatum getDatum(IChunkData chunk, int x, int y) {
        float value = UNKNOWN_BIOME;
        ResourceLocation biome = chunk.getBiome(x, y).getRegistryName();
        if(MAPPING.containsKey(biome)){
            value = MAPPING.get(biome);
        }
        return new ContinuousDatum(value);
    }
}
