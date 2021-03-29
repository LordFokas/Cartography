package lordfokas.cartography.integration.journeymap;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public interface IChunkData {
    Chunk getChunk(int x, int z);

    int getWorldHeight();
    int getTerrainHeight(int x, int z);
    int getWaterDepth(int x, int z);

    Biome getBiome(int x, int z);

    void traverseColumn(int x, int z, IColumnVisitor visitor);

    interface IColumnVisitor{
        boolean visit(int y, BlockState state);
    }
}
