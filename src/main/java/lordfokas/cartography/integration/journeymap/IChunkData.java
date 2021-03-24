package lordfokas.cartography.integration.journeymap;

import net.minecraft.world.biome.Biome;

public interface IChunkData {
    int getWorldHeight();
    int getPrecipitationHeight(int x, int z);

    float getTemperature(int x, int z);
    float getRainfall(int x, int z);

    Biome getBiome(int x, int z);
}
