package lordfokas.cartography.core.mapping;

import java.awt.Color;

public class Colors {

    public static int HSB2ARGB(float h, float s, float b){
        return 0xFF000000|Color.getHSBColor(h, s, b).getRGB();
    }

    public static float normalizeHue(float hue){
        while(hue <   0F) hue += 360F;
        while(hue > 360F) hue -= 360F;
        return hue / 360F;
    }

    public static int darken(int color){
        int alpha = color & 0xFF000000;
        color = (color >> 1) & 0x7F7F7F;
        return color | alpha;
    }
}
