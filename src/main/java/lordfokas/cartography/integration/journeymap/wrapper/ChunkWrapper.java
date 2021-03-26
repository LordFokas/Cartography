package lordfokas.cartography.integration.journeymap.wrapper;

import journeymap.client.model.ChunkMD;
import lordfokas.cartography.integration.journeymap.IChunkData;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;

import java.util.Map;

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

    @Override
    public Chunk getChunk(int x, int z){
        return getActualChunk(x, z).getChunk();
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
    public int getTerrainHeight(int x, int z) {
        int wx = master.toWorldX(x);
        int wz = master.toWorldZ(z);

        BlockPos pos = master.getWorld().getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, new BlockPos(wx, 0, wz));
        int h = pos.getY();

        System.err.println("World height: "+h);

        return h;
    }

    @Override
    public int getWaterDepth(int x, int z) {
        int wx = master.toWorldX(x);
        int wz = master.toWorldZ(z);

        World w = master.getWorld();

        BlockPos pos = w.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, new BlockPos(wx, 0, wz));
        int h = pos.getY();
        if(h == 0) return 0;


        if(!w.isWaterAt(new BlockPos(wx, --h, wz))) return 0;
        int d = 1;

        while(h>=d && w.isWaterAt(new BlockPos(wx, h-d, wz))){
            d++;
        }

        return d;
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
