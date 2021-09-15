package lordfokas.cartography.integration.terrafirmacraft;

import lordfokas.cartography.core.DataType;
import lordfokas.cartography.core.mapping.IChunkData;
import lordfokas.cartography.core.mapping.discrete.DiscreteDatum;
import lordfokas.cartography.core.mapping.discrete.IDiscreteDataSource;
import lordfokas.cartography.utils.Pointer;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class SurfaceScanner implements IDiscreteDataSource {
    private final DataType dataType;
    private final Function<Block, Tuple<String, TFCBlockTypes.Type>> classifier;

    public SurfaceScanner(DataType dataType, Function<Block, Tuple<String, TFCBlockTypes.Type>> classifier){
        this.dataType = dataType;
        this.classifier = classifier;
    }

    @Override
    public final DataType getDataType() {
        return dataType;
    }

    @Override
    public DiscreteDatum getDatum(IChunkData chunk, int x, int y) {
        String value = scan(chunk, x, y);

        String a = scan(chunk, x+1, y);
        String b = scan(chunk, x-1, y);
        String c = scan(chunk, x, y+1);
        String d = scan(chunk, x, y-1);
        boolean boundary = !( value.equals(a) && value.equals(b) && value.equals(c) && value.equals(d) );

        return new DiscreteDatum(value, boundary);
    }

    @Nonnull
    private String scan(IChunkData c, int x, int z){
        Pointer<String> id = new Pointer<>("");
        c.traverseColumn(x, z, (y, state) -> {
            Tuple<String, TFCBlockTypes.Type> res = classifier.apply(state.getBlock());
            if(res != null){
                id.value = res.getB().name() + ":" + res.getA();
                return true;
            }
            return false;
        });
        return id.value;
    }
}
