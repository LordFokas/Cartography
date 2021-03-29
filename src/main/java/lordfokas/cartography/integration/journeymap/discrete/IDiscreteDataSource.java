package lordfokas.cartography.integration.journeymap.discrete;

import lordfokas.cartography.integration.journeymap.DataType;
import lordfokas.cartography.integration.journeymap.IChunkData;

public interface IDiscreteDataSource {
    DataType getDataType();
    DiscreteDatum getDatum(IChunkData chunk, int x, int y);
}
