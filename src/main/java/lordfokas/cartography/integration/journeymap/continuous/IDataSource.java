package lordfokas.cartography.integration.journeymap.continuous;

import lordfokas.cartography.integration.journeymap.DataType;
import lordfokas.cartography.integration.journeymap.IChunkData;

public interface IDataSource{
    DataType getDataType();
    Datum getDatum(IChunkData chunk, int x, int y);
}
