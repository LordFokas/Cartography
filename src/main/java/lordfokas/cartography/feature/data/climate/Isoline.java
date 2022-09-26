package lordfokas.cartography.feature.data.climate;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.level.ChunkPos;

public class Isoline {
    final Map<ChunkPos, Curve> curves;
    public final String value;
    public final String unit;

    public static Isoline of(ChunkPos pos, String value, String unit, float angle, int mx, int my) {
        Isoline.Curve curve = new Isoline.Curve(pos, angle, mx, my);
        Map<ChunkPos, Isoline.Curve> data = new HashMap<>();
        data.put(pos, curve);
        return new Isoline(data, value, unit);
    }

    public Isoline(Map<ChunkPos, Curve> curves, String value, String unit) {
        this.curves = curves;
        this.value = value;
        this.unit = unit;
    }

    public static class Curve {
        public final ChunkPos chunk;
        public final float angle;
        public final int mx, my;

        public Curve(ChunkPos chunk, float angle, int mx, int my) {
            this.chunk = chunk;
            this.angle = angle;
            this.mx = mx;
            this.my = my;
        }
    }

    public Isoline copy() {
        return new Isoline(new HashMap<>(curves), value, unit);
    }
}
