package additional;

public class TriUtils {
	public static final double PI = Math.PI;

	public static final int UNKNOWN = 1;
	public static final int INPUT = 2;
	public static final int INSERT = 3;
	public static final int START = 4;
	public static final int END = 5;
	public static final int MERGE = 6;
	public static final int SPLIT = 7;
	public static final int REGULAR_UP = 8;
	public static final int REGULAR_DOWN = 9;

	public static String typeToString(int type) {
		switch (type) {
			case TriUtils.UNKNOWN:
				return "UNKNOWN";
			case TriUtils.INPUT:
				return "INPUT";
			case TriUtils.INSERT:
				return "INERT";
			case TriUtils.START:
				return "START";
			case TriUtils.END:
				return "END";
			case TriUtils.MERGE:
				return "MERGE";
			case TriUtils.SPLIT:
				return "SPLIT";
			case TriUtils.REGULAR_UP:
				return "REGULAR_UP";
			case TriUtils.REGULAR_DOWN:
				return "REGULAR_DOWN";
			default:
				return "??? (" + type + ")";
		}
	}

	public static double orient2d(double[] pa, double[] pb, double[] pc) {
		double detleft, detright;

		detleft = (pa[0] - pc[0]) * (pb[1] - pc[1]);
		detright = (pa[1] - pc[1]) * (pb[0] - pc[0]);

		return detleft - detright;
	}

	public static int l_id = 0;
	public static int p_id = 0;

	public static void initPoly2TriUtils() {
		l_id = 0;
		p_id = 0;
	}
}
