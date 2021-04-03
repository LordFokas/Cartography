package lordfokas.cartography.core;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

public interface ILabelPlacer {
    void place(RegistryKey<World> dim, int lx, int lz, String text, int hue, DataType type, int abs, int angle);
}
