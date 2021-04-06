package lordfokas.cartography.core.continuous;

import lordfokas.cartography.core.*;
import lordfokas.cartography.core.markers.IMarkerPlacer;
import net.minecraft.util.math.ChunkPos;

import java.awt.image.BufferedImage;

public class IsoplethMapRenderer implements IMapRenderer {
    protected final IContinuousDataCompiler compiler;
    protected final IContinuousDataSource[] sources;

    public IsoplethMapRenderer(IContinuousDataCompiler compiler, IContinuousDataSource... sources){
        this.compiler = compiler;
        this.sources = sources;
    }

    @Override
    public boolean render(BufferedImage image, IChunkData chunk, IMarkerPlacer labels) {
        DataType type = sources[0].getDataType();
        int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
        boolean boundary = false;
        int value = 0, lx=0, lz=0, count = 0;
        float normal = 0;

        for(int x = 0; x < 16; x++)
        for(int y = 0; y < 16; y++){
            ContinuousDatum[] data = new ContinuousDatum[sources.length];

            for(int d = 0; d < sources.length; d++){
                data[d] = sources[d].getDatum(chunk, x, y);
            }

            image.setRGB(x, y, compiler.compile(data));

            if(!boundary && data[0].boundary){
                boundary = true;
                normal = data[0].value;
                value = data[0].absolute;
                lx = x1 = x;
                lz = y1 = y;
            }
            if(data[0].boundary){
                count++;
                x2 = x;
                y2 = y;
            }
        }

        if(boundary && (value % type.interval == 0) && count > 1){
            ChunkPos ck = chunk.getChunk(lx, lz).getPos();
            float hue = Colors.normalizeHue(((IsoplethDataCompiler)compiler).getMaster().interpolate(normal));
            int color = Colors.HSB2ARGB(hue, 1, 1);
            int angle = (x1==x2) ? 90 : (int) Math.round(Math.toDegrees(Math.atan(((double)y2-y1)/((double)x2-x1))));
            labels.place(chunk.getDimension(), ck.getMinBlockX()+lx, ck.getMinBlockZ()+lz, value+type.unit, color, type, value, angle, MapType.ISOHYETAL, MapType.ISOTHERMAL);
        }

        return true;
    }
}
