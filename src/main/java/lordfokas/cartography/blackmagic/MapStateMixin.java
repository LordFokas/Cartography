package lordfokas.cartography.blackmagic;

import com.google.common.collect.Iterables;
import journeymap.client.data.DataCache;
import journeymap.client.model.EntityDTO;
import journeymap.client.model.MapState;
import journeymap.client.model.MapType;
import lordfokas.cartography.integration.journeymap.blackmagic.JMHacks;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(value = MapState.class, remap = false)
public class MapStateMixin {
    @Shadow private boolean surfaceMappingAllowed;
    @Shadow private boolean caveMappingAllowed;
    @Shadow private boolean topoMappingAllowed;

    /** @author LordFokas */ @Overwrite
    public MapType.Name getNextMapType(MapType.Name name) {
        EntityDTO player = DataCache.getPlayer();
        LivingEntity playerEntity = player.entityLivingRef.get();
        if (playerEntity == null) {
            return name;
        } else {
            List<MapType.Name> types = new ArrayList<>(8);
            if (this.surfaceMappingAllowed) { types.add(MapType.Name.day); types.add(MapType.Name.night); }
            if (this.caveMappingAllowed && (player.underground || name == MapType.Name.underground)) { types.add(MapType.Name.underground); }
            if (this.topoMappingAllowed) { types.add(MapType.Name.topo); }
            types.addAll(JMHacks.getCustomNames());

            if (name == MapType.Name.none && !types.isEmpty()) {
                return types.get(0);
            } else {
                if (types.contains(name)) {
                    Iterator<MapType.Name> cyclingIterator = Iterables.cycle(types).iterator();
                    while(cyclingIterator.hasNext()) {
                        MapType.Name current = cyclingIterator.next();
                        if (current == name) {
                            return cyclingIterator.next();
                        }
                    }
                }
                return name;
            }
        }
    }
}
