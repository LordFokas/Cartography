package lordfokas.cartography.integration.terrafirmacraft;

import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;
import net.minecraft.world.chunk.Chunk;

public class TFCHelper {

    public static ChunkData getChunkData(Chunk chunk){
        return chunk.getCapability(ChunkDataCapability.CAPABILITY).orElse(null);
    }

}
