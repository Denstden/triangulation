import components.ConvexHull;
import components.Point;
import components.VoronoiPoint;
import exception.NoHullsException;
import javafx.util.Pair;
import solver.Triangulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DrawPolygons extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
    private JPanel panel;
    private JButton triangulate;
    private int countVertices;
    private boolean drawPolygons = true;
    private CustomMouseListener mouseListener;
    private List<ConvexHull> hulls = new ArrayList<>();


    public DrawPolygons(int countVertices, JTextArea area, Triangulator triangulator) {
        super("Draw polygons");
        this.countVertices = countVertices;
        init(area);

        triangulate.addActionListener(e -> {
            hulls = mouseListener.convexHulls;
            if (hulls.size()>1) {
                drawPolygons = false;

                triangulator.setHulls(hulls);

                int m = hulls.size();
                int k = hulls.get(0).size();
                try {
                    List<Pair<Point, Point>> edges = triangulator.triangulate();
                    area.append("Triangulation is completed for " + m + " polygons with " + k + " vertices each.\nTime = " + triangulator.getTime() + " ms.\n");
                    ((CustomJPanel) panel).edges = edges;
                    repaint();
                } catch (NoHullsException ex) {
                    area.append("Hulls do not generatePolygons!!!\n");
                }
            } else{
                area.append("Size of hulls must be greater than one!");
            }
        });
    }

    private void init(JTextArea area) {
        panel = new CustomJPanel();
        triangulate.setAlignmentX(Component.RIGHT_ALIGNMENT);
        triangulate.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        panel.add(triangulate);
        setMinimumSize(dimension);
        setPreferredSize(dimension);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        mouseListener = new CustomMouseListener(area);
        panel.addMouseListener(mouseListener);
        setContentPane(panel);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setVisible(true);
    }

    private class CustomJPanel extends JPanel{
        private List<Point> points = new ArrayList<>();
        private List<Pair<Point, Point>> edges = new ArrayList<>();

        public CustomJPanel(){
            setPreferredSize(dimension);
        }

        public void addPoint(Point point){
            points.add(point);
        }

        public void addEdge(Point point1, Point point2){
            edges.add(new Pair<>(point1, point2));
        }

        public void removePoint(Point point){
            points.remove(point);
            Point p1, p2;
            Pair<Point, Point> pair;
            for (int i=0; i< edges.size(); i++){
                pair = edges.get(i);
                p1 = pair.getKey();
                p2 = pair.getValue();
                if (p1.compareTo(point)==0 || p2.compareTo(point)==0){
                    edges.remove(pair);
                }
            }
        }

        private void printEdges(Graphics page) {
            if (edges != null) {
                page.setColor(Color.RED);
                Point p1, p2;
                for (Pair<Point, Point> pair : edges) {
                    p1 = pair.getKey();
                    p2 = pair.getValue();
                    page.drawLine((int) p1.x, (int) p1.y,
                            (int) p2.x, (int) p2.y);
                }
            }
        }

        public void paintComponent(Graphics page) {
            super.paintComponent(page);
            if (points != null) {
                for (Point point : points)
                    point.draw2(page);
            }

            if (edges != null) {
                printEdges(page);
            }

            if (hulls != null && !drawPolygons){
                page.setColor(Color.BLACK);
                for (ConvexHull hull:hulls){
                    printHull(hull, page);
                }
            }
        }

        private void printHull(ConvexHull hull, Graphics page) {
            VoronoiPoint first = null, prev = null;
            for (VoronoiPoint point : hull) {
                if (first == null) first = point;
                if (prev != null) {
                    page.drawLine((int) point.x,(int) point.y,
                            (int) prev.x, (int) prev.y);
                }
                prev = point;
            }
            if (first != null) {
                page.drawLine((int) first.x, (int) first.y,
                        (int) prev.x, (int) prev.y);
            }

        }
    }

    private class CustomMouseListener extends MouseAdapter{
        private int countPoints = 0;
        private List<ConvexHull> convexHulls = new ArrayList<>();
        private List<VoronoiPoint> points = new ArrayList<>();
        private VoronoiPoint lastPoint;
        private VoronoiPoint firstPoint;
        private JTextArea area;

        public CustomMouseListener(JTextArea area){
            this.area = area;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Component component = (Component)e.getSource();
            java.awt.Point location = component.getLocationOnScreen();
            if (drawPolygons) {
                addPoint(e.getXOnScreen()-location.x, e.getYOnScreen()-location.y);
            }
        }

        private void addPoint(int x, int y){
            VoronoiPoint point = new VoronoiPoint(x, y);
            if (points.contains(point)){
                clearPolygon();
            }
            else {
                if (++countPoints % (countVertices + 1) != 0) {
                    drawPoint(point);
                } else {
                    addPolygon();
                }
            }
            repaint();
        }

        private void drawPoint(VoronoiPoint point) {
            if (countPoints % (countVertices + 1) == 1) {
                firstPoint = point;
            }
            points.add(point);
            draw(point.x, point.y);
            if (lastPoint != null) {
                drawLine(lastPoint, point);
            }
            lastPoint = point;
        }

        private void addPolygon() {
            drawLine(lastPoint, firstPoint);
            if (isConvex(points)) {
                Collections.sort(points);
                convexHulls.add(new ConvexHull(points));
            } else {
                deletePoints(points);
                area.append("Not convex!\n");
            }
            points = new ArrayList<>();
            lastPoint = null;
            firstPoint = null;
        }

        private void clearPolygon() {
            deletePoints(points);
            points = new ArrayList<>();
            lastPoint = null;
            firstPoint = null;
            area.append("Points must not be repeated!\n");
            countPoints -= countPoints % (countVertices + 1);
        }

        private void deletePoints(List<VoronoiPoint> points){
            for (VoronoiPoint point: points){
                ((CustomJPanel)(panel)).removePoint(new Point(point.x, point.y));
            }
        }

        private boolean isConvex(List<VoronoiPoint> points) {
            if (points.size() < 4)
                return true;
            boolean sign = false;
            int n = points.size();
            for (int i = 0; i < n; i++) {
                double dx1 = points.get((i + 2) % n).x - points.get((i + 1) % n).x;
                double dy1 = points.get((i + 2) % n).y - points.get((i + 1) % n).y;
                double dx2 = points.get(i).x - points.get((i + 1) % n).x;
                double dy2 = points.get(i).y - points.get((i + 1) % n).y;
                double zcrossproduct = dx1 * dy2 - dy1 * dx2;
                if (i == 0)
                    sign = zcrossproduct > 0;
                else {
                    if (sign != (zcrossproduct > 0))
                        return false;
                }
            }
            return true;
        }

        private void draw(double x, double y){
            ((CustomJPanel)(panel)).addPoint(new Point(x, y));
        }

        private void drawLine(VoronoiPoint lastPoint, VoronoiPoint point){
            ((CustomJPanel)(panel)).addEdge(new Point(lastPoint.x, lastPoint.y), new Point(point.x, point.y));
        }
    }
}
