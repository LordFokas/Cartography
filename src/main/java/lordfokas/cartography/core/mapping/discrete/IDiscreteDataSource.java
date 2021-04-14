package lordfokas.cartography.core.mapping.discrete;

import lordfokas.cartography.core.DataType;
import lordfokas.cartography.core.mapping.IChunkData;

public interface IDiscreteDataSource {
    DataType getDataType();
    DiscreteDatum getDatum(IChunkData chunk, int x, int y);
}
