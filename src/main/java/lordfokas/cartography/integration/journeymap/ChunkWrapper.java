package lordfokas.cartography.integration.journeymap;

import journeymap.client.model.ChunkMD;
import lordfokas.cartography.core.IChunkData;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;


class ChunkWrapper implements IChunkData {
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

        return pos.getY();
    }

    @Override
    public int getWaterDepth(int x, int z) {
        int wx = master.toWorldX(x);
        int wz = master.toWorldZ(z);

        int h = getTerrainHeight(x, z);
        if(h == 0) return 0;

        World w = master.getWorld();
        if(!w.isWaterAt(new BlockPos(wx, --h, wz))) return 0;
        int d = 1;

        while(h>=d && w.isWaterAt(new BlockPos(wx, h-d, wz))){
            d++;
        }

        return d;
    }

    @Override
    public Biome getBiome(int x, int z) {
        int s = getSeaLevel();
        return master.getBiome(master.getBlockPos(x, s, z));
    }

    @Override
    public void traverseColumn(int x, int z, IColumnVisitor visitor) {
        int wx = master.toWorldX(x);
        int wz = master.toWorldZ(z);
        int y = getTerrainHeight(x, z);
        World w = master.getWorld();
        boolean loop = true;
        while(loop && y > 0){
            BlockState state = w.getBlockState(new BlockPos(wx, --y, wz));
            loop = visitor.visit(y, state);
        }
    }
}
