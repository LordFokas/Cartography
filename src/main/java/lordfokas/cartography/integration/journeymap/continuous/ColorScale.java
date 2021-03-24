package lordfokas.cartography.integration.journeymap.continuous;

public class ColorScale {
    protected final float low, high, delta;

    public ColorScale(float low, float high){
        this.low = low;
        this.high = high;
        this.delta = high - low;
    }

    public float interpolate(float value){
        return value * delta + low;
    }
}
