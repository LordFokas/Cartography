package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.core.DataType;
import lordfokas.cartography.core.IChunkData;
import lordfokas.cartography.core.discrete.DiscreteDatum;
import lordfokas.cartography.core.discrete.IDiscreteDataSource;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.types.Ore;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.RockCategory;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;
import javax.xml.ws.Holder;
import java.util.HashMap;
import java.util.Map;

public class TFCRockLayerSource implements IDiscreteDataSource {
    private final RockCategory.Layer layer;

    public TFCRockLayerSource(RockCategory.Layer layer){
        if(layer == null) throw new NullPointerException("Layer cannot be null");
        this.layer = layer;
    }

    @Override
    public DataType getDataType() {
        return DataType.RAINFALL;
    }

    @Override
    public DiscreteDatum getDatum(IChunkData chunk, int x, int y) {
        String value = rock(chunk, x, y);

        String a = rock(chunk, x+1, y);
        String b = rock(chunk, x-1, y);
        String c = rock(chunk, x, y+1);
        String d = rock(chunk, x, y-1);
        boolean boundary = !( value.equals(a) && value.equals(b) && value.equals(c) && value.equals(d) );

        return new DiscreteDatum(value, boundary);
    }

    @Nonnull
    private String rock(IChunkData c, int x, int z){
        Holder<String> id = new Holder<>("");
        c.traverseColumn(x, z, (y, state) -> {
            Block block = state.getBlock();
            String rock = RockTypes.getRock(block);
            if(rock == null) return true;
            id.value = rock;
            return false;
        });
        return id.value;
    }

    private static class RockTypes {
        private static final Rock.BlockType[] TYPES = new Rock.BlockType[]{ Rock.BlockType.RAW, Rock.BlockType.HARDENED};
        private static final Map<Block, String> ROCKS = new HashMap<>();

        public static String getRock(Block block){
            return ROCKS.get(block);
        }

        static {
            for(Rock.Default rock : Rock.Default.values()){
                String name = rock.name().toLowerCase();
                for(Rock.BlockType type : TYPES){
                    ROCKS.put(TFCBlocks.ROCK_BLOCKS.get(rock).get(type).get(), name);
                }
                for(Map<Ore.Grade, RegistryObject<Block>> ores : TFCBlocks.GRADED_ORES.get(rock).values()){
                    for(RegistryObject<Block> obj : ores.values()){
                        ROCKS.put(obj.get(), name);
                    }
                }
            }
        }
    }
}
