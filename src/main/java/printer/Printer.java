package printer;

import components.ConvexHull;
import components.Point;
import components.PointsPanel;
import javafx.util.Pair;

import javax.swing.*;
import java.util.List;

public class Printer {
    public static PointsPanel printAll(List<Pair<Point, Point>> edges, List<ConvexHull> hulls, String name) {
        JFrame frame = new JFrame("Random generation");
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        PointsPanel panel = new PointsPanel(null, edges, hulls);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
        return panel;
    }
}
