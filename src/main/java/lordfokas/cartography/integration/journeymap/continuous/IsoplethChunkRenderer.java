package lordfokas.cartography.integration.journeymap.continuous;

import journeymap.client.render.ComparableBufferedImage;
import lordfokas.cartography.integration.journeymap.IChunkData;
import lordfokas.cartography.integration.journeymap.wrapper.CustomChunkRenderer;

public class IsoplethChunkRenderer extends CustomChunkRenderer {
    protected final IDataCompiler compiler;
    protected final IDataSource[] sources;

    public IsoplethChunkRenderer(IDataCompiler compiler, IDataSource ... sources){
        this.compiler = compiler;
        this.sources = sources;
    }

    @Override
    public boolean render(ComparableBufferedImage image, IChunkData chunk) {
        for(int x = 0; x < 16; x++)
        for(int y = 0; y < 16; y++){
            Datum[] data = new Datum[sources.length];

            for(int d = 0; d < sources.length; d++){
                data[d] = sources[d].getDatum(chunk, x, y);
            }

            image.setRGB(x, y, compiler.compile(data));
        }

        return true;
    }
}
