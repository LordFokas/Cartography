package lordfokas.cartography.core;

import lordfokas.cartography.core.data.IThreadQueue;
import lordfokas.cartography.core.player.PlayerDataStoreManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

import java.io.File;

public class GameContainerServer extends GameContainer {
    private static GameContainerServer instance;

    public static GameContainerServer instance(){
        return instance;
    }

    private GameContainerServer(File file, IThreadQueue gameThreadQueue){
        super("Server", new PlayerDataStoreManager(file), gameThreadQueue);
        MinecraftForge.EVENT_BUS.post(new LoadEvent(this));
    }

    @Override
    public void destroy(){
        super.destroy();
        MinecraftForge.EVENT_BUS.post(new UnloadEvent(this));
    }

    public static class EventHandler {

        @SubscribeEvent
        public void onServerStarting(FMLServerStartingEvent evt){
            MinecraftServer server = evt.getServer();
            File dir = server.getServerDirectory();
            instance = new GameContainerServer(new File(dir, "rave"), server::submit);
        }

        @SubscribeEvent
        public void onServerStopped(FMLServerStoppedEvent evt){
            instance.destroy();
            instance = null;
        }
    }

    public static final class LoadEvent extends GameContainer.LoadEvent<GameContainerServer> {
        protected LoadEvent(GameContainerServer container) {
            super(container);
        }
    }

    public static final class UnloadEvent extends GameContainer.UnloadEvent<GameContainerServer> {
        protected UnloadEvent(GameContainerServer container) {
            super(container);
        }
    }
}
