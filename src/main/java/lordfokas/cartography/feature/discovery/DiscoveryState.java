package lordfokas.cartography.feature.discovery;

public class DiscoveryState {
    private boolean depleted;

    public DiscoveryState(boolean depleted) {
        this.depleted = depleted;
    }

    public boolean isDepleted() {
        return depleted;
    }

    public void setDepleted(boolean depleted) {
        this.depleted = depleted;
    }
}
