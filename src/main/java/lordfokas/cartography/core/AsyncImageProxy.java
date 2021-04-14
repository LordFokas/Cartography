package lordfokas.cartography.core;

import lordfokas.cartography.core.data.ThreadHandler;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class AsyncImageProxy {
    private static final HashMap<Integer, HashMap<ResourceLocation, BufferedImage>> SCALED = new HashMap<>();
    private static final HashMap<ResourceLocation, BufferedImage> BASE = new HashMap<>();

    @Nonnull
    public static BufferedImage getImage(ResourceLocation texture){
        return BASE.computeIfAbsent(texture, $ -> ThreadHandler.getOnGameThreadBlocking($$ -> ImageHandler.getImage(texture)));
    }

    @Nonnull
    public static BufferedImage getImage(ResourceLocation texture, int scale){
        if(scale < 1) throw new IllegalArgumentException("Image scale has to be >= 1");
        if(scale == 1) return getImage(texture);

        HashMap<ResourceLocation, BufferedImage> cache = SCALED.computeIfAbsent(scale, $ -> new HashMap<>());
        return cache.computeIfAbsent(texture, $ -> ImageHandler.upscale(getImage(texture), scale));
    }
}