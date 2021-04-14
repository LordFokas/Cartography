package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.core.DataType;
import lordfokas.cartography.core.mapping.IChunkData;
import lordfokas.cartography.core.mapping.discrete.DiscreteDatum;
import lordfokas.cartography.core.mapping.discrete.IDiscreteDataSource;
import net.minecraft.util.Tuple;

import javax.xml.ws.Holder;

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
        Holder<Tuple<String, TFCBlockTypes.Type>> holder = new Holder<>();

        chunk.traverseColumn(x, z, (y, state) -> {
            Tuple<String, TFCBlockTypes.Type> block = TFCBlockTypes.getName(state.getBlock(), CLASSIFICATIONS);
            if(block == null) return false;
            TFCBlockTypes.Type type = block.getB();
            if(type == TFCBlockTypes.Type.LEAVES) return false;
            if(type.classification != TFCBlockTypes.Classification.TREE) return true;
            holder.value = block;
            return true;
        });

        if(holder.value == null) return null;
        return new DiscreteDatum("SAPLING:"+holder.value.getA());
    }
}
