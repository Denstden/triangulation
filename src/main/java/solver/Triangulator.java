package solver;

import additional.Triangulation;
import components.ConvexHull;
import components.Point;
import components.VoronoiPoint;
import components.avltree.AVLNode;
import exception.NoHullsException;
import javafx.util.Pair;

import java.util.*;

public class Triangulator {
    private static final double EPSILON = 0.001;
    private static final int COUNT_COORDINATES = 2;
    private List<ConvexHull> hulls;
    private long time;
    private int totalPointsNumber;
    private SimpleConverter simpleConverter = new SimpleConverter();

    public Triangulator() {
    }

    public Triangulator(List<ConvexHull> hulls) {
        this.hulls = hulls;
    }

    public List<ConvexHull> getHulls() {
        return hulls;
    }

    public void setHulls(List<ConvexHull> hulls) {
        this.hulls = hulls;
    }

    public long getTime() {
        return time;
    }

    public String getTitle(){
        return ""+time+" miliseconds";
    }

    public List<Pair<Point, Point>> triangulate() throws NoHullsException {
        if (hulls == null) {
            throw new NoHullsException();
        }
        List<VoronoiPoint> points = getAllHullsVoronoiPoints();
        Collections.sort(points);
        ConvexHull totalHull = new ConvexHull(points);

        Point firstPoint = getFirstHullPoint(totalHull.getHead());
        Pair<Point, AVLNode<VoronoiPoint>> pair = getLastHullPoint(totalHull.getHead());
        Point lastPoint = pair.getKey();
        AVLNode<VoronoiPoint> totalHullNode = pair.getValue();

        int hullMark = firstPoint.getHullMark();
        boolean[] boundaryHulls = new boolean[hulls.size()];
        List<Point> polygon = buildExternalPolygon(hullMark, boundaryHulls, firstPoint, lastPoint, totalHullNode);

        totalPointsNumber = polygon.size();
        List<List<Point>> holes = getAllHullsPoints(boundaryHulls);

        int numContours = simpleConverter.convert(holes, polygon);
        List<List<Integer>> triangles = triangulateAndFixTime(numContours, simpleConverter.contours, simpleConverter.vertices);

        return getAllEdges(triangles, simpleConverter.vertices);
    }

    private List<VoronoiPoint> getAllHullsVoronoiPoints() {
        List<VoronoiPoint> points = new ArrayList<>();
        for (int i = 0; i < hulls.size(); ++i) {
            ConvexHull hull = hulls.get(i);
            for (int j = 0; j < hull.size(); ++j)
                points.add(new VoronoiPoint(hull.get(j), i));
        }
        return points;
    }

    private Point getFirstHullPoint(AVLNode<VoronoiPoint> node){
        Point point = node.value;
        Point firstPoint = point;
        while (point.getHullMark() == firstPoint.getHullMark()) {
            firstPoint = point;
            node = node.prev;
            point = node.value;
        }
        return firstPoint;
    }

    private Pair<Point, AVLNode<VoronoiPoint>> getLastHullPoint(AVLNode<VoronoiPoint> node){
        Point point = node.value;
        Point lastPoint = point;
        while (point.getHullMark() == lastPoint.getHullMark()) {
            lastPoint = point;
            node = node.next;
            point = node.value;
        }
        return new Pair<>(lastPoint, node);
    }

    private List<Point> buildExternalPolygon(int hullMark, boolean[] boundaryHulls,
                                             Point firstPoint, Point lastPoint, AVLNode<VoronoiPoint> totalHullNode){
        List<Point> polygon = new ArrayList<>();
        while (!boundaryHulls[hullMark]) {
            boundaryHulls[hullMark] = true;
            ConvexHull hull = hulls.get(hullMark);
            AVLNode<VoronoiPoint> hullNode = hull.getHead();

            if (firstPoint.equals(lastPoint)) {
                polygon.addAll(getHullPoints(hullNode, firstPoint));
                polygon.add(getPointWithEpsilon(polygon.get(polygon.size()-1), lastPoint));
            }
            else {
                while (!hullNode.value.equals(firstPoint)) {
                    hullNode = hullNode.next;
                }
                while (!hullNode.value.equals(lastPoint)) {
                    polygon.add(hullNode.value);
                    hullNode = hullNode.prev;
                }
                polygon.add(hullNode.value);
            }

            firstPoint = totalHullNode.value;
            lastPoint = totalHullNode.value;
            hullMark = firstPoint.getHullMark();
            while (lastPoint.getHullMark() == totalHullNode.value.getHullMark()) {
                lastPoint = totalHullNode.value;
                totalHullNode = totalHullNode.next;
            }
        }
        return polygon;
    }

    private List<List<Point>> getAllHullsPoints(boolean[] boundaryHulls) {
        List<List<Point>> holes = new ArrayList<>();
        for (int i = 0; i < hulls.size(); ++i) {
            if (!boundaryHulls[i]) {
                ConvexHull h = hulls.get(i);
                List<Point> tmp = new ArrayList<>(h.size());
                for (int j = h.size() - 1; j >= 0; --j) {
                    tmp.add(h.get(j));
                }
                holes.add(tmp);
                totalPointsNumber += tmp.size();
            }
        }
        return holes;
    }

    private List<Point> getHullPoints(AVLNode<VoronoiPoint> hullNode, Point point){
        List<Point> result = new ArrayList<>();
        while (!hullNode.value.equals(point)) {
            hullNode = hullNode.next;
        }
        result.add(hullNode.value);
        hullNode = hullNode.prev;
        while (!hullNode.value.equals(point)) {
            result.add(hullNode.value);
            hullNode = hullNode.prev;
        }
        return result;
    }

    private Point getPointWithEpsilon(Point p, Point lastPoint){
        Point tmp = new Point(lastPoint.x + EPSILON, lastPoint.y);
        if (Point.rightTurn(p, lastPoint, tmp)) {
            tmp.x -= 2 * EPSILON;
            if (Point.rightTurn(p, lastPoint, tmp)) {
                tmp.x += EPSILON;
                tmp.y += EPSILON;
                if (Point.rightTurn(p, lastPoint, tmp)) {
                    tmp.y -= 2 * EPSILON;
                    if (Point.rightTurn(p, lastPoint, tmp)) {
                        System.err.println("So bad :(");
                    }
                }
            }
        }
        return tmp;
    }

    private List<List<Integer>> triangulateAndFixTime(int numContours, int[] contours, double[][] vertices){
        long begin = System.currentTimeMillis();
        List<List<Integer>> triangles = Triangulation.triangulate(numContours, contours, vertices);
        time = System.currentTimeMillis() - begin;
        return triangles;
    }

    private List<Pair<Point, Point>> getAllEdges(List<List<Integer>> triangles, double[][] vertices){
        List<Pair<Point, Point>> edges = new ArrayList<>();
        List<Integer> triangle;
        double[] xy1, xy2, xy3;
        for (int i = 0; i < triangles.size(); ++i){
            triangle = triangles.get(i);
            xy1 = vertices[triangle.get(0)];
            xy2 = vertices[triangle.get(1)];
            xy3 = vertices[triangle.get(2)];
            edges.addAll(getTriangleEdges(xy1, xy2, xy3));
        }
        return edges;
    }

    private List<Pair<Point, Point>> getTriangleEdges(double[] xy1, double[] xy2, double[] xy3){
        List<Pair<Point, Point>> edges = new ArrayList<>();
        Point p1 = new Point(xy1[0], xy1[1]);
        Point p2 = new Point(xy2[0], xy2[1]);
        Point p3 = new Point(xy3[0], xy3[1]);
        edges.add(new Pair<>(p1, p2));
        edges.add(new Pair<>(p2, p3));
        edges.add(new Pair<>(p1, p3));
        return edges;
    }

    private class SimpleConverter{
        private double[][] vertices;
        private int[] contours;

        public int convert(List<List<Point>> holes, List<Point> polygon){
            vertices = new double[totalPointsNumber][COUNT_COORDINATES];
            int numContours = holes.size() + 1;
            contours = new int[numContours];
            int vI = 0, cI = 0;
            contours[cI] = polygon.size();
            ++cI;
            for (int i = 0; i < polygon.size(); ++i) {
                Point p = polygon.get(i);
                vertices[vI][0] = p.x;
                vertices[vI][1] = p.y;
                ++vI;
            }

            for (int i = 0; i < holes.size(); ++i) {
                List<Point> list = holes.get(i);
                contours[cI] = list.size();
                for (int j = 0; j < list.size(); ++j) {
                    Point p = list.get(j);
                    vertices[vI][0] = p.x;
                    vertices[vI][1] = p.y;
                    ++vI;
                }
                ++cI;
            }
            return numContours;
        }
    }
}
