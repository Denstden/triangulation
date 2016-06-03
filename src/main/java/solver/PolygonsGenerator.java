package solver;

import components.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PolygonsGenerator {
    private int countPolygons;
    private int countVertices;

    public PolygonsGenerator(int countPolygons, int countVertices) {
        this.countPolygons = countPolygons;
        this.countVertices = countVertices;
    }

    public List<ConvexHull> generatePolygons() {
        List<Point> centers = generateRandomCenters();
        VoronoiDiagram voronoiDiagram = buildVoronoiDiagram(centers);

        List<VoronoiPoint> voronoiPoints = voronoiDiagram.getPoints();
        List<ConvexHull> hulls = new ArrayList<>();

        if (voronoiPoints.size() == 1) {
            hulls.add(generateConvexHullForOnePoint(voronoiPoints.get(0)));
        } else {
            hulls.addAll(generateConvexHulls(voronoiPoints));
        }
        return hulls;
    }

    private VoronoiDiagram buildVoronoiDiagram(List<Point> centers){
        VoronoiDiagram voronoiDiagram = null;
        boolean done = false;
        while (!done) {
            try {
                Collections.sort(centers);
                List<VoronoiPoint> voronoiPoints = centers.stream().map(VoronoiPoint::new).collect(Collectors.toList());
                voronoiDiagram = new VoronoiDiagram(voronoiPoints);
                done = true;
            } catch (Exception e) {
                centers.clear();
                centers = generateRandomCenters();
            }
        }
        return voronoiDiagram;
    }

    private ConvexHull generateConvexHullForOnePoint(VoronoiPoint point){
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double radius = random.nextDouble(0, getMaxRadius(point));
        while (radius == 0) {
            radius = random.nextDouble(0, getMaxRadius(point));
        }
        return generateConvexHull(point, radius);
    }

    private List<ConvexHull> generateConvexHulls(List<VoronoiPoint> points){
        List<ConvexHull> convexHulls = new ArrayList<>();

        Point neighbour;
        double distance;
        for (VoronoiPoint point : points) {
            neighbour = point.getNearestNeighbour();
            distance = point.distanceTo(neighbour);
            double maxRadius = getMaxRadius(point);
            double radius = Math.min(distance / 2, maxRadius);
            convexHulls.add(generateConvexHull(point, radius));
        }

        return convexHulls;
    }

    private ConvexHull generateConvexHull(VoronoiPoint center, double radius) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Double> angles = new ArrayList<>(countVertices);
        for (int i = 0; i < countVertices; ++i) {
            angles.add(random.nextDouble(0, 2 * Math.PI));
        }

        List<VoronoiPoint> points = new ArrayList<>();
        double x, y, r;
        r = radius;
        for (int i = 0; i < countVertices; ++i) {
            x = r * Math.cos(angles.get(i)) + center.x;
            y = r * Math.sin(angles.get(i)) + center.y;
            points.add(new VoronoiPoint(x, y));
        }
        Collections.sort(points);
        return new ConvexHull(points);
    }

    private ArrayList<Point> generateRandomCenters() {
        ArrayList<Point> points = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double x, y;
        for (int i = 0; i < countPolygons; i++) {
            x = random.nextDouble(0, PointsPanel.WIDTH);
            y = random.nextDouble(0, PointsPanel.HEIGHT);
            points.add(new VoronoiPoint(x, y));
        }
        return points;
    }

    private double getMaxRadius(Point point) {
        double tmp0 = Math.min(point.x, point.y);
        double tmp1 = Math.min(PointsPanel.WIDTH - point.x, PointsPanel.HEIGHT - point.y);
        return Math.min(tmp0, tmp1);
    }
}
