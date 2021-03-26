package lordfokas.cartography.integration.minecraft;

import lordfokas.cartography.integration.journeymap.continuous.Datum;

public class TerrainDatum extends Datum {
    public final boolean water;
    public final int depth;

    public TerrainDatum(float value, boolean boundary, boolean water, int depth) {
        super(value, boundary);
        this.water = water;
        this.depth = depth;
    }
}
