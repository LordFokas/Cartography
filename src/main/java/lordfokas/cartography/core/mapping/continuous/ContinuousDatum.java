package lordfokas.cartography.core.mapping.continuous;

public class ContinuousDatum {
    public final float value;
    public final boolean boundary;
    public final int absolute;

    public ContinuousDatum(float value){
        this(value, false, 0);
    }

    public ContinuousDatum(float value, boolean boundary){
        this(value, boundary, 0);
    }

    public ContinuousDatum(float value, boolean boundary, int absolute){
        this.value = value;
        this.boundary = boundary;
        this.absolute = absolute;
    }
}
