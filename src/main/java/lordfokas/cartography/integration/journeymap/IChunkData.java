package lordfokas.cartography.integration.journeymap;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public interface IChunkData {
    Chunk getChunk(int x, int z);

    int getWorldHeight();
    int getTerrainHeight(int x, int z);
    int getWaterDepth(int x, int z);

    float getTemperature(int x, int z);
    float getRainfall(int x, int z);

    Biome getBiome(int x, int z);
}
