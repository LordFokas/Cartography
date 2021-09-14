package lordfokas.cartography.core.data;

@FunctionalInterface
public interface IThreadQueue {
    void submit(Runnable r);
}
