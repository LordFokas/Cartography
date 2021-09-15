package lordfokas.cartography.core;

import lordfokas.cartography.Cartography;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

public class ImageHandler {
    private static final ResourceLocation FRAME = new ResourceLocation(Cartography.MOD_ID, "textures/label_frame.png");
    private static final ResourceLocation FONT = new ResourceLocation(Cartography.MOD_ID, "textures/label_font.png");
    private static final BufferedImage NOT_FOUND;
    private static BufferedImage frameBegin, frameMid, frameEnd;

    private static final HashMap<Integer, HashMap<ResourceLocation, BufferedImage>> SCALED = new HashMap<>();
    private static final HashMap<ResourceLocation, BufferedImage> BASE = new HashMap<>();
    private static final HashMap<Character, BufferedImage> CHARS = new HashMap<>();
    private static final HashMap<String, BufferedImage> LABELS = new HashMap<>();

    @Nonnull
    public static BufferedImage getImage(ResourceLocation texture){
        return BASE.computeIfAbsent(texture, $ -> {
            try{
                Resource res = Minecraft.getInstance().getResourceManager().getResource(texture);
                return ImageIO.read(res.getInputStream());
            }catch(IOException e){
                e.printStackTrace();
                return NOT_FOUND;
            }
        });
    }

    @Nonnull
    public static BufferedImage getImage(ResourceLocation texture, int scale){
        if(scale < 1) throw new IllegalArgumentException("Image scale has to be >= 1");
        if(scale == 1) return getImage(texture);

        HashMap<ResourceLocation, BufferedImage> cache = SCALED.computeIfAbsent(scale, $ -> new HashMap<>());
        return cache.computeIfAbsent(texture, $ -> upscale(getImage(texture), scale));
    }

    @Nonnull
    public static BufferedImage getLabel(String text, int scale){
        return getLabel(text, null, scale);
    }

    @Nonnull
    public static BufferedImage getLabel(String text, ResourceLocation icon_path, int scale){
        if(scale < 1) throw new IllegalArgumentException("Scale must be >= 1");
        String key = text+"@x"+scale;
        if(LABELS.containsKey(key)){
            return LABELS.get(key);
        }

        int w = frameBegin.getWidth();

        int icon_width = 0;
        BufferedImage icon = null;
        if(icon_path != null){
            icon = getImage(icon_path);
            icon_width = icon.getWidth() + w;
        }

        char[] chars = text.toCharArray();
        BufferedImage[] images = new BufferedImage[chars.length];
        int width = images.length - 1 + (w * 2) + icon_width;
        for(int i = 0; i < chars.length; i++){
            char c = chars[i];
            BufferedImage b = CHARS.get(c);
            width += b.getWidth();
            images[i] = b;
        }

        int height = frameBegin.getHeight();
        BufferedImage label = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        copyToBuffer(label, frameBegin, 0, 0);
        int l = (width / w);
        for(int i = 1; i < l; i++){
            copyToBuffer(label, frameMid, w*i, 0);
        }
        copyToBuffer(label, frameEnd, width - w, 0);

        if(icon != null) copyToBuffer(label, icon, 4, 4, ImageHandler::spriteIsOpaque);

        int offset = w + icon_width;
        for(BufferedImage ch : images){
            copyToBuffer(label, ch, offset, 6, ImageHandler::spriteIsOpaque);
            offset += ch.getWidth() + 1;
        }

        if(scale > 1) label = upscale(label, scale);
        LABELS.put(key, label);
        return label;
    }

    @Nonnull
    public static BufferedImage upscale(BufferedImage input, int factor){
        int w = input.getWidth() * factor, h = input.getHeight() * factor;
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for(int x = 0; x < w; x++){
            for(int y = 0; y < h; y++){
                output.setRGB(x, y, input.getRGB(x/factor, y/factor));
            }
        }
        return output;
    }

    static {
        BufferedImage pink = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        pink.setRGB(0, 0, 0xFFFF00FF);
        NOT_FOUND = upscale(pink, 16);
    }

    public static void init() {
        BufferedImage font = getImage(FONT);
        char[][] lines = new char[][]{
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray(),
                "abcdefghijklmnopqrstuvwxyz".toCharArray(),
                "0123456789*-+<>.:,;!#%/()=?".toCharArray()
        };

        int line_height = font.getHeight() / lines.length;
        int line_width = font.getWidth();
        for (int line = 0; line < lines.length; line++) {
            BufferedImage line_source = font.getSubimage(0, line * line_height, line_width, line_height);
            int offset = 0;
            for (char c : lines[line]) {
                offset = makeChar(line_source, c, offset);
            }
        }
        CHARS.put(' ', new BufferedImage(line_height/4, line_height, BufferedImage.TYPE_INT_ARGB));

        BufferedImage frame = getImage(FRAME);
        BufferedImage[] frame_parts = new BufferedImage[3];
        int part_width = frame.getWidth() / frame_parts.length;
        int part_height = frame.getHeight();
        for (int i = 0; i < 3; i++) {
            frame_parts[i] = frame.getSubimage(i * part_width, 0, part_width, part_height);
        }
        frameBegin = frame_parts[0];
        frameMid = frame_parts[1];
        frameEnd = frame_parts[2];
    }

    private static int makeChar(BufferedImage source, char c, int offset){
        int width = getCharWidth(source, offset);
        int height = source.getHeight();
        BufferedImage target = source.getSubimage(offset, 0, width, height);
        CHARS.put(c,target);
        return offset+width+1;
    }

    private static int getCharWidth(BufferedImage source, int offset){
        int height = source.getHeight();
        int width = source.getWidth();

        columns:
        for(int x = offset; x < width; x++){
            for(int y = 0; y < height; y++){
                if((source.getRGB(x, y) & 0xFF000000) != 0) {
                    continue columns;
                }
            }
            return x - offset;
        }

        return width - offset;
    }

    private static void copyToBuffer(BufferedImage buffer, BufferedImage sprite, int buffer_x, int buffer_y){
        copyToBuffer(buffer, sprite, buffer_x, buffer_y, ImageHandler::always);
    }

    private static void copyToBuffer(BufferedImage buffer, BufferedImage sprite, int buffer_x, int buffer_y, IPixelTransferPredicate predicate){
        for(int x = 0; x < sprite.getWidth(); x++){
            for(int y = 0; y < sprite.getHeight(); y++){
                int bx = buffer_x + x;
                int by = buffer_y + y;
                int rgb = sprite.getRGB(x, y);
                if(predicate.apply(buffer.getRGB(bx, by), rgb)){
                    buffer.setRGB(bx, by, rgb);
                }
            }
        }
    }

    @FunctionalInterface
    private interface IPixelTransferPredicate {
        boolean apply(int buffer, int sprite);
    }

    private static boolean always(int buffer, int sprite){ return true; }
    private static boolean spriteIsOpaque(int buffer, int sprite){ return (sprite & 0xFF000000) != 0; }
}
