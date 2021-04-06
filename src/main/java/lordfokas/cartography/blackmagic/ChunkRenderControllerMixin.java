package lordfokas.cartography.blackmagic;

import journeymap.client.JourneymapClient;
import journeymap.client.cartography.ChunkRenderController;
import journeymap.client.cartography.render.BaseRenderer;
import journeymap.client.model.*;
import journeymap.client.render.ComparableBufferedImage;
import journeymap.common.Journeymap;
import journeymap.common.log.LogFormatter;
import lordfokas.cartography.integration.journeymap.blackmagic.JMHacks;
import lordfokas.cartography.integration.journeymap.JMIntegration;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = ChunkRenderController.class, remap = false)
public class ChunkRenderControllerMixin {
    private static final String renderChunk = "renderChunk(Ljourneymap/client/model/RegionCoord;Ljourneymap/client/model/MapType;Ljourneymap/client/model/ChunkMD;)Z";

    @Inject(method = renderChunk, at = @At("HEAD"), cancellable = true)
    public void interceptRenderChunk(RegionCoord rCoord, MapType mapType, ChunkMD chunkMd, CallbackInfoReturnable<Boolean> ret){
        if(JourneymapClient.getInstance().isMapping() && JMHacks.isCustom(mapType.name)){
            boolean renderOkay = false;

            try {
                RegionImageSet regionImageSet = RegionImageCache.INSTANCE.getRegionImageSet(rCoord);
                MapType type = MapType.from(mapType.name, null, rCoord.dimension);
                ComparableBufferedImage chunkSliceImage = regionImageSet.getChunkImage(chunkMd, type);
                BaseRenderer renderer = JMIntegration.getRenderer(JMHacks.getCustom(mapType.name));
                renderOkay = renderer.render(chunkSliceImage, chunkMd, null);
                if (renderOkay) regionImageSet.setChunkImage(chunkMd, type, chunkSliceImage);
            } catch (ArrayIndexOutOfBoundsException var9) {
                Journeymap.getLogger().log(Level.WARN, LogFormatter.toString(var9));
                ret.setReturnValue(false); return;
            } catch (Throwable var10) {
                Journeymap.getLogger().error("Unexpected error in ChunkRenderController: " + LogFormatter.toString(var10));
            }

            if (!renderOkay && Journeymap.getLogger().isDebugEnabled()) {
                Journeymap.getLogger().debug(String.format("Chunk %s render failed for %s", chunkMd.getCoord(), mapType));
            }
            ret.setReturnValue(renderOkay);
        }
    }
}
