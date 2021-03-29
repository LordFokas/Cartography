package lordfokas.cartography.integration.journeymap.continuous;

public class ContinuousDatum {
    public final float value;
    public final boolean boundary;

    public ContinuousDatum(float value){
        this(value, false);
    }

    public ContinuousDatum(float value, boolean boundary){
        this.value = value;
        this.boundary = boundary;
    }
}
