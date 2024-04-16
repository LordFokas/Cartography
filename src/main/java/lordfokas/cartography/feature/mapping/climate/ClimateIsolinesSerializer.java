package lordfokas.cartography.feature.mapping.climate;

import java.io.IOException;

import net.minecraft.resources.ResourceLocation;

import com.eerussianguy.blazemap.api.BlazeRegistry;
import com.eerussianguy.blazemap.api.debug.MDInspectionController;
import com.eerussianguy.blazemap.api.pipeline.DataType;
import com.eerussianguy.blazemap.api.util.MinecraftStreams;
import lordfokas.cartography.CartographyReferences;

public class ClimateIsolinesSerializer implements DataType<ClimateIsolinesMD> {
    @Override
    public void serialize(MinecraftStreams.Output output, ClimateIsolinesMD md) throws IOException {
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                output.writeShort(md.rainfall[x][z]);
                output.writeShort(md.temperature[x][z]);
            }
        }
    }

    @Override
    public ClimateIsolinesMD deserialize(MinecraftStreams.Input input) throws IOException {
        int[][] rainfall = new int[16][16];
        int[][] temperature = new int[16][16];

        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                rainfall[x][z] = input.readShort();
                temperature[x][z] = input.readShort();
            }
        }

        return new ClimateIsolinesMD(rainfall, temperature);
    }

    @Override
    public BlazeRegistry.Key<?> getID() {
        return CartographyReferences.MasterData.CLIMATE_ISO;
    }

    @Override
    public MDInspectionController<ClimateIsolinesMD> getInspectionController() {
        return new MDInspectionController<>() {
            @Override
            public int getNumLines(ClimateIsolinesMD md) {
                return 0;
            }

            @Override
            public String getLine(ClimateIsolinesMD md, int line) {
                return null;
            }

            @Override
            public int getNumGrids(ClimateIsolinesMD md) {
                return 1;
            }

            @Override
            public String getGridName(ClimateIsolinesMD md, int grid) {
                return "temp isolines + rain isolines";
            }

            @Override
            public ResourceLocation getIcon(ClimateIsolinesMD md, int grid, int x, int z) {
                return null;
            }

            @Override
            public int getTint(ClimateIsolinesMD md, int grid, int x, int z) {
                int tint = 0xFF000000;
                int rainfall = md.rainfall[x][z];
                if(rainfall != ClimateIsolinesMD.NONE && rainfall % 10 == 0) {
                    tint |= 0x0000FF;
                }
                int temperature = md.temperature[x][z];
                if(temperature != ClimateIsolinesMD.NONE) {
                    tint |= 0xFF0000;
                }
                return tint;
            }

            @Override
            public String getTooltip(ClimateIsolinesMD md, int grid, int x, int z) {
                return null;
            }
        };
    }
}
