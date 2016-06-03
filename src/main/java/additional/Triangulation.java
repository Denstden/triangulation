package additional;

import java.util.ArrayList;

public class Triangulation {
	public static boolean debug = false;

	public static String debugFileName = "polygon_triangulation_log.txt";

	public static ArrayList triangulate(int numContures, int[] numVerticesInContures, double[][] vertices) {
		Polygon p = new Polygon(numContures, numVerticesInContures, vertices);
		if (debug) {
			p.setDebugFile(debugFileName);
			p.setDebugOption(debug);
		} else {
			p.setDebugOption(false);
		}
		if (p.triangulation())
			return p.triangles();
		else
			return null;
	}
}
