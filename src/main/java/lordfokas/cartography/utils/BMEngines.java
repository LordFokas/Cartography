package lordfokas.cartography.utils;

import java.util.function.Supplier;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import com.eerussianguy.blazemap.engine.async.AsyncChainRoot;
import com.eerussianguy.blazemap.engine.async.AsyncDataCruncher;
import com.eerussianguy.blazemap.engine.async.DebouncingThread;
import com.eerussianguy.blazemap.engine.client.BlazeMapClientEngine;
import com.eerussianguy.blazemap.engine.server.BlazeMapServerEngine;

public class BMEngines {
    private static final Supplier<AsyncChainRoot> ASYNC_CHAIN;
    private static final Supplier<AsyncDataCruncher> ASYNC_DATA_CRUNCHER;
    private static final Supplier<DebouncingThread> DEBOUNCER;

    public static AsyncChainRoot async() {
        return ASYNC_CHAIN.get();
    }

    public static AsyncDataCruncher cruncher() {
        return ASYNC_DATA_CRUNCHER.get();
    }

    public static DebouncingThread debouncer() {
        return DEBOUNCER.get();
    }

    static {
        if(FMLEnvironment.dist == Dist.CLIENT) {
            ASYNC_CHAIN = BlazeMapClientEngine::async;
            ASYNC_DATA_CRUNCHER = BlazeMapClientEngine::cruncher;
            DEBOUNCER = BlazeMapClientEngine::debouncer;
        }
        else {
            ASYNC_CHAIN = BlazeMapServerEngine::async;
            ASYNC_DATA_CRUNCHER = BlazeMapServerEngine::cruncher;
            DEBOUNCER = BlazeMapServerEngine::debouncer;
        }
    }
}
