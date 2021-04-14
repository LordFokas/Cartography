package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.core.mapping.continuous.ContinuousDatum;

public class TerrainDatum extends ContinuousDatum {
    public final boolean water;
    public final int depth;

    public TerrainDatum(float value, boolean boundary, boolean water, int depth) {
        super(value, boundary);
        this.water = water;
        this.depth = depth;
    }
}
