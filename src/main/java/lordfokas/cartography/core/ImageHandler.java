package lordfokas.cartography.core;

import lordfokas.cartography.Cartography;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

public class ImageHandler {
    private static final ResourceLocation ISOPLETH_LABELS = new ResourceLocation(Cartography.MOD_ID, "textures/isopleth.png");
    private static final int WIDTH_ENDS = 3, WIDTH_CHARS = 7;
    private static final String CHAR_ORDER = "0123456789m*C-";

    private static final HashMap<ResourceLocation, BufferedImage> CACHE = new HashMap<>();
    private static final HashMap<Character, BufferedImage> CHARS = new HashMap<>();
    private static final HashMap<String, BufferedImage> LABELS = new HashMap<>();

    public static BufferedImage getImage(ResourceLocation tex){
        if(CACHE.containsKey(tex)) return CACHE.get(tex);
        try{
            IResource res = Minecraft.getInstance().getResourceManager().getResource(tex);
            BufferedImage image = ImageIO.read(res.getInputStream());
            CACHE.put(tex, image);
            return image;
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage getLabel(String text, int scale){
        if(scale < 1) throw new IllegalArgumentException("Scale must be >= 1");
        String key = text+":"+scale;
        if(LABELS.containsKey(key)){
            return LABELS.get(key);
        }
        char[] chars = ("^"+text+"$").toCharArray();
        BufferedImage[] imgs = new BufferedImage[chars.length];
        int width = 0;
        for(int i = 0; i < chars.length; i++){
            char c = chars[i];
            BufferedImage b = CHARS.get(c);
            width += b.getWidth();
            imgs[i] = b;
        }
        int height = imgs[0].getHeight();
        BufferedImage label = new BufferedImage(width, height, imgs[0].getType());
        int choff = 0;
        for(BufferedImage ch : imgs){
            for(int x = 0; x < ch.getWidth(); x++){
                for(int y = 0; y < height; y++){
                    label.setRGB(x+choff, y, ch.getRGB(x, y));
                }
            }
            choff += ch.getWidth();
        }
        if(scale > 1) label = upscale(label, scale);
        LABELS.put(key, label);
        return label;
    }

    public static BufferedImage upscale(BufferedImage input, int factor){
        int w = input.getWidth() * factor, h = input.getHeight() * factor;
        BufferedImage output = new BufferedImage(w, h, input.getType());
        for(int x = 0; x < w; x++){
            for(int y = 0; y < h; y++){
                output.setRGB(x, y, input.getRGB(x/factor, y/factor));
            }
        }
        return output;
    }

    static{
        BufferedImage source = getImage(ISOPLETH_LABELS);
        if(source == null) throw new NullPointerException("Isopleth labels image not found");
        char[] chars = CHAR_ORDER.toCharArray();
        int offset = makeChar(source, '^', WIDTH_ENDS, 0);
        for(char c : chars){
            offset = makeChar(source, c, WIDTH_CHARS, offset);
        }
        makeChar(source, '$', WIDTH_ENDS, offset);
    }

    private static int makeChar(BufferedImage source, char c, int width, int offset){
        int height = source.getHeight();
        BufferedImage target = source.getSubimage(offset, 0, width, height);
        CHARS.put(c,target);
        return offset+width+1;
    }
}
