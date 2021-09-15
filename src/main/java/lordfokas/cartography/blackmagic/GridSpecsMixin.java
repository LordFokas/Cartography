package lordfokas.cartography.blackmagic;

import journeymap.client.model.GridSpec;
import journeymap.client.model.GridSpecs;
import journeymap.client.model.MapType;
import lordfokas.cartography.integration.journeymap.JMHacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GridSpecs.class, remap = false)
public class GridSpecsMixin {
    private static final String getSpec = "Ljourneymap/client/model/GridSpecs;getSpec(Ljourneymap/client/model/MapType;)Ljourneymap/client/model/GridSpec;";

    @Shadow
    private GridSpec day;

    @Inject(method = getSpec, at = @At("HEAD"), cancellable = true)
    public void getSpec(MapType mapType, CallbackInfoReturnable<GridSpec> ret) {
        if(JMHacks.isCustom(mapType.name)){
            ret.setReturnValue(day);
        }
    }
}
