package lordfokas.cartography.core;

import lordfokas.cartography.core.data.IThreadQueue;
import lordfokas.cartography.core.player.PlayerDataStoreManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;

public class GameContainerClient extends GameContainer {
    private static GameContainerClient instance;

    public static GameContainerClient instance(){
        return instance;
    }

    private GameContainerClient(File file, IThreadQueue gameThreadQueue){
        super("Client", new PlayerDataStoreManager(file), gameThreadQueue);
        MinecraftForge.EVENT_BUS.post(new LoadEvent(this));
    }

    @Override
    public void destroy(){
        super.destroy();
        MinecraftForge.EVENT_BUS.post(new UnloadEvent(this));
    }

    public static class EventHandler {
        @SubscribeEvent
        public void onClientJoining(final ClientPlayerNetworkEvent.LoggedInEvent evt){
            Minecraft client = Minecraft.getInstance();
            instance = new GameContainerClient(new File(client.gameDirectory, "rave"), client::submit);
        }

        @SubscribeEvent
        public void onClientLeaving(final ClientPlayerNetworkEvent.LoggedOutEvent evt){
            if(instance == null) return;
            instance.destroy();
            instance = null;
        }
    }

    public static final class LoadEvent extends GameContainer.LoadEvent<GameContainerClient> {
        protected LoadEvent(GameContainerClient container) {
            super(container);
        }
    }

    public static final class UnloadEvent extends GameContainer.UnloadEvent<GameContainerClient> {
        protected UnloadEvent(GameContainerClient container) {
            super(container);
        }
    }
}
