import components.ConvexHull;
import components.Point;
import exception.NoHullsException;
import javafx.util.Pair;
import printer.Printer;
import solver.PolygonsGenerator;
import solver.Triangulator;
import util.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class MainForm extends JFrame {
    private JPanel mainPanel;
    private JButton genarateRandomButton;
    private JTextField kTextField;
    private JTextField mTextField;
    private JButton readPolygonsButton;
    private JLabel folderLabel;
    private JTextField sourceFileField;
    private JTextArea inforamtionTextArea;
    private JTextField dstFileField;
    private JButton savePolygonsButton;
    private JButton drawPolygonsButton;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);

    private FileUtils fileUtils = new FileUtils();
    private Triangulator triangulator = new Triangulator();

    public MainForm() {
        super("Triangulation");
        init();

        String delim = ",";

        genarateRandomButton.addActionListener(e -> {
            String kStr = kTextField.getText();
            String mStr = mTextField.getText();
            Integer k = -1, m = -1;
            boolean flag = true;
            try {
                k = Integer.valueOf(kStr);
            } catch (Exception ex) {
                inforamtionTextArea.append("Value in \"Number of vertices\" field is not a number\n");
                flag = false;
            }
            try {
                m = Integer.valueOf(mStr);
            } catch (Exception ex) {
                inforamtionTextArea.append("Value in \"Number of polygons\" field is not a number\n");
                flag = false;
            }
            if (flag) {
                if (k > 2 && m > 1) {
                    List<ConvexHull> hulls = new PolygonsGenerator(m, k).generatePolygons();
                    triangulator.setHulls(hulls);
                    try {
                        List<Pair<Point, Point>> edges = triangulator.triangulate();
                        inforamtionTextArea.append("Triangulation is completed for " + m + " polygons with " + k + " vertices each.\nTime = " + triangulator.getTime() + " ms.\n");
                        Printer.printAll(edges, hulls, triangulator.getTitle());
                    } catch (NoHullsException ex) {
                        inforamtionTextArea.append("Hulls do not generatePolygons!!!\n");
                    }
                }
                if (k <= 2) {
                    inforamtionTextArea.append("Number of vertices is less or equal to 2.\nPlease, choose greater number.\n");
                }
                if (m <= 1) {
                    inforamtionTextArea.append("Number of polygons is less or equal to 1.\nPlease, choose greater number.\n");
                }
            }
        });

        readPolygonsButton.addActionListener(e -> {
            String filename = sourceFileField.getText();
            List<ConvexHull> hulls = null;
            try {
                hulls = fileUtils.readConvexHullsFromFile(filename, delim);
                triangulator.setHulls(hulls);
            } catch (IOException ex) {
                inforamtionTextArea.append(fileUtils.getWarning());
            }

            int m = hulls.size();
            int k = hulls.get(0).size();
            try {
                List<Pair<Point, Point>> edges = triangulator.triangulate();
                inforamtionTextArea.append("Triangulation is completed for " + m + " polygons with " + k + " vertices each.\nTime = " + triangulator.getTime() + " ms.\n");
                Printer.printAll(edges, hulls, triangulator.getTitle());
            } catch (NoHullsException ex){
                inforamtionTextArea.append("Hulls do not generatePolygons!!!\n");
            }
        });

        savePolygonsButton.addActionListener(e -> {
            if (triangulator.getHulls() != null) {
                String filename = dstFileField.getText();
                if (fileUtils.writeConvexHullsToFile(triangulator.getHulls(), filename, delim)) {
                    inforamtionTextArea.append("Saving is completed");
                }
                else {
                    inforamtionTextArea.append(fileUtils.getWarning());
                }
            }
            else {
                inforamtionTextArea.append("List of polygons is empty. Please, generatePolygons it or read it from file.\n");
            }
        });

        drawPolygonsButton.addActionListener(e->{
            String kStr = kTextField.getText();
            Integer k = -1;
            boolean flag = true;
            try {
                k = Integer.valueOf(kStr);
            } catch (Exception ex) {
                inforamtionTextArea.append("Value in \"Number of vertices\" field is not a number\n");
                flag = false;
            }
            if (flag) {
                if (k <= 2) {
                    inforamtionTextArea.append("Number of vertices is less or equal to 2.\nPlease, choose greater number.\n");
                } else {
                    new DrawPolygons(k, inforamtionTextArea, triangulator);
                }
            }
        });

    }

    private void init() {
        setContentPane(mainPanel);
        setPreferredSize(dimension);
        setMinimumSize(dimension);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
