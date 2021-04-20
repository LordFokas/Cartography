package lordfokas.cartography.modules.geology;

import lordfokas.cartography.core.ImageHandler;
import lordfokas.cartography.core.MapType;
import lordfokas.cartography.core.data.ClusterRealm;
import lordfokas.cartography.core.data.ThreadHandler;
import lordfokas.cartography.core.markers.IMarkerHandler;
import lordfokas.cartography.core.markers.Marker;
import lordfokas.cartography.integration.journeymap.JMPlugin;
import lordfokas.cartography.integration.terrafirmacraft.TFCBlockTypes;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

public class RockClusterRealm extends ClusterRealm<ChunkPos, String, String, RockCluster> {
    private final ResourceLocation icon;
    private final RegistryKey<World> dim;
    private final String rock;

    public RockClusterRealm(RegistryKey<World> dim, String rock){
        this.rock = rock.replace("STONE:", "");
        this.dim = dim;
        this.icon = TFCBlockTypes.getLooseRockTexturePath(this.rock);
    }

    @Override
    protected boolean isInRange(ChunkPos coordinate, Collection<ChunkPos> cluster) {
        for(ChunkPos target : cluster){
            if(coordinate.getChessboardDistance(target) <= 1){
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean isIncluded(ChunkPos coordinate, Collection<ChunkPos> cluster) {
        for(ChunkPos target : cluster){
            if(target.equals(coordinate)){
                return true;
            }
        }
        return false;
    }

    @Override
    protected RockCluster merge(RockCluster added, Iterable<RockCluster> existing) {
        ArrayList<ChunkPos> coordinates = new ArrayList<>(added.getCoordinates());
        for(RockCluster cluster : existing){
            coordinates.addAll(cluster.getCoordinates());
        }
        return new RockCluster(coordinates, added.getData(), new ArrayList<>(16));
    }

    @Override
    protected RockCluster split(RockCluster removed, RockCluster existing) {
        ArrayList<ChunkPos> coordinates = new ArrayList<>(existing.getCoordinates());
        coordinates.removeAll(removed.getCoordinates());
        return new RockCluster(coordinates, existing.getData(), new ArrayList<>(16));
    }

    @Override
    protected void undeploy(RockCluster cluster) {
        IMarkerHandler markers = JMPlugin.instance();
        ThreadHandler.runOnGameThreadBlocking(() -> {
            for(String key : cluster.getKeys()){
                markers.delete(key);
            }
        });
        cluster.getKeys().clear();
    }

    @Override
    protected void deploy(RockCluster cluster) {
        if(cluster.getCoordinates().size() < 10) return;

        int SCALE = 4;
        BlockPos center = calculateCenterOfMass(cluster);
        BufferedImage image = ThreadHandler.getOnGameThreadBlocking($ -> ImageHandler.getLabel(rock, icon, SCALE));

        String marker_key = "RockCluster_"+rock+"_"+center.getX()+"_"+center.getZ();
        Marker marker = new Marker(marker_key, dim, center.getX(), center.getZ(), image, SCALE, 1, MapType.GEOLOGICAL);
        cluster.getKeys().add(marker_key);

        ThreadHandler.runOnGameThreadBlocking(() -> JMPlugin.instance().place(marker));
    }

    @Override
    protected String summarize(Collection<ChunkPos> cluster) {
        return rock;
    }

    @Override
    protected RockCluster make(ChunkPos coordinate, String data) {
        Collection<ChunkPos> coordinates = new ArrayList<>(1);
        coordinates.add(coordinate);
        return new RockCluster(coordinates, data, new ArrayList<>(16));
    }

    private BlockPos calculateCenterOfMass(RockCluster cluster){
        int sx = 0, sz = 0, c = 0;
        for(ChunkPos pos : cluster.getCoordinates()){
            int x = pos.x << 4;
            int z = pos.z << 4;
            sx += x+8;
            sz += z+8;
            c++;
        }
        return new BlockPos(sx/c, 0, sz/c);
    }
}
