package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.core.DataType;
import lordfokas.cartography.core.mapping.IChunkData;
import lordfokas.cartography.core.mapping.discrete.DiscreteDatum;
import lordfokas.cartography.core.mapping.discrete.IDiscreteDataSource;
import lordfokas.cartography.utils.Pointer;
import net.minecraft.util.Tuple;

public class TreeSource implements IDiscreteDataSource {
    private static final TFCBlockTypes.Classification[] CLASSIFICATIONS = new TFCBlockTypes.Classification[]{
            TFCBlockTypes.Classification.TREE,
            TFCBlockTypes.Classification.SEDIMENT,
            TFCBlockTypes.Classification.ROCK
    };

    @Override
    public DataType getDataType() {
        return DataType.FOREST;
    }

    @Override
    public DiscreteDatum getDatum(IChunkData chunk, int x, int z) {
        Pointer<Tuple<String, TFCBlockTypes.Type>> pointer = new Pointer<>();

        chunk.traverseColumn(x, z, (y, state) -> {
            Tuple<String, TFCBlockTypes.Type> block = TFCBlockTypes.getName(state.getBlock(), CLASSIFICATIONS);
            if(block == null) return false;
            TFCBlockTypes.Type type = block.getB();
            if(type == TFCBlockTypes.Type.LEAVES) return false;
            if(type.classification != TFCBlockTypes.Classification.TREE) return true;
            pointer.value = block;
            return true;
        });

        if(pointer.value == null) return null;
        return new DiscreteDatum("SAPLING:"+pointer.value.getA());
    }
}
