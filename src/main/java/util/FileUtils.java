package util;

import components.ConvexHull;
import components.VoronoiPoint;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {
    private String warning;

    public List<ConvexHull> readConvexHullsFromFile(String filename, String delim) throws IOException {
        List<ConvexHull> hulls = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    int pointsNum = Integer.parseInt(line);
                    List<VoronoiPoint> points = new ArrayList<>(pointsNum);
                    for (int i = 0; i < pointsNum && (line = br.readLine()) != null; ++i) {
                        String[] point = line.split(delim);
                        if (point.length == 2) {
                            points.add(new VoronoiPoint(Double.parseDouble(point[0]), Double.parseDouble(point[1])));
                        } else {
                            warning = "Problem with reading " + filename + "\nIncorrect data in file\n";
                            System.out.print(warning);
                        }
                    }
                    Collections.sort(points);
                    hulls.add(new ConvexHull(points));
                }
            }
        } catch (IOException e) {
            warning = "Problem with opening file \'" + filename + "\'\n";
            throw new IOException();
        }
        return hulls;
    }

    public boolean writeConvexHullsToFile(List<ConvexHull> hulls, String filename, String delim) {
        if (hulls.size() > 0) {
            for (int i = 0; i < hulls.size(); ++i) {
                ConvexHull hull = hulls.get(i);
                if (hull.size() > 0) {
                    List<String> lines = new ArrayList<>();
                    lines.add(Integer.toString(hull.size()));
                    VoronoiPoint point;
                    for (int j = 0; j < hull.size(); ++j) {
                        point = hull.get(j);
                        lines.add(point.x + delim + point.y);
                    }

                    Path file = Paths.get(filename);
                    try {
                        if (i > 0) {
                            Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
                        } else {
                            Files.write(file, lines, Charset.forName("UTF-8"));
                        }
                    } catch (IOException ex) {
                        warning = "Problem with writing to " + filename + "\n";
                        System.out.print(warning);
                        return false;
                    }
                }
            }
        } else {
            warning = "List of hulls is empty\n";
            System.out.print(warning);
        }
        return true;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }
}
