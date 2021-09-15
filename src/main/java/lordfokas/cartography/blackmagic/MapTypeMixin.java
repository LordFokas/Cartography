package lordfokas.cartography.blackmagic;

import journeymap.client.api.display.Context;
import journeymap.client.model.MapType;
import lordfokas.cartography.integration.journeymap.JMHacks;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MapType.class, remap = false)
public abstract class MapTypeMixin {
    @SuppressWarnings("unused")
    @Shadow public abstract boolean isAllowed();

    @Shadow @Final public MapType.Name name;
    private static final String fromApiType = "fromApiContextMapType(Ljourneymap/client/api/display/Context$MapType;Ljava/lang/Integer;Lnet/minecraft/resources/ResourceKey;)Ljourneymap/client/model/MapType;";
    private static final String toApiType = "toApiContextMapType(Ljourneymap/client/model/MapType$Name;)Ljourneymap/client/api/display/Context$MapType;";
    private static final String isAllowed = "isAllowed()Z";

    @Inject(method = toApiType, at = @At("HEAD"), cancellable = true)
    private void interceptForwardLookup(MapType.Name name, CallbackInfoReturnable<Context.MapType> ret){
        Context.MapType type = JMHacks.lookup(name);
        ret.setReturnValue(type);
    }

    @Inject(method = fromApiType, at = @At("HEAD"), cancellable = true)
    private static void interceptReverseLookup(Context.MapType type, Integer vSlice, ResourceKey<Level> dimension, CallbackInfoReturnable<MapType> ret){
        MapType.Name name = JMHacks.lookup(type);
        MapType value = new MapType(name, vSlice, dimension);
        ret.setReturnValue(value);
    }

    @Inject(method = isAllowed, at = @At("HEAD"), cancellable = true)
    private void interceptIsAllowed(CallbackInfoReturnable<Boolean> ret){
        if(JMHacks.isCustom(this.name)) ret.setReturnValue(true);
    }
}