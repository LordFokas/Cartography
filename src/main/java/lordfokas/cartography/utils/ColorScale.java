package lordfokas.cartography.utils;

public class ColorScale {
    protected final float low, delta;

    public ColorScale(float low, float high) {
        this.low = low;
        this.delta = high - low;
    }

    public float interpolate(float value) {
        return value * delta + low;
    }
}
