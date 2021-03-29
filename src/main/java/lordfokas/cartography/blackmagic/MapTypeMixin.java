package lordfokas.cartography.blackmagic;

import journeymap.client.model.MapType;
import lordfokas.cartography.integration.journeymap.JMHacks;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = MapType.class, remap = false)
public class MapTypeMixin {
    @Shadow public final Integer vSlice;
    @Shadow public final MapType.Name name;
    @Shadow public final RegistryKey<World> dimension;
    @Shadow public final journeymap.client.api.display.Context.MapType apiMapType;
    @Shadow private final int theHashCode;
    @Shadow private final String theCacheKey;

    MapTypeMixin(MapType.Name name, Integer vSlice, RegistryKey<World> dimension){
        if(name != MapType.Name.underground && name != JMHacks.GEOLOGICAL){
            vSlice = null;
        }

        this.name = name;
        this.vSlice = vSlice;
        this.dimension = dimension;
        this.apiMapType = JMHacks.lookup(name);
        this.theCacheKey = MapType.toCacheKey(name, vSlice, dimension);
        this.theHashCode = this.theCacheKey.hashCode();

        System.err.printf("Constructed %s / %s with Mixin constructor!\n", name, vSlice);
    }
}