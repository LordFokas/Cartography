package lordfokas.cartography.blackmagic;

import journeymap.client.model.MapType;
import lordfokas.cartography.integration.journeymap.JMHacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MapType.Name.class, remap = false)
public class MapTypeNameMixin {

    @Inject(method = "values()[Ljourneymap/client/model/MapType$Name;", at = @At("HEAD"), cancellable = true)
    private static void interceptValues(CallbackInfoReturnable<MapType.Name[]> ret){
        MapType.Name[] names = JMHacks.getAllNames();
        if(names.length > 0) ret.setReturnValue(names);
    }

    @Inject(method = "valueOf(Ljava/lang/String;)Ljourneymap/client/model/MapType$Name;", at = @At("HEAD"), cancellable = true)
    private static void interceptLookup(String name, CallbackInfoReturnable<MapType.Name> ret){
        for(MapType.Name entry : JMHacks.getAllNames()){
            if(entry.name().equals(name)){
                ret.setReturnValue(entry);
                break;
            }
        }
    }
}
