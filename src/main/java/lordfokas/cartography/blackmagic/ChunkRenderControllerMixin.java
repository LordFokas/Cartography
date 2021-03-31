package lordfokas.cartography.blackmagic;

import journeymap.client.cartography.ChunkRenderController;
import journeymap.client.model.*;
import journeymap.client.render.ComparableBufferedImage;
import journeymap.common.Journeymap;
import journeymap.common.log.LogFormatter;
import lordfokas.cartography.integration.journeymap.JMHacks;
import lordfokas.cartography.integration.terrafirmacraft.TFCMaps;
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
        if(Journeymap.getClient().isMapping() && JMHacks.isCustom(mapType.name)){
            boolean renderOkay = false;

            try {
                RegionImageSet regionImageSet = RegionImageCache.INSTANCE.getRegionImageSet(rCoord);
                MapType type = MapType.from(mapType.name, null, rCoord.dimension);
                ComparableBufferedImage chunkSliceImage = regionImageSet.getChunkImage(chunkMd, type);
                if (mapType.name == JMHacks.ISOHYETAL) {
                    renderOkay = TFCMaps.RAINFALL.render(chunkSliceImage, chunkMd, null);
                }else if (mapType.name == JMHacks.ISOTHERMAL) {
                    renderOkay = TFCMaps.TEMPERATURE.render(chunkSliceImage, chunkMd, null);
                }else if (mapType.name == JMHacks.GEOLOGICAL) {
                    renderOkay = TFCMaps.GEOLOGY.render(chunkSliceImage, chunkMd, null);
                }
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
