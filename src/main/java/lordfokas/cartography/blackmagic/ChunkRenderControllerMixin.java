package lordfokas.cartography.blackmagic;

import journeymap.client.cartography.ChunkRenderController;
import journeymap.client.cartography.render.BaseRenderer;
import lordfokas.cartography.integration.journeymap.continuous.ColorScale;
import lordfokas.cartography.integration.journeymap.continuous.IDataCompiler;
import lordfokas.cartography.integration.journeymap.continuous.IsoplethChunkRenderer;
import lordfokas.cartography.integration.journeymap.continuous.IsoplethDataCompiler;
import lordfokas.cartography.integration.minecraft.TerrainHeightDataSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(value = ChunkRenderController.class, remap = false)
public class ChunkRenderControllerMixin {

    @Inject(method = "<init>()V", at = @At("TAIL"))
    public void interceptConstructor(CallbackInfo callback){
        ColorScale scale = new ColorScale(0F, 270F);
        IDataCompiler compiler = new IsoplethDataCompiler(scale);
        BaseRenderer renderer = new IsoplethChunkRenderer(compiler, new TerrainHeightDataSource());

        try{
            Field topo = ChunkRenderController.class.getDeclaredField("topoRenderer");
            topo.setAccessible(true);
            topo.set(this, renderer);
        } catch(ReflectiveOperationException e){
            e.printStackTrace();
        }
    }
}
