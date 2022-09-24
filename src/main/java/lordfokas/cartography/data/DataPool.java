package lordfokas.cartography.data;

import java.util.*;
import java.util.function.Supplier;

public abstract class DataPool<C, D> implements DataFlow.IDataConsumer<C, D>, DataFlow.IDataSource<C, D> {
    protected final ArrayList<DataFlow.IDataConsumer<C, D>> consumers = new ArrayList<>(4);
    protected final HashMap<C, D> pool = new HashMap<>();

    public void addConsumer(DataFlow.IDataConsumer<C, D> consumer){
        this.consumers.add(consumer);
    }

    @Override
    public void addData(C coordinate, D data){
        pool.put(coordinate, data);
        notifyConsumers(DataPool::notifyAdd, coordinate);
    }

    @Override
    public void removeData(C coordinate, D data){
        notifyConsumers(DataPool::notifyRemove, coordinate);
        pool.remove(coordinate);
    }

    @Override
    public Collection<C> keys(){
        return pool.keySet();
    }

    @Override
    public D get(C coordinate){
        return pool.get(coordinate);
    }

    public D computeIfAbsent(C coordinate, Supplier<D> supplier){
        return pool.computeIfAbsent(coordinate, $ -> supplier.get());
    }

    protected void notifyConsumers(IConsumerNotifier notifier, C coordinate){
        D data = pool.get(coordinate);
        for(DataFlow.IDataConsumer<C, D> consumer : consumers){
            notifier.notify(consumer, coordinate, data);
        }
    }

    @FunctionalInterface
    protected interface IConsumerNotifier{
        <C, D> void notify(DataFlow.IDataConsumer<C, D> consumer, C coordinate, D data);
    }

    protected static <C, D> void notifyAdd(DataFlow.IDataConsumer<C, D> consumer, C coordinate, D data){
        consumer.addData(coordinate, data);
    }

    protected static <C, D> void notifyRemove(DataFlow.IDataConsumer<C, D> consumer, C coordinate, D data){
        consumer.removeData(coordinate, data);
    }
}
