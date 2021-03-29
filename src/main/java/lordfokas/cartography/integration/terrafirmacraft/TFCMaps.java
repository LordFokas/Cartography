package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.integration.journeymap.continuous.ColorScale;
import lordfokas.cartography.integration.journeymap.continuous.IContinuousDataSource;
import lordfokas.cartography.integration.journeymap.continuous.IsoplethChunkRenderer;
import lordfokas.cartography.integration.journeymap.continuous.IsoplethDataCompiler;
import lordfokas.cartography.integration.journeymap.wrapper.CustomChunkRenderer;
import lordfokas.cartography.integration.minecraft.TerrainHeightDataSource;
import net.dries007.tfc.common.types.RockCategory;

public class TFCMaps {
    public static final CustomChunkRenderer RAINFALL, TEMPERATURE, BIOGEOGRAPHY, GEOLOGY;

    private static final ColorScale RAIN_COLOR = new ColorScale(0F, 300F);
    private static final ColorScale HEAT_COLOR = new ColorScale(300F, 0F);

    static {
        IContinuousDataSource topography = new TerrainHeightDataSource(4);
        IContinuousDataSource isohyetal  = new TFCRainfallSource();
        IContinuousDataSource isothermal = new TFCTemperatureSource();

        RAINFALL    = new IsoplethChunkRenderer(new IsoplethDataCompiler(RAIN_COLOR, HEAT_COLOR), isohyetal, topography, isothermal);
        TEMPERATURE = new IsoplethChunkRenderer(new IsoplethDataCompiler(HEAT_COLOR, RAIN_COLOR), isothermal, topography, isohyetal);

        BIOGEOGRAPHY = null;
        GEOLOGY = new RockLayerChunkRenderer(new TFCRockLayerSource(RockCategory.Layer.TOP), new TerrainHeightDataSource(8));
    }
}
