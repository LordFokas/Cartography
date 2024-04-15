package lordfokas.cartography.feature.discovery;

import java.util.Collection;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;

import lordfokas.cartography.data.Cluster;

public class DiscoveryCluster extends Cluster<BlockPos, DiscoveryState> {
    public final String type;

    public DiscoveryCluster(Collection<BlockPos> coordinates, Collection<DiscoveryState> members, String type, Consumer<DiscoveryCluster> notifier) {
        super(coordinates, new DiscoveryStateCluster(members));
        this.type = type;
        ((DiscoveryStateCluster)data).triggerNotify = () -> notifier.accept(this);
    }

    public BlockPos centerOfMass() {
        int size = getCoordinates().size();
        if(size == 1) {
            return getCoordinates().iterator().next();
        }
        long x = 0, z = 0;
        for(BlockPos pos : getCoordinates()) {
            x += pos.getX();
            z += pos.getZ();
        }
        x /= size;
        z /= size;
        return new BlockPos(x, 0, z);
    }

    public Collection<DiscoveryState> getMembers() {
        return ((DiscoveryStateCluster)data).members;
    }

    private static class DiscoveryStateCluster extends DiscoveryState {
        private final Collection<DiscoveryState> members;
        private Runnable triggerNotify = () -> {};

        public DiscoveryStateCluster(Collection<DiscoveryState> members) {
            super(false);
            this.members = members;

            boolean depleted = false;
            for(DiscoveryState state : members) {
                if(state.isDepleted()) {
                    depleted = true;
                    break;
                }
            }
            setDepleted(depleted, false);
        }

        @Override
        public boolean isDepleted() {
            return members.stream().findFirst().get().isDepleted();
        }

        @Override
        public void setDepleted(boolean depleted) {
            setDepleted(depleted, true);
        }

        private void setDepleted(boolean depleted, boolean notify) {
            for(DiscoveryState state : members) {
                state.setDepleted(depleted);
            }
            if(notify) {
                triggerNotify.run();
            }
        }
    }
}
