package lordfokas.cartography.core.continuous;

import lordfokas.cartography.core.Colors;
import lordfokas.cartography.integration.minecraft.TerrainDatum;

public class IsoplethDataCompiler implements IContinuousDataCompiler {
    protected final ColorScale[] scales;

    public IsoplethDataCompiler(ColorScale ... scales){
        this.scales = scales;
    }

    @Override
    public int compile(ContinuousDatum[] data) {
        // Map main data
        ContinuousDatum main = data[0];
        float h = Colors.normalizeHue(scales[0].interpolate(main.value));
        float s = main.boundary ? 1F : 0.65F;
        float b = main.boundary ? 0.65F : 1F;

        // Map terrain data
        TerrainDatum terrain = (TerrainDatum) data[1];
        if(terrain.water){
            float d = ((float)Math.min(12, Math.max(0, terrain.depth-3))/12F);
            h = 0.69F;
            s = main.boundary ? 0.3F : 1F;
            b = main.boundary ? 0.8F : (1F - (d/2.5F));
        }else if(terrain.boundary && !main.boundary){
            s = 0.4F;
            b = 0.6F;
        }

        // Map secondary data
        ContinuousDatum secondary = data[2];
        if(!main.boundary && secondary.boundary){
            h = Colors.normalizeHue(scales[1].interpolate(1.0F));
            s = 1F;
            b = 0.65F;
        }

        return Colors.HSB2ARGB(h, s, b);
    }
}
