package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.core.IMapRenderer;
import lordfokas.cartography.core.MapType;
import lordfokas.cartography.core.continuous.ColorScale;
import lordfokas.cartography.core.continuous.IContinuousDataSource;
import lordfokas.cartography.core.continuous.IsoplethChunkRenderer;
import lordfokas.cartography.core.continuous.IsoplethDataCompiler;
import lordfokas.cartography.integration.minecraft.TerrainHeightDataSource;
import lordfokas.cartography.modules.geology.Geology;
import lordfokas.cartography.modules.meteorology.Meteorology;
import net.dries007.tfc.common.types.RockCategory;

public class TFCIntegration {
    private static final ColorScale RAIN_COLOR = new ColorScale(0F, 300F);
    private static final ColorScale HEAT_COLOR = new ColorScale(300F, 0F);

    public static void loadMapTypes(){
        IContinuousDataSource terrain   = new TerrainHeightDataSource(4);
        IContinuousDataSource isohyets  = new TFCRainfallSource();
        IContinuousDataSource isotherms = new TFCTemperatureSource();

        IMapRenderer isohyetal  = new IsoplethChunkRenderer(new IsoplethDataCompiler(RAIN_COLOR, HEAT_COLOR), isohyets, terrain, isotherms);
        IMapRenderer isothermal = new IsoplethChunkRenderer(new IsoplethDataCompiler(HEAT_COLOR, RAIN_COLOR), isotherms, terrain, isohyets);
        // IMapRenderer biogeographical = null;
        IMapRenderer geological = new RockLayerChunkRenderer(new TFCRockLayerSource(RockCategory.Layer.TOP), new TerrainHeightDataSource(8));

        Meteorology.MAP_TYPE_REGISTRY.register(MapType.ISOHYETAL, isohyetal);
        Meteorology.MAP_TYPE_REGISTRY.register(MapType.ISOTHERMAL, isothermal);

        // Biology.MAP_TYPE_REGISTRY.register(MapType.BIOGEOGRAPHICAL, biogeography);

        Geology.MAP_TYPE_REGISTRY.register(MapType.GEOLOGICAL, geological);
    }
}
