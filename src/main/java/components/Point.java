package components;

import java.awt.*;
import java.util.Random;

public class Point implements Comparable<Point> {
    public double x;
    public double y;
    Point nearestNeighbour;
    protected Color color = new Color(Math.abs((new Random()).nextInt()) % 16777216);
    // 256*256*256
    private int hullMark = -1;                                      // no hull

    public int getHullMark() {
        return hullMark;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        nearestNeighbour = null;
    }

    public Point(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        nearestNeighbour = null;
        this.color = color;
    }

    public Point() {
        this.x = Math.random() * PointsPanel.WIDTH;
        this.y = Math.random() * PointsPanel.HEIGHT;
    }

    public Point(double x, double y, int hullMark) {
        this(x, y);
        this.hullMark = hullMark;
    }

    public Point(Point p, int hullMark) {
        this.x = p.x;
        this.y = p.y;
        this.nearestNeighbour = p.nearestNeighbour;
        this.hullMark = hullMark;
    }

    public int compareTo(Point point) {
        if (x > point.x || x == point.x && y < point.y) return 1;
        if (x == point.x && y == point.y) return 0;
        return -1;

    }

    public double distanceTo(Point point) {
        if (point == null) return Double.MAX_VALUE;
        return Math.sqrt((x - point.x) * (x - point.x) + (y - point.y) * (y - point.y));
    }

    public void print() {
        System.out.println(x + " " + y);
    }

    public Point getNearestNeighbour() {
        return nearestNeighbour;
    }

    public void setNearestNeighbour(Point newNeighbour) {
        if ((nearestNeighbour == null || distanceTo(nearestNeighbour) > distanceTo(newNeighbour))
                && ((hullMark == -1 && newNeighbour.hullMark == -1)
                    || (hullMark >= 0 && newNeighbour.hullMark >= 0 && hullMark != newNeighbour.hullMark)))
            nearestNeighbour = newNeighbour;
    }

    public void draw(Graphics page) {
        page.setColor(color);
        page.fillOval((int) x - 3, PointsPanel.HEIGHT - (int) y - 3, 6, 6);
        if (nearestNeighbour != null && PointsPanel.printNeighbours)
            page.drawLine((int) x, PointsPanel.HEIGHT - (int) y, (int) nearestNeighbour.x, PointsPanel.HEIGHT - (int) nearestNeighbour.y);
    }

    public void draw2(Graphics page){
        page.setColor(Color.BLUE);
        page.fillOval((int)x-2, (int)y-2, 4, 4);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object point) {
        double epsilon = 0.00001;
        return point instanceof Point && Math.abs(x - ((Point) point).x) < epsilon && Math.abs(y - ((Point) point).y) < epsilon;
    }

    public static double polarAngle(Point center, Point p) {
        double alpha = Math.atan2(p.y - center.y, p.x - center.x);
        if (alpha < 0) alpha += 2 * Math.PI;
        return alpha;
    }

    public Point turnBack(double angle) {
        double x = this.x * Math.cos(angle) + this.y * Math.sin(angle);
        double y = -this.x * Math.sin(angle) + this.y * Math.cos(angle);
        return new Point(x, y);
    }

    public static boolean leftTurn(Point p1, Point p2, Point p3) {
        return ((p2.x - p1.x) * (p3.y - p2.y) - (p2.y - p1.y) * (p3.x - p2.x)) >= 0;
    }

    public static boolean rightTurn(Point p1, Point p2, Point p3) {
        return ((p2.x - p1.x) * (p3.y - p2.y) - (p2.y - p1.y) * (p3.x - p2.x)) < 0;
    }
}
