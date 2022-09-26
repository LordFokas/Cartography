package lordfokas.cartography.data;

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
}
