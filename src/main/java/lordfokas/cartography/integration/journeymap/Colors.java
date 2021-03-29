package lordfokas.cartography.integration.journeymap;

import java.awt.Color;

public class Colors {

    public static int HSB2ARGB(float h, float s, float b){
        return Color.getHSBColor(h, s, b).getRGB();
    }

    public static float normalizeHue(float hue){
        while(hue <   0F) hue += 360F;
        while(hue > 360F) hue -= 360F;
        return hue / 360F;
    }
}
