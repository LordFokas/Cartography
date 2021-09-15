package lordfokas.cartography.core.mapping;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public interface IChunkData {
    LevelChunk getChunk(int x, int z);
    ResourceKey<Level> getDimension();
    ChunkPos getPos();

    int getWorldHeight();
    int getTerrainHeight(int x, int z);
    int getWaterDepth(int x, int z);

    void traverseColumn(int x, int z, IColumnVisitor visitor);

    interface IColumnVisitor{
        boolean visit(int y, BlockState state);
    }
}
