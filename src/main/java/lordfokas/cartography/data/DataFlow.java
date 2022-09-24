package lordfokas.cartography.data;

import java.io.*;
import java.util.*;

public class DataFlow {
    public interface IDataSource<C, D> {
        Collection<C> keys();
        D get(C coordinate);
    }

    public interface IDataSink<C, D> {
        void setData(Map<C, D> pool);
    }

    public interface IDataConsumer<C, D> extends IDataSink<C, D> {
        void addData(C coordinate, D data);
        void removeData(C coordinate, D data);
    }

    public interface IDataSerializer<C, D> {
        void deserialize(DataInputStream stream, IDataSink<C, D> sink) throws IOException;
        void serialize(DataOutputStream stream, IDataSource<C, D> source) throws IOException;
    }
}
