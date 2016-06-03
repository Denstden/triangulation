package components;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PointsPanel extends JPanel {

    public static final int WIDTH = 1350, HEIGHT = 700;

    public static double MAX = 100000000;
    public static final Line up = new Line(-MAX, MAX, MAX, MAX);
    public static final Line down = new Line(-MAX, -MAX, MAX, -MAX);
    public static final Line left = new Line(-MAX, -MAX, -MAX, MAX);
    public static final Line right = new Line(MAX, -MAX, MAX, MAX);

    private List<Point> points;

    private VoronoiDiagram diagram;
    private List<ConvexHull> convexHulls;
    private List<Pair<Point, Point>> edges;

    public static boolean printNeighbours = true;
    public static boolean printConvexHull = true;
    public static boolean printDiagram = true;


    public PointsPanel(List<Point> points) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.points = points;
    }

    public PointsPanel(VoronoiDiagram diagram) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.diagram = diagram;
    }

    public PointsPanel(List<Point> points, VoronoiDiagram diagram) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.points = points;
        this.diagram = diagram;
    }

    public PointsPanel(List<Point> points, List<ConvexHull> convexHulls) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.points = points;
        this.convexHulls = convexHulls;
    }

    public PointsPanel(List<Point> points, List<Pair<Point, Point>> edges, List<ConvexHull> convexHulls) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.points = points;
        this.edges = edges;
        this.convexHulls = convexHulls;
    }

    public void paintComponent(Graphics page) {
        super.paintComponent(page);
        if (points != null) {
            for (Point point : points)
                point.draw(page);
        }

        if (diagram != null) {
            if (printConvexHull) {
                page.setColor(new Color(333333333));
                printConvexHull(page);
            }

//            diagram.draw(page);
        }
        if (edges != null) {
            printEdges(page);
        }
        if (convexHulls != null) {
            printConvexHull(page);
        }
    }

    private void printEdges(Graphics page) {
        if (edges != null) {
            page.setColor(Color.RED);
            Point p1, p2;
            for (Pair<Point, Point> pair : edges) {
                p1 = pair.getKey();
                p2 = pair.getValue();
                page.drawLine((int) p1.x, HEIGHT - (int) p1.y,
                        (int) p2.x, HEIGHT - (int) p2.y);
            }
        }
    }

    private void printConvexHull(Graphics page) {
        if (diagram != null) {
            VoronoiPoint first = null, prev = null;
            for (VoronoiPoint point : diagram.convexHull) {
                if (first == null) first = point;
                if (prev != null) {
                    page.drawLine((int) point.x, HEIGHT - (int) point.y,
                            (int) prev.x, HEIGHT - (int) prev.y);
                }
                prev = point;
            }
            if (first != null) {
                page.drawLine((int) first.x, HEIGHT - (int) first.y,
                        (int) prev.x, HEIGHT - (int) prev.y);
            }
        }
        if (convexHulls != null) {
            page.setColor(Color.BLACK);
            for (ConvexHull convexHull : convexHulls) {
                VoronoiPoint first = null, prev = null;
                for (VoronoiPoint point : convexHull) {
                    if (first == null) first = point;
                    if (prev != null) {
                        page.drawLine((int) point.x, HEIGHT - (int) point.y,
                                (int) prev.x, HEIGHT - (int) prev.y);
                    }
                    prev = point;
                }
                if (first != null) {
                    page.drawLine((int) first.x, HEIGHT - (int) first.y,
                            (int) prev.x, HEIGHT - (int) prev.y);
                }
            }
        }
    }
}
