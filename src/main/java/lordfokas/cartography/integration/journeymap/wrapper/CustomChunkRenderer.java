package lordfokas.cartography.integration.journeymap.wrapper;

import journeymap.client.cartography.Stratum;
import journeymap.client.cartography.render.BaseRenderer;
import journeymap.client.model.ChunkMD;
import journeymap.client.render.ComparableBufferedImage;
import lordfokas.cartography.integration.journeymap.IChunkData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public abstract class CustomChunkRenderer extends BaseRenderer {

    @Override
    public final boolean render(ComparableBufferedImage image, ChunkMD chunkMD, Integer vslice) {
        IChunkData chunk = new ChunkWrapper(this, chunkMD);
        return this.render(image, chunk);
    }

    protected abstract boolean render(ComparableBufferedImage image, IChunkData chunk);

    public ChunkMD getChunkAt(ChunkPos pos){
        return dataCache.getChunkMD(pos);
    }

    @Override public void setStratumColors(Stratum stratum, int i, Integer integer, boolean b, boolean b1, boolean b2){}
    @Override public int getBlockHeight(ChunkMD chunkMD, BlockPos blockPos) { return 0; }
    @Override protected Integer getBlockHeight(ChunkMD chunkMD, int i, Integer integer, int i1, Integer integer1, Integer integer2){ return null; }
}
