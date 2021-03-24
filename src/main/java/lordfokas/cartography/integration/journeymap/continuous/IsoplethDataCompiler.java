package lordfokas.cartography.integration.journeymap.continuous;

import lordfokas.cartography.integration.journeymap.Colors;

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

        return Colors.HSB2ARGB(h, s, b);
    }
}
