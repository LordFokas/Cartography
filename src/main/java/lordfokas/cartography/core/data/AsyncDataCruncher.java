package lordfokas.cartography.core.data;

import lordfokas.cartography.Cartography;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class AsyncDataCruncher {
    private static final AsyncDataCruncher INSTANCE = new AsyncDataCruncher();
    private AsyncDataCruncher(){}

    static void submitDataTask(Runnable r){
        INSTANCE.enqueue(r);
    }

    public static void start(){
        Cartography.logger().info("Starting AsyncDataCruncher Thread");
        Thread thread = INSTANCE.thread;
        thread.setName("Cartography AsyncDataCruncher");
        thread.setDaemon(true);
        thread.start();
        Cartography.logger().info("Started AsyncDataCruncher Thread");
    }

    public static void assertIsOnDataCruncherThread(){
        Thread current = Thread.currentThread();
        if(current != INSTANCE.thread){
            throw new IllegalStateException("Operation can only be performed in the AsyncDataCruncher thread");
        }
    }

    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    private final Thread thread = new Thread(this::loop);

    private void enqueue(Runnable r){
        tasks.add(r);
        thread.interrupt();
    }

    @SuppressWarnings("BusyWait") // the thread is dedicated to this loop
    private void loop(){
        while(true){
            this.work();
            try{ Thread.sleep(30_000L); }
            catch(InterruptedException ignored){}
        }
    }

    private void work(){
        while(!tasks.isEmpty()){
            Runnable task = tasks.poll();
            try{ task.run(); }
            catch(Throwable t){ t.printStackTrace(); }
        }
    }
}
