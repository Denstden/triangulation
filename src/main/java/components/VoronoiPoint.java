package components;

public class VoronoiPoint extends Point {

    VoronoiEdge firstEdge;

    public VoronoiPoint(double x, double y) {
        super(x, y);
    }

    public VoronoiPoint(Point point) {
        super(point.x, point.y, point.color);
    }

    public VoronoiPoint(Point point, int hullMark) {
        super(point, hullMark);
    }

    public VoronoiPoint(double x, double y, int hullMark) {
        super(x, y, hullMark);
    }
}
