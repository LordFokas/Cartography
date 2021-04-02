package lordfokas.cartography.core.continuous;

import lordfokas.cartography.core.IChunkData;
import lordfokas.cartography.core.IMapRenderer;

import java.awt.image.BufferedImage;

public class IsoplethChunkRenderer implements IMapRenderer {
    protected final IContinuousDataCompiler compiler;
    protected final IContinuousDataSource[] sources;

    public IsoplethChunkRenderer(IContinuousDataCompiler compiler, IContinuousDataSource... sources){
        this.compiler = compiler;
        this.sources = sources;
    }

    @Override
    public boolean render(BufferedImage image, IChunkData chunk) {
        for(int x = 0; x < 16; x++)
        for(int y = 0; y < 16; y++){
            ContinuousDatum[] data = new ContinuousDatum[sources.length];

            for(int d = 0; d < sources.length; d++){
                data[d] = sources[d].getDatum(chunk, x, y);
            }

            image.setRGB(x, y, compiler.compile(data));
        }

        return true;
    }
}
