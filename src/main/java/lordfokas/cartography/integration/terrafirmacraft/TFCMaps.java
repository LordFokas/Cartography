package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.integration.journeymap.continuous.ColorScale;
import lordfokas.cartography.integration.journeymap.continuous.IDataSource;
import lordfokas.cartography.integration.journeymap.continuous.IsoplethChunkRenderer;
import lordfokas.cartography.integration.journeymap.continuous.IsoplethDataCompiler;
import lordfokas.cartography.integration.journeymap.wrapper.CustomChunkRenderer;
import lordfokas.cartography.integration.minecraft.TerrainHeightDataSource;

public class TFCMaps {
    public static final CustomChunkRenderer RAINFALL, TEMPERATURE, BIOGEOGRAPHY, GEOLOGY;

    private static final ColorScale RAIN_COLOR = new ColorScale(30F, 210F);
    private static final ColorScale HEAT_COLOR = new ColorScale(300F, 0F);

    static {
        IDataSource topography = new TerrainHeightDataSource();
        IDataSource isohyetal  = new TFCRainfallSource();
        IDataSource isothermal = new TFCTemperatureSource();

        RAINFALL    = new IsoplethChunkRenderer(new IsoplethDataCompiler(RAIN_COLOR, HEAT_COLOR), isohyetal, topography, isothermal);
        TEMPERATURE = new IsoplethChunkRenderer(new IsoplethDataCompiler(HEAT_COLOR, RAIN_COLOR), isothermal, topography, isohyetal);

        BIOGEOGRAPHY = null;
        GEOLOGY = null;
    }
}
