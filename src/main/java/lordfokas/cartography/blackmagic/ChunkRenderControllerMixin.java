package lordfokas.cartography.blackmagic;

import journeymap.client.cartography.ChunkRenderController;
import lordfokas.cartography.integration.terrafirmacraft.TFCMaps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(value = ChunkRenderController.class, remap = false)
public class ChunkRenderControllerMixin {

    @Inject(method = "<init>()V", at = @At("TAIL"))
    public void interceptConstructor(CallbackInfo callback){
        try{
            Field topo = ChunkRenderController.class.getDeclaredField("topoRenderer");
            topo.setAccessible(true);
            topo.set(this, TFCMaps.TEMPERATURE);
        } catch(ReflectiveOperationException e){
            e.printStackTrace();
        }
    }
}
