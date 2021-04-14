package lordfokas.cartography.core.markers;

import lordfokas.cartography.core.ImageHandler;
import lordfokas.cartography.core.MapType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.image.BufferedImage;

public class Marker {
    public final String key;
    public final RegistryKey<World> dim;
    public final int worldX, worldZ;
    public final BufferedImage image;
    public final int tint;
    public final int imageScale;
    public final int markerScale;
    public final int rotation;
    public final BlockPos nw, se;
    public final MapType[] maps;

    private Marker(String key, RegistryKey<World> dim, int x, int z, BufferedImage image, int tint, int imageScale, int markerScale, int rotation, MapType ... maps){
        this.key = key;
        this.dim = dim;
        this.worldX = x;
        this.worldZ = z;
        this.image = image;
        this.tint = tint;
        this.imageScale = imageScale;
        this.markerScale = markerScale;
        this.rotation = rotation;
        this.maps = maps;

        int h = image.getHeight()/(imageScale * markerScale), w = image.getWidth()/(imageScale * markerScale);
        nw = new BlockPos(x - (w/2), 0, z - (h/2));
        se = nw.offset(w, 0, h);
    }

    public Marker(String key, RegistryKey<World> dim, int x, int z, String label, int tint, int imageScale, int markerScale, int rotation, MapType ... maps){
        this(key, dim, x, z, ImageHandler.getLabel(label, imageScale), tint, imageScale, markerScale, rotation, maps);
    }

    public Marker(String key, RegistryKey<World> dim, int x, int z, BufferedImage image, int imageScale, int markerScale, MapType ... maps){
        this(key, dim, x, z, image, 0xFFFFFF, imageScale, markerScale, 0, maps);
    }
}
