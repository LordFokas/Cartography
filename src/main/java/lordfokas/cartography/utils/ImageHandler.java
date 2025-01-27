package lordfokas.cartography.utils;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.Function;
import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import com.mojang.blaze3d.platform.NativeImage;
import lordfokas.cartography.Cartography;

public class ImageHandler {
    private static final ResourceLocation FRAME = Cartography.resource("textures/label_frame.png");
    private static final ResourceLocation FONT = Cartography.resource("textures/label_font.png");
    private static final NativeImage NOT_FOUND;
    private static NativeImage frameBegin, frameMid, frameEnd;

    private static final HashMap<ResourceLocation, NativeImage> BASE = new HashMap<>();
    private static final HashMap<Character, NativeImage> CHARS = new HashMap<>();
    private static final HashMap<String, DynamicLabel> LABELS = new HashMap<>();
    private static final HashMap<ResourceLocation, Integer> COLORS = new HashMap<>();

    @Nonnull
    public static synchronized NativeImage getImage(ResourceLocation texture) {
        return BASE.computeIfAbsent(texture, $ -> {
            try {
                Resource res = Minecraft.getInstance().getResourceManager().getResource(texture);
                return NativeImage.read(res.getInputStream());
            }
            catch(IOException e) {
                e.printStackTrace();
                return NOT_FOUND;
            }
        });
    }

    public static int getColor(ResourceLocation path){
        return COLORS.computeIfAbsent(path, $ -> {
            NativeImage image = getImage(path);
            int chosen = -1;
            float best = 0;
            float[] hsb = new float[3];

            for(int x = 0; x < image.getWidth(); x++){
                for(int y = 0; y < image.getHeight(); y++){
                    int pixel = image.getPixelRGBA(x, y);
                    if((pixel & 0xFF000000) != 0xFF000000) continue;
                    // Mojang's NativeImage is ABGR
                    int b = (pixel >> 16) & 0xFF;
                    int g = (pixel >> 8) & 0xFF;
                    int r = (pixel) & 0xFF;
                    Color.RGBtoHSB(r, g, b, hsb);
                    float score = hsb[1] + hsb[2];
                    if(score > best){
                        chosen = pixel;
                        best = score;
                    }
                }
            }

            return Colors.argb2abgr(chosen);
        });
    }

    public static class DynamicLabel implements AutoCloseable {
        public final DynamicTexture texture;
        public final NativeImage image;
        public final ResourceLocation path;

        private DynamicLabel(NativeImage image, ResourceLocation path) {
            this.image = image;
            this.path = path;
            this.texture = new DynamicTexture(image);

            Minecraft.getInstance().getTextureManager().register(path, texture);
            texture.upload();
        }

        @Override
        public void close() {
            texture.close();
            image.close();
        }
    }

    @Nonnull
    public static synchronized DynamicLabel getLabel(String text) {
        return getLabel(text, null, Colors.NO_TINT);
    }

    @Nonnull
    public static synchronized DynamicLabel getLabel(String text, ResourceLocation icon_path) {
        return getLabel(text, icon_path, Colors.NO_TINT);
    }

    @Nonnull
    public static synchronized DynamicLabel getLabel(String text, ResourceLocation icon_path, int tint) {
        String key = text.replaceAll("[^a-zA-Z0-9_/.-]", "").toLowerCase(Locale.ROOT)+"---ft"+tint;
        final int tint_abgr = Colors.argb2abgr(tint); // *sigh*
        if(LABELS.containsKey(key)) {
            return LABELS.get(key);
        }

        int w = frameBegin.getWidth();

        int icon_width = 0;
        NativeImage icon = null;
        if(icon_path != null) {
            icon = getImage(icon_path);
            icon = downscaleTo16(icon);
            icon_width = icon.getWidth() + w;
        }

        char[] chars = text.toCharArray();
        NativeImage[] images = new NativeImage[chars.length];
        int width = images.length - 1 + (w * 2) + icon_width;
        for(int i = 0; i < chars.length; i++) {
            char c = chars[i];
            NativeImage b = CHARS.get(c);
            width += b.getWidth();
            images[i] = b;
        }

        int height = frameBegin.getHeight();
        NativeImage label = new NativeImage(width, height, false);
        copyToBuffer(label, frameBegin, 0, 0);
        int l = (width / w);
        for(int i = 1; i < l; i++) {
            copyToBuffer(label, frameMid, w * i, 0);
        }
        copyToBuffer(label, frameEnd, width - w, 0);

        int offset = w + icon_width;
        for(NativeImage ch : images) {
            copyToBuffer(label, ch, offset, 6, ImageHandler::spriteIsOpaque);
            offset += ch.getWidth() + 1;
        }

        // Tint all opaque pixels
        modifyBuffer(label, ImageHandler::bufferIsOpaque, pixel -> pixel & tint_abgr);

        // Icon goes after text to not be affected by tint
        if(icon != null) copyToBuffer(label, icon, 4, 4, ImageHandler::spriteIsOpaque);

        DynamicLabel dynamic = new DynamicLabel(label, Cartography.resource("textures/dynamic/label/" + key));
        LABELS.put(key, dynamic);
        return dynamic;
    }

    @Nonnull
    public static NativeImage upscale(NativeImage input, int factor) {
        int w = input.getWidth() * factor, h = input.getHeight() * factor;
        NativeImage output = new NativeImage(w, h, false);
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                output.setPixelRGBA(x, y, input.getPixelRGBA(x / factor, y / factor));
            }
        }
        return output;
    }

    /**
     * This assumes all textures being processed are square.
     */
    @Nonnull
    public static NativeImage downscaleTo16(NativeImage input) {
        int width = input.getWidth();

        int invScaleFactor = width / 16;
        if (width % 16 > 0) {
            invScaleFactor += 1;
        }

        // float scaleFactor = 1.0f / invScaleFactor;

        NativeImage output = new NativeImage(16, 16, false);
        for(int y = 0; y < 16; y++) {
            for(int x = 0; x < 16; x++) {
                // TODO: Make a nicer colour sampling for downscaling than just "every xth pixel"
                output.setPixelRGBA(x, y, input.getPixelRGBA(x * invScaleFactor, y * invScaleFactor));
            }
        }

        return output;
    }

    static {
        NativeImage pink = new NativeImage(1, 1, false);
        pink.setPixelRGBA(0, 0, 0xFFFF00FF);
        NOT_FOUND = upscale(pink, 16);
    }

    public static void init() {
        NativeImage font = getImage(FONT);
        char[][] lines = new char[][] {
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray(),
            "abcdefghijklmnopqrstuvwxyz".toCharArray(),
            "0123456789*-+<>.:,;!#%/()=?".toCharArray()
        };

        int line_height = font.getHeight() / lines.length;
        int line_width = font.getWidth();
        for(int line = 0; line < lines.length; line++) {
            NativeImage line_source = makeSnippet(font, 0, line * line_height, line_width, line_height);
            int offset = 0;
            for(char c : lines[line]) {
                offset = makeChar(line_source, c, offset);
            }
        }
        CHARS.put(' ', new NativeImage(line_height / 4, line_height, false));

        NativeImage frame = getImage(FRAME);
        NativeImage[] frame_parts = new NativeImage[3];
        int part_width = frame.getWidth() / frame_parts.length;
        int part_height = frame.getHeight();
        for(int i = 0; i < 3; i++) {
            frame_parts[i] = makeSnippet(frame, i * part_width, 0, part_width, part_height);
        }
        frameBegin = frame_parts[0];
        frameMid = frame_parts[1];
        frameEnd = frame_parts[2];
    }

    private static int makeChar(NativeImage source, char c, int offset) {
        int width = getCharWidth(source, offset);
        int height = source.getHeight();
        NativeImage target = makeSnippet(source, offset, 0, width, height);
        CHARS.put(c, target);
        return offset + width + 1;
    }

    private static int getCharWidth(NativeImage source, int offset) {
        int height = source.getHeight();
        int width = source.getWidth();

        columns:
        for(int x = offset; x < width; x++) {
            for(int y = 0; y < height; y++) {
                if((source.getPixelRGBA(x, y) & 0xFF000000) != 0) {
                    continue columns;
                }
            }
            return x - offset;
        }

        return width - offset;
    }

    private static void copyToBuffer(NativeImage buffer, NativeImage sprite, int buffer_x, int buffer_y) {
        copyToBuffer(buffer, sprite, buffer_x, buffer_y, ImageHandler::always);
    }

    private static void copyToBuffer(NativeImage buffer, NativeImage sprite, int buffer_x, int buffer_y, IPixelSelectionPredicate predicate) {
        int width = sprite.getWidth();
        int height = sprite.getHeight();

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                int bx = buffer_x + x;
                int by = buffer_y + y;
                int rgb = sprite.getPixelRGBA(x, y);
                if(predicate.test(buffer.getPixelRGBA(bx, by), rgb)) {
                    buffer.setPixelRGBA(bx, by, rgb);
                }
            }
        }
    }

    private static void modifyBuffer(NativeImage buffer, IPixelSelectionPredicate predicate, Function<Integer, Integer> function) {
        for(int x = 0; x < buffer.getWidth(); x++) {
            for(int y = 0; y < buffer.getHeight(); y++) {
                int argb = buffer.getPixelRGBA(x, y);
                if(predicate.test(argb, 0)) {
                    buffer.setPixelRGBA(x, y, function.apply(argb));
                }
            }
        }
    }

    private static NativeImage makeSnippet(NativeImage source, int x, int y, int w, int h) {
        NativeImage result = new NativeImage(w, h, false);
        for(int px = 0; px < w; px++) {
            for(int py = 0; py < h; py++) {
                int bx = x + px;
                int by = y + py;
                result.setPixelRGBA(px, py, source.getPixelRGBA(bx, by));
            }
        }
        return result;
    }

    @FunctionalInterface
    private interface IPixelSelectionPredicate {
        boolean test(int buffer, int sprite);
    }

    private static boolean always(int buffer, int sprite) {
        return true;
    }

    private static boolean spriteIsOpaque(int buffer, int sprite) {
        return (sprite & 0xFF000000) != 0;
    }

    private static boolean bufferIsOpaque(int buffer, int sprite) {
        return (buffer & 0xFF000000) != 0;
    }
}
