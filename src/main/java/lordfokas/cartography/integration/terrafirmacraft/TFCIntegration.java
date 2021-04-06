package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.core.DataType;
import lordfokas.cartography.core.IMapRenderer;
import lordfokas.cartography.core.MapType;
import lordfokas.cartography.core.continuous.ColorScale;
import lordfokas.cartography.core.continuous.IContinuousDataSource;
import lordfokas.cartography.core.continuous.IsoplethMapRenderer;
import lordfokas.cartography.core.continuous.IsoplethDataCompiler;
import lordfokas.cartography.core.discrete.IDiscreteDataSource;
import lordfokas.cartography.modules.biology.Biology;
import lordfokas.cartography.modules.geology.Geology;
import lordfokas.cartography.modules.meteorology.Meteorology;
import net.minecraft.util.Tuple;

public class TFCIntegration {
    private static final ColorScale RAIN_COLOR = new ColorScale(0F, 300F);
    private static final ColorScale HEAT_COLOR = new ColorScale(300F, 0F);

    public static void loadMapTypes(){
        TerrainHeightSource   terrain   = new TerrainHeightSource(4);
        IContinuousDataSource isohyets  = new RainfallSource();
        IContinuousDataSource isotherms = new TemperatureSource();

        IDiscreteDataSource soil = new SurfaceScanner(DataType.SOIL, b -> stripOre(TFCBlockTypes.getName(b, TFCBlockTypes.Classification.SEDIMENT, TFCBlockTypes.Classification.ROCK)));
        IDiscreteDataSource rock = new SurfaceScanner(DataType.SOIL, b -> stripOre(TFCBlockTypes.getName(b, TFCBlockTypes.Type.STONE, TFCBlockTypes.Type.ORE)));

        IMapRenderer isohyetal  = new IsoplethMapRenderer(new IsoplethDataCompiler(RAIN_COLOR, HEAT_COLOR), isohyets, terrain, isotherms);
        IMapRenderer isothermal = new IsoplethMapRenderer(new IsoplethDataCompiler(HEAT_COLOR, RAIN_COLOR), isotherms, terrain, isohyets);
        IMapRenderer biogeographical = new SoilMapRenderer(soil, terrain);
        IMapRenderer geological = new RockLayerMapRenderer(rock, terrain);

        Meteorology.MAP_TYPE_REGISTRY.register(MapType.ISOHYETAL, isohyetal);
        Meteorology.MAP_TYPE_REGISTRY.register(MapType.ISOTHERMAL, isothermal);
        Biology.MAP_TYPE_REGISTRY.register(MapType.BIOGEOGRAPHICAL, biogeographical);
        Geology.MAP_TYPE_REGISTRY.register(MapType.GEOLOGICAL, geological);
    }

    private static Tuple<String, TFCBlockTypes.Type> stripOre(Tuple<String, TFCBlockTypes.Type> block){
        if(block != null && block.getB() == TFCBlockTypes.Type.ORE){
            return new Tuple<>(block.getA(), TFCBlockTypes.Type.STONE);
        }
        return block;
    }
}
