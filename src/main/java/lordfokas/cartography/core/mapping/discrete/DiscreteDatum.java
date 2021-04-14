package lordfokas.cartography.core.mapping.discrete;

public class DiscreteDatum {
    public final String value;
    public final boolean boundary;

    public DiscreteDatum(String value){
        this(value, false);
    }

    public DiscreteDatum(String value, boolean boundary){
        this.value = value;
        this.boundary = boundary;
    }
}