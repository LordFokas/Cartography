package lordfokas.cartography.data;

import java.util.*;
import java.util.function.Predicate;

public class DataPool<C, D> implements DataFlow.IDataConsumer<C, D>, DataFlow.IDataSource<C, D> {
    protected final ArrayList<DataFlow.IDataConsumer<C, D>> consumers = new ArrayList<>(4);
    protected final HashMap<C, D> pool = new HashMap<>();

    public void addConsumer(DataFlow.IDataConsumer<C, D> consumer) {
        this.consumers.add(consumer);
    }

    @Override
    public synchronized void addData(C coordinate, D data) {
        pool.put(coordinate, data);
        notifyConsumers(DataPool::notifyAdd, coordinate, data);
    }

    @Override
    public synchronized void setData(Map<C, D> pool) {
        this.pool.clear();
        this.pool.putAll(pool);
        for(DataFlow.IDataConsumer<C, D> consumer : consumers) {
            consumer.setData(pool);
        }
    }

    @Override
    public synchronized void removeData(C coordinate, D data) {
        data = pool.remove(coordinate);
        notifyConsumers(DataPool::notifyRemove, coordinate, data);
    }

    public synchronized void removeAll(Predicate<C> predicate) {
        for(C removed : pool.keySet().stream().filter(predicate).toList()) {
            removeData(removed, null);
        }
    }

    @Override
    public Collection<C> keys() {
        return pool.keySet();
    }

    @Override
    public D get(C coordinate) {
        return pool.get(coordinate);
    }

    protected void notifyConsumers(IConsumerNotifier notifier, C coordinate, D data) {
        for(DataFlow.IDataConsumer<C, D> consumer : consumers) {
            notifier.notify(consumer, coordinate, data);
        }
    }

    @FunctionalInterface
    protected interface IConsumerNotifier {
        <C, D> void notify(DataFlow.IDataConsumer<C, D> consumer, C coordinate, D data);
    }

    protected static <C, D> void notifyAdd(DataFlow.IDataConsumer<C, D> consumer, C coordinate, D data) {
        consumer.addData(coordinate, data);
    }

    protected static <C, D> void notifyRemove(DataFlow.IDataConsumer<C, D> consumer, C coordinate, D data) {
        consumer.removeData(coordinate, data);
    }
}
