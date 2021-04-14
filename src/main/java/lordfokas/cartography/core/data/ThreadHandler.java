package lordfokas.cartography.core.data;

import net.minecraft.client.Minecraft;

import javax.xml.ws.Holder;
import java.util.function.Function;

public final class ThreadHandler<I, O> {
    private static final IThreadQueue GAME_THREAD_QUEUE = ThreadHandler::submitGameTask;
    private static final IThreadQueue DATA_THREAD_QUEUE = AsyncDataCruncher::submitDataTask;

    public static <O> ThreadHandler<Void, O> startOnGameThread(Function<Void, O> fn){
        return new ThreadHandler<>(null, fn, GAME_THREAD_QUEUE);
    }

    public static <O> ThreadHandler<Void, O> startOnDataThread(Function<Void, O> fn){
        return new ThreadHandler<>(null, fn, DATA_THREAD_QUEUE);
    }

    public static void runOnGameThread(Runnable r){
        GAME_THREAD_QUEUE.submit(r);
    }

    public static void runOnDataThread(Runnable r){
        DATA_THREAD_QUEUE.submit(r);
    }


    @SuppressWarnings("BusyWait") // this is blocking on purpose
    public static void runOnGameThreadBlocking(Runnable task){
        AsyncDataCruncher.assertIsOnDataCruncherThread();
        Thread thread = Thread.currentThread();
        Holder<Boolean> control = new Holder<>(Boolean.FALSE);
        Holder<Throwable> error = new Holder<>();
        GAME_THREAD_QUEUE.submit(() -> {
            try{
                task.run();
            }catch(Throwable t){
                error.value = t;
            }
            control.value = Boolean.TRUE;
            thread.interrupt();
        });
        while(control.value != Boolean.TRUE){
            try{ Thread.sleep( 50); }
            catch(InterruptedException ignored){}
        }
        if(error.value != null) throw new RuntimeException("Error executing task on game thread: "+error.value.getMessage(), error.value);
    }

    public static <T> T getOnGameThreadBlocking(Function<Void, T> fn){
        Holder<T> holder = new Holder<>();
        runOnGameThreadBlocking(() -> holder.value = fn.apply(null));
        return holder.value;
    }

    private final ThreadHandler<?,?> root;
    private final Function<I, O> fn;
    private final IThreadQueue threadQueue;
    private ThreadHandler<O, ?> next;
    private boolean closed = false;

    private ThreadHandler(ThreadHandler<?,?> parent, Function<I, O> fn, IThreadQueue threadQueue){
        if(parent == null) this.root = null;
        else if(parent.root == null) this.root = parent;
        else this.root = parent.root;
        this.fn = fn;
        this.threadQueue = threadQueue;
    }

    public <N> ThreadHandler<O, N> thenOnGameThread(Function<O, N> fn){
        return thenOnThread(fn, GAME_THREAD_QUEUE);
    }

    public <N> ThreadHandler<O, N> thenOnDataThread(Function<O, N> fn){
        return thenOnThread(fn, DATA_THREAD_QUEUE);
    }

    private <N> ThreadHandler<O, N> thenOnThread(Function<O, N> fn, IThreadQueue threadQueue){
        if(closed) throw new IllegalStateException("ThreadHandler is already closed");
        closed = true;
        ThreadHandler<O, N> next = new ThreadHandler<>(this, fn, threadQueue);
        this.next = next;
        return next;
    }

    private void execute(I input){
        threadQueue.submit(() -> {
            if(next != null){
                next.execute(fn.apply(input));
            }else{
                fn.apply(input);
            }
        });
    }

    public void start(){
        if(root == null){
            this.execute(null);
        }else{
            root.execute(null);
        }
    }

    @FunctionalInterface
    private interface IThreadQueue {
        void submit(Runnable r);
    }

    private static final Minecraft MC = Minecraft.getInstance();
    private static void submitGameTask(Runnable r){
        MC.submitAsync(r);
    }
}
