package lordfokas.cartography.core.discrete;

import lordfokas.cartography.core.DataType;
import lordfokas.cartography.core.IChunkData;

public interface IDiscreteDataSource {
    DataType getDataType();
    DiscreteDatum getDatum(IChunkData chunk, int x, int y);
}
