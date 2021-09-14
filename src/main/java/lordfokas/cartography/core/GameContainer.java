package lordfokas.cartography.core;

import lordfokas.cartography.core.data.AsyncDataCruncher;
import lordfokas.cartography.core.data.IThreadQueue;
import lordfokas.cartography.core.data.ThreadHandler;
import lordfokas.cartography.core.player.PlayerDataStoreManager;
import net.minecraftforge.eventbus.api.Event;

public abstract class GameContainer {
    protected final PlayerDataStoreManager dataStoreManager;
    protected final AsyncDataCruncher asyncDataCruncher;
    protected final ThreadHandler.Root threadHandler;

    protected GameContainer(String side, PlayerDataStoreManager dataStoreManager, IThreadQueue gameThreadQueue){
        this.dataStoreManager = dataStoreManager;
        this.asyncDataCruncher = new AsyncDataCruncher(side);
        this.threadHandler = new ThreadHandler.Root(asyncDataCruncher, gameThreadQueue);
    }

    public PlayerDataStoreManager getDataStoreManager(){
        return dataStoreManager;
    }

    public AsyncDataCruncher getAsyncDataCruncher(){
        return asyncDataCruncher;
    }

    public ThreadHandler.Root getThreadHandler(){
        return threadHandler;
    }

    public void destroy(){
        asyncDataCruncher.stop();
    }

    protected static abstract class LifecycleEvent<C extends GameContainer> extends Event {
        private final C container;

        protected LifecycleEvent(C container){
            this.container = container;
        }

        public C getContainer(){
            return container;
        }
    }

    protected static abstract class LoadEvent<C extends GameContainer> extends LifecycleEvent<C>{
        protected LoadEvent(C container) {
            super(container);
        }
    }

    protected static abstract class UnloadEvent<C extends GameContainer> extends LifecycleEvent<C>{
        protected UnloadEvent(C container) {
            super(container);
        }
    }
}
