package lordfokas.cartography.integration.journeymap;

import journeymap.client.cartography.Stratum;
import journeymap.client.cartography.render.BaseRenderer;
import journeymap.client.model.ChunkMD;
import journeymap.client.render.ComparableBufferedImage;
import lordfokas.cartography.core.mapping.IChunkData;
import lordfokas.cartography.core.mapping.IMapRenderer;
import lordfokas.cartography.core.markers.IMarkerHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

class CustomChunkRenderer extends BaseRenderer {

    private final IMapRenderer renderer;
    private final IMarkerHandler labels;

    public CustomChunkRenderer(IMapRenderer renderer){
        this.renderer = renderer;
        this.labels = JMPlugin.instance();
    }

    @Override
    public final boolean render(ComparableBufferedImage image, ChunkMD chunkMD, Integer vslice) {
        IChunkData chunk = new ChunkWrapper(this, chunkMD);
        return this.renderer.render(image, chunk, labels);
    }

    public ChunkMD getChunkAt(ChunkPos pos){
        return dataCache.getChunkMD(pos);
    }

    @Override public void setStratumColors(Stratum stratum, int i, Integer integer, boolean b, boolean b1, boolean b2){}
    @Override public int getBlockHeight(ChunkMD chunkMD, BlockPos blockPos) { return 0; }
    @Override protected Integer getBlockHeight(ChunkMD chunkMD, int i, Integer integer, int i1, Integer integer1, Integer integer2){ return null; }
}
