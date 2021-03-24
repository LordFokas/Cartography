package lordfokas.cartography.integration.journeymap.continuous;

public class Datum {
    public final float value;
    public final boolean boundary;

    public Datum(float value){
        this(value, false);
    }

    public Datum(float value, boolean boundary){
        this.value = value;
        this.boundary = boundary;
    }
}
