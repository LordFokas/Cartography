package lordfokas.cartography.utils;

import java.awt.*;

public class Colors {

    public static final int NO_TINT = -1;

    public static int HSB2ABGR(float h, float s, float b){
        Color color = Color.getHSBColor(h, s, b);
        return 0xFF000000 | color.getBlue() << 16 | color.getGreen() << 8 | color.getRed();
    }

    // Well, fuck you too Mojang. Get some standards.
    public static int argb2abgr(int input){
        int r = input & 0xFF0000;
        int b = input & 0x0000FF;
        input &= 0xFF00FF00;
        return input | (b << 16) | (r >> 16);
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
