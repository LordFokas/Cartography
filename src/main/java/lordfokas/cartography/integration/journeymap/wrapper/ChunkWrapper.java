package lordfokas.cartography.integration.journeymap.wrapper;

import journeymap.client.model.ChunkMD;
import lordfokas.cartography.integration.journeymap.IChunkData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;

public class ChunkWrapper implements IChunkData {
    private final CustomChunkRenderer source;
    private final ChunkMD master;

    public ChunkWrapper(CustomChunkRenderer source, ChunkMD master){
        this.source = source;
        this.master = master;
    }

    private ChunkMD getActualChunk(int x, int z){
        if(x < 0 || x > 15 || z < 0 || z > 15){
            ChunkPos cur = master.getCoord();
            int cx = ((cur.x << 4) + x) >> 4;
            int cz = ((cur.z << 4) + z) >> 4;
            return source.getChunkAt(new ChunkPos(cx, cz));
        }else{
            return master;
        }
    }

    private int clamp(int v){
        if(v <  0) return v+16;
        if(v > 15) return v-16;
        return v;
    }

    @Override
    public int getWorldHeight() {
        return master.getWorldActualHeight();
    }

    public int getSeaLevel() {
        return master.getWorld().getSeaLevel();
    }

    @Override
    public int getPrecipitationHeight(int x, int z) {
        ChunkMD chunk = getActualChunk(x, z);
        x = clamp(x);
        z = clamp(z);

        return chunk.getPrecipitationHeight(x, z);
    }

    @Override
    public float getTemperature(int x, int z) {
        ChunkMD chunk = getActualChunk(x, z);
        x = clamp(x);
        z = clamp(z);

        return 0;
    }

    @Override
    public float getRainfall(int x, int z) {
        ChunkMD chunk = getActualChunk(x, z);
        x = clamp(x);
        z = clamp(z);

        return 0;
    }

    @Override
    public Biome getBiome(int x, int z) {
        int s = getSeaLevel();
        return master.getBiome(master.getBlockPos(x, s, z));
    }
}
