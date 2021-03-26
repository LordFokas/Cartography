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

        RAINFALL = new IsoplethChunkRenderer(new IsoplethDataCompiler(RAIN_COLOR), new TFCRainfallSource(), topography);
        TEMPERATURE = null;

        BIOGEOGRAPHY = null;
        GEOLOGY = null;
    }
}
