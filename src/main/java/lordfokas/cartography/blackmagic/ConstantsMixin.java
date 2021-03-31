package lordfokas.cartography.blackmagic;

import journeymap.client.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Constants.class, remap = false)
public class ConstantsMixin {

    @Inject(method = "getString(Ljava/lang/String;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    private static void interceptGetString(String key, CallbackInfoReturnable<String> ret){
        if(key.startsWith("cartography.fullscreen.map_")){
            switch (key){
                case "cartography.fullscreen.map_geological":
                    ret.setReturnValue("Geological");
                    return;
                case "cartography.fullscreen.map_isohyetal":
                    ret.setReturnValue("Isohyetal");
                    return;
                case "cartography.fullscreen.map_isothermal":
                    ret.setReturnValue("Isothermal");
                    return;
                default: ;
            }
        }
    }
}
