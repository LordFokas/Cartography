package lordfokas.cartography.core.markers;

import lordfokas.cartography.core.DataType;
import lordfokas.cartography.core.MapType;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

public interface IMarkerPlacer {
    void place(RegistryKey<World> dim, int lx, int lz, String text, int hue, DataType type, int abs, int angle, MapType ... types);
}