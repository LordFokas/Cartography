package lordfokas.cartography.core.markers;

public interface IMarkerHandler {
    void place(Marker marker);
    void delete(String key);
}