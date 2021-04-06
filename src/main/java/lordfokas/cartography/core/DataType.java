package lordfokas.cartography.core;

public enum DataType {
    TERRAIN_HEIGHT("m", 8),
    TEMPERATURE("*C", 1),
    RAINFALL("mm", 5),
    SOIL,
    ROCKLAYER;

    public final String unit;
    public final int interval;

    DataType(){ this("", 0); }
    DataType(String unit, int interval){
        this.unit = unit;
        this.interval = interval;
    }
}
