package lordfokas.cartography.integration.journeymap.continuous;

import lordfokas.cartography.integration.journeymap.Colors;
import lordfokas.cartography.integration.minecraft.TerrainDatum;

public class IsoplethDataCompiler implements IDataCompiler{
    protected final ColorScale[] scales;

    public IsoplethDataCompiler(ColorScale ... scales){
        this.scales = scales;
    }

    @Override
    public int compile(Datum[] data) {
        Datum datum = data[0];

        float h = Colors.normalizeHue(scales[0].interpolate(datum.value));
        float s = datum.boundary ? 1F : 0.75F;
        float b = datum.boundary ? 0.75F : 1F;

        if(data.length == 2){
            TerrainDatum terrain = (TerrainDatum) data[1];
            if(terrain.water){
                float d = ((float)Math.min(12, Math.max(0, terrain.depth-3))/12F);
                h = 0.69F;
                s = datum.boundary ? 0.65F : 1F;
                b = 1F - (d/2.5F);
            }else if(terrain.boundary && !datum.boundary){
                s = 0.4F;
                b = 0.6F;
            }
        }

        return Colors.HSB2ARGB(h, s, b);
    }
}
