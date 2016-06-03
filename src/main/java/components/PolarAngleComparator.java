package components;

import java.util.Comparator;

public class PolarAngleComparator implements Comparator<VoronoiPoint> {
    VoronoiPoint startPoint;
    double direction;
    boolean alwaysEqual = true;

    public PolarAngleComparator() { }

    public PolarAngleComparator(VoronoiPoint point) {
        if (point == null) {
            throw new NullPointerException();
        }

        startPoint = point;
        alwaysEqual = false;
    }

    public PolarAngleComparator(VoronoiPoint point, VoronoiPoint dir) {
        if (point == null || dir == null) {
            throw new NullPointerException();
        }

        startPoint = point;
        direction = Point.polarAngle(startPoint, dir);
        alwaysEqual = false;
    }

    @Override
    public int compare(VoronoiPoint p1, VoronoiPoint p2) {
        if (alwaysEqual) {
            return 0;
        }

        if (p1 == null || p2 == null || startPoint == null) {
            throw new NullPointerException();
        }

        if (p1.equals(p2)) {
            return 0;
        }

        if (p1.equals(startPoint)) {
            return -1;
        }

        if (p2.equals(startPoint)) {
            return 1;
        }

        double angle1 = Point.polarAngle(startPoint,p1);
        double angle2 = Point.polarAngle(startPoint,p2);

        if (angle1 >= direction) angle1 -= 2*Math.PI;
        if (angle2 >= direction) angle2 -= 2*Math.PI;

        if (angle1 == angle2) {
            double d1 = (p1).distanceTo(startPoint);
            double d2 = (p2).distanceTo(startPoint);

            return Double.compare(d1, d2);
        }

        return Double.compare(angle1, angle2);
    }
}
