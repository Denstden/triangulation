package components;

public class VoronoiEdge {

    Point beginVertex;
    Point endVertex;
    VoronoiPoint leftSide;
    VoronoiPoint rightSide;

    VoronoiEdge clockwise;
    VoronoiEdge anticlockwise;

    VoronoiEdge reverse;

    public VoronoiEdge(Point beginVertex, Point endVertex, VoronoiPoint leftSide, VoronoiPoint rightSide) {
        this.beginVertex = beginVertex;
        this.endVertex = endVertex;
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        this.reverse = new VoronoiEdge(endVertex, beginVertex, rightSide, leftSide, this);
    }

    public VoronoiEdge(Point beginVertex, Point endVertex, VoronoiPoint leftSide, VoronoiPoint rightSide, VoronoiEdge reverse) {
        this.beginVertex = beginVertex;
        this.endVertex = endVertex;
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        this.reverse = reverse;
    }

    static VoronoiEdge getPerpendicularEdge(VoronoiPoint p1, VoronoiPoint p2) {
        Line line = new Line(p1, p2, true);
        Point middle = new VoronoiPoint((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
        double max = PointsPanel.MAX;
        Point begin = new Point(-max, -max), end = new Point(max, max);
        findNewPoint(line, PointsPanel.up, middle, begin, end);
        findNewPoint(line, PointsPanel.down, middle, begin, end);
        findNewPoint(line, PointsPanel.left, middle, begin, end);
        findNewPoint(line, PointsPanel.right, middle, begin, end);

        if (Point.polarAngle(begin, p1) > Point.polarAngle(begin, end))
            return new VoronoiEdge(begin, end, p1, p2);
        else return new VoronoiEdge(begin, end, p2, p1);
    }

    private static void findNewPoint(Line line1, Line line2, Point middle, Point left, Point right) {
        Point point = line1.intersection(line2);
        if (point != null) {
            double max = PointsPanel.MAX;
            if (point.x > max) {
                point.x = max;
                point.y = line1.findY(point.x);
            }
            if (point.y > max) {
                point.y = max;
                point.x = line1.findX(point.y);
            }
            if (point.x < -max) {
                point.x = -max;
                point.y = line1.findY(point.x);
            }
            if (point.y < -max) {
                point.y = -max;
                point.x = line1.findX(point.y);
            }
            if (point.x < middle.x || point.x == middle.x && point.y < middle.y) {
                if (middle.distanceTo(left) > middle.distanceTo(point)) {
                    left.x = point.x;
                    left.y = point.y;
                }
            } else {
                if (middle.distanceTo(right) > middle.distanceTo(point)) {
                    right.x = point.x;
                    right.y = point.y;
                }
            }
        }
    }

    @Override
    public boolean equals(Object edge) {
        return edge instanceof VoronoiEdge && ((VoronoiEdge) edge).beginVertex.equals(beginVertex) && ((VoronoiEdge) edge).endVertex.equals(endVertex);
    }
}
