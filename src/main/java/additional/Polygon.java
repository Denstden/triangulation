package additional;

import additional.splayTree.BTreeNode;
import additional.splayTree.SplayTree;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Polygon {

	protected int _ncontours = 0;

	protected int[] _nVertices = null;

	protected HashMap _points = new HashMap();

	protected int[] _pointsKeys = null;

	protected HashMap _edges = new HashMap();

	protected int[] _edgesKeys = null;

	private PriorityQueue _qpoints = new PriorityQueue(30, new PointbaseComparatorCoordinatesReverse());

	private SplayTree _edgebst = new SplayTree();

	private ArrayList _mpolys = new ArrayList();

	private ArrayList _triangles = new ArrayList();

	private HashMap _startAdjEdgeMap = new HashMap();

	private HashMap _diagonals = new HashMap();

	private boolean _debug = false;

	private FileWriter _logfile = null;

	private UpdateKey updateKey = new UpdateKey();

	private String _debugFileName = "polygon_triangulation_log.txt";

	public HashMap points() {
		return _points;
	}

	public HashMap edges() {
		return _edges;
	}

	private void initPolygon(int numContours, int[] numVerticesInContours, double[][] vertices) {
		int i, j;
		int nextNumber = 1;

		_ncontours = numContours;
		_nVertices = new int[_ncontours];
		for (i = 0; i < numContours; ++i) {
			for (j = 0; j < numVerticesInContours[i]; ++j) {
				_points.put(nextNumber, new Pointbase(nextNumber, vertices[nextNumber - 1][0], vertices[nextNumber - 1][1], TriUtils.INPUT));
				++nextNumber;
			}
		}
		_nVertices[0] = numVerticesInContours[0];
		for (i = 1; i < _ncontours; ++i) {
			_nVertices[i] = _nVertices[i - 1] + numVerticesInContours[i];
		}
		i = 0;
		j = 1;
		int first = 1;
		Linebase edge;

		while (i < _ncontours) {
			for (; j + 1 <= _nVertices[i]; ++j) {
				edge = new Linebase((Pointbase) _points.get(j), (Pointbase) _points.get(j + 1), TriUtils.INPUT);
				_edges.put(TriUtils.l_id, edge);
			}
			edge = new Linebase((Pointbase) _points.get(j), (Pointbase) _points.get(first), TriUtils.INPUT);
			_edges.put(TriUtils.l_id, edge);

			j = _nVertices[i] + 1;
			first = _nVertices[i] + 1;
			++i;
		}
		TriUtils.p_id = _nVertices[_ncontours - 1];
	}

	Polygon(int numContures, int[] numVerticesInContures, double[][] vertices) {
		TriUtils.initPoly2TriUtils();
		initPolygon(numContures, numVerticesInContures, vertices);
		initializate();
		_debug = false;
	}

	public void writeToLog(String s) {
		if (!_debug) return;
		try {
			_logfile.write(s);
		} catch (IOException e) {
			_debug = false;
			System.out.println("Writing to LogFile (debugging) failed.");
			e.printStackTrace();
			System.out.println("Setting _debug = false, continuing the work.");
		}
	}

	public Pointbase getPoint(int index) {
		return (Pointbase) _points.get(index);
	}

	public Linebase getEdge(int index) {
		return (Linebase) _edges.get(index);
	}

	public Pointbase qpointsTop() {
		return (Pointbase) _qpoints.peek();
	}

	public Pointbase qpointsPop() {
		return (Pointbase) _qpoints.poll();
	}

	public void destroy() {
	}

	public boolean is_exist(double x, double y) {
		Iterator iter = _points.keySet().iterator();
		Pointbase pb;
		while (iter.hasNext()) {
			pb = getPoint((Integer) iter.next());
			if ((pb.x == x) && (pb.y == y)) return true;
		}
		return false;
	}

	private int prev(int i) {
		int j = 0, prevLoop = 0, currentLoop = 0;

		while (i > _nVertices[currentLoop]) {
			prevLoop = currentLoop;
			currentLoop++;
		}

		if (i == 1 || (i == _nVertices[prevLoop] + 1)) j = _nVertices[currentLoop];
		else if (i <= _nVertices[currentLoop]) j = i - 1;

		return j;
	}

	private int next(int i) {
		int j = 0, prevLoop = 0, currentLoop = 0;

		while (i > _nVertices[currentLoop]) {
			prevLoop = currentLoop;
			currentLoop++;
		}

		if (i < _nVertices[currentLoop]) j = i + 1;
		else if (i == _nVertices[currentLoop]) {
			if (currentLoop == 0) j = 1;
			else j = _nVertices[prevLoop] + 1;
		}

		return j;
	}

	private void rotate(double theta) {
		for (int i = 0; i < _pointsKeys.length; ++i)
			(getPoint(_pointsKeys[i])).rotate(theta);
	}

	private int[] getSorted(Set s) {
		Object[] temp = s.toArray();
		int[] result = new int[temp.length];
		for (int i = 0; i < temp.length; ++i) {
			result[i] = ((Integer) temp[i]).intValue();
		}
		Arrays.sort(result);
		return result;
	}

	private void initializePointsKeys() {
		_pointsKeys = getSorted(_points.keySet());
	}

	private void initializeEdgesKeys() {
		_edgesKeys = getSorted(_edges.keySet());
	}

	private Set getSetFromStartAdjEdgeMap(int index) {
		Set s = (Set) _startAdjEdgeMap.get(index);
		if (s != null) return s;
		s = new HashSet();
		_startAdjEdgeMap.put(index, s);
		return s;
	}

	private void initializate() {
		initializePointsKeys();

		int id, idp, idn;
		Pointbase p, pnext, pprev;
		double area;

		for (int i = 0; i < _pointsKeys.length; ++i) {
			id = _pointsKeys[i];
			idp = prev(id);
			idn = next(id);

			p = getPoint(id);
			pnext = getPoint(idn);
			pprev = getPoint(idp);

			if ((p.compareTo(pnext) > 0) && (pprev.compareTo(p) > 0))
				p.type = TriUtils.REGULAR_DOWN;
			else if ((p.compareTo(pprev) > 0) && (pnext.compareTo(p) > 0))
				p.type = TriUtils.REGULAR_UP;
			else {
				area = TriUtils.orient2d(new double[]{pprev.x, pprev.y},
						new double[]{p.x, p.y},
						new double[]{pnext.x, pnext.y});

				if ((pprev.compareTo(p) > 0) && (pnext.compareTo(p) > 0))
					p.type = (area > 0) ? TriUtils.END : TriUtils.MERGE;
				if ((pprev.compareTo(p) < 0) && (pnext.compareTo(p) < 0))
					p.type = (area > 0) ? TriUtils.START : TriUtils.SPLIT;
			}

			_qpoints.add(new Pointbase(p));

			getSetFromStartAdjEdgeMap(id).add(id);
		}
	}

	private void addDiagonal(int i, int j) {
		int type = TriUtils.INSERT;

		Linebase diag = new Linebase(getPoint(i),
				getPoint(j),
				type);
		_edges.put(diag.id(), diag);

		getSetFromStartAdjEdgeMap(i).add(diag.id());
		getSetFromStartAdjEdgeMap(j).add(diag.id());

		_diagonals.put(diag.id(), diag);

		writeToLog("Add Diagonal from " + i + " to " + j + "\n");
	}

	private void handleStartVertex(int i) {

		double y = ((Pointbase) _points.get(i)).y;

		_edgebst.inOrder(updateKey, y);

		Linebase edge = getEdge(i);
		edge.setHelper(i);
		edge.setKeyValue(y);

		_edgebst.insert(edge);

		if (_debug) {
			writeToLog("set e" + i + " helper to " + i + "\n");
			writeToLog("Insert e" + i + " to splay tree\n");
			writeToLog("key:" + edge.keyValue() + "\n");
		}
	}

	private void handleEndVertex(int i) {
		double y = getPoint(i).y;

		_edgebst.inOrder(updateKey, y);

		int previ = prev(i);
		Linebase edge = getEdge(previ);
		int helper = edge.helper();

		if (getPoint(helper).type == TriUtils.MERGE)
			addDiagonal(i, helper);
		_edgebst.delete(edge.keyValue());

		if (_debug) {
			writeToLog("Remove e" + previ + " from splay tree\n");
			writeToLog("key:" + edge.keyValue() + "\n");
		}
	}

	private void handleSplitVertex(int i) {
		Pointbase point = getPoint(i);
		double x = point.x, y = point.y;

		_edgebst.inOrder(updateKey, y);

		BTreeNode leftnode = _edgebst.findMaxSmallerThan(x);
		Linebase leftedge = (Linebase) leftnode.data();

		int helper = leftedge.helper();
		addDiagonal(i, helper);

		if (_debug) {
			writeToLog("Search key:" + x + " edge key:" + leftedge.keyValue() + "\n");
			writeToLog("e" + leftedge.id() + " is directly left to v" + i + "\n");
			writeToLog("Set e" + leftedge.id() + " helper to " + i + "\n");
			writeToLog("set e" + i + " helper to " + i + "\n");
			writeToLog("Insert e" + i + " to splay tree\n");
			writeToLog("Insert key:" + getEdge(i).keyValue() + "\n");
		}

		leftedge.setHelper(i);
		Linebase edge = getEdge(i);
		edge.setHelper(i);
		edge.setKeyValue(y);
		_edgebst.insert(edge);
	}

	private void handleMergeVertex(int i) {
		Pointbase point = getPoint(i);
		double x = point.x, y = point.y;

		_edgebst.inOrder(updateKey, y);

		int previ = prev(i);
		Linebase previEdge = getEdge(previ);
		int helper = previEdge.helper();

		Pointbase helperPoint = getPoint(helper);

		if (helperPoint.type == TriUtils.MERGE)
			addDiagonal(i, helper);

		_edgebst.delete(previEdge.keyValue());

		if (_debug) {
			writeToLog("e" + previ + " helper is " + helper + "\n");
			writeToLog("Remove e" + previ + " from splay tree.\n");
		}

		BTreeNode leftnode = _edgebst.findMaxSmallerThan(x);
		Linebase leftedge = (Linebase) leftnode.data();

		helper = leftedge.helper();
		helperPoint = getPoint(helper);
		if (helperPoint.type == TriUtils.MERGE)
			addDiagonal(i, helper);

		leftedge.setHelper(i);

		if (_debug) {
			writeToLog("Search key:" + x + " found:" + leftedge.keyValue() + "\n");
			writeToLog("e" + leftedge.id() + " is directly left to v" + i + "\n");
			writeToLog("Set e" + leftedge.id() + " helper to " + i + "\n");
		}
	}

	private void handleRegularVertexDown(int i) {
		Pointbase point = getPoint(i);

		double y = point.y;

		_edgebst.inOrder(updateKey, y);

		int previ = prev(i);

		Linebase previEdge = getEdge(previ);

		int helper = previEdge.helper();

		Pointbase helperPoint = getPoint(helper);

		if (helperPoint.type == TriUtils.MERGE)
			addDiagonal(i, helper);

		_edgebst.delete(previEdge.keyValue());

		Linebase edge = getEdge(i);
		edge.setHelper(i);
		edge.setKeyValue(y);
		_edgebst.insert(edge);

		if (_debug) {
			writeToLog("e" + previ + " helper is " + helper + "\n");
			writeToLog("Remove e" + previ + " from splay tree.\n");
			writeToLog("Set e" + i + " helper to " + i + "\n");
			writeToLog("Insert e" + i + " to splay tree\n");
			writeToLog("Insert key:" + edge.keyValue() + "\n");
		}
	}

	private void handleRegularVertexUp(int i) {
		Pointbase point = getPoint(i);

		double x = point.x, y = point.y;

		_edgebst.inOrder(updateKey, y);

		BTreeNode leftnode = _edgebst.findMaxSmallerThan(x);

		Linebase leftedge = (Linebase) leftnode.data();

		int helper = leftedge.helper();
		Pointbase helperPoint = getPoint(helper);
		if (helperPoint.type == TriUtils.MERGE) addDiagonal(i, helper);
		leftedge.setHelper(i);

		if (_debug) {
			writeToLog("Search key:" + x + " found:" + leftedge.keyValue() + "\n");
			writeToLog("e" + leftedge.id() + " is directly left to v" + i + " and its helper is:" + helper + "\n");
			writeToLog("Set e" + leftedge.id() + " helper to " + i + "\n");
		}
	}

	public boolean partition2Monotone() {
		if (qpointsTop().type != TriUtils.START) {
			System.out.println("Please check your input polygon:\n1)orientations?\n2)duplicated points?\n");
			System.out.println("poly2tri stopped.\n");
			return false;
		}

		int id;
		while (_qpoints.size() > 0) {
			Pointbase vertex = qpointsPop();

			id = vertex.id;

			if (_debug) {
				String stype;
				switch (vertex.type) {
					case TriUtils.START:
						stype = "START";
						break;
					case TriUtils.END:
						stype = "END";
						break;
					case TriUtils.MERGE:
						stype = "MERGE";
						break;
					case TriUtils.SPLIT:
						stype = "SPLIT";
						break;
					case TriUtils.REGULAR_UP:
						stype = "REGULAR_UP";
						break;
					case TriUtils.REGULAR_DOWN:
						stype = "REGULAR_DOWN";
						break;
					default:
						System.out.println("No duplicated points please! poly2tri stopped\n");
						return false;
				}
				writeToLog("\n\nHandle vertex:" + vertex.id + " type:" + stype + "\n");
			}

			switch (vertex.type) {
				case TriUtils.START:
					handleStartVertex(id);
					break;
				case TriUtils.END:
					handleEndVertex(id);
					break;
				case TriUtils.MERGE:
					handleMergeVertex(id);
					break;
				case TriUtils.SPLIT:
					handleSplitVertex(id);
					break;
				case TriUtils.REGULAR_UP:
					handleRegularVertexUp(id);
					break;
				case TriUtils.REGULAR_DOWN:
					handleRegularVertexDown(id);
					break;
				default:
					System.out.println("No duplicated points please! poly2tri stopped\n");
					return false;
			}
		}
		return true;
	}

	private double angleCosb(double[] pa, double[] pb, double[] pc) {
		double dxab = pa[0] - pb[0];
		double dyab = pa[1] - pb[1];

		double dxcb = pc[0] - pb[0];
		double dycb = pc[1] - pb[1];

		double dxab2 = dxab * dxab;
		double dyab2 = dyab * dyab;
		double dxcb2 = dxcb * dxcb;
		double dycb2 = dycb * dycb;
		double ab = dxab2 + dyab2;
		double cb = dxcb2 + dycb2;

		double cosb = dxab * dxcb + dyab * dycb;
		double denom = Math.sqrt(ab * cb);

		cosb /= denom;

		return cosb;
	}

	private int selectNextEdge(Linebase edge) {
		int eid = edge.endPoint(1).id;
		Set edges = getSetFromStartAdjEdgeMap(eid);

		assert (edges.size() != 0);

		int nexte = 0;

		if (edges.size() == 1)
			nexte = (Integer) (edges.iterator().next());
		else {
			int[] edgesKeys = getSorted(edges);

			int nexte_ccw = 0, nexte_cw = 0;
			double max = -2.0, min = 2.0; // max min of cos(alfa)
			Linebase iEdge;

			Iterator iter = edges.iterator();
			int it;
			while (iter.hasNext()) {
				it = (Integer) iter.next();
				if (it == edge.id()) continue;

				iEdge = getEdge(it);

				double[] A = {0, 0}, B = {0, 0}, C = {0, 0};
				A[0] = edge.endPoint(0).x;
				A[1] = edge.endPoint(0).y;
				B[0] = edge.endPoint(1).x;
				B[1] = edge.endPoint(1).y;

				if (!edge.endPoint(1).equals(iEdge.endPoint(0))) iEdge.reverse();
				C[0] = iEdge.endPoint(1).x;
				C[1] = iEdge.endPoint(1).y;

				double area = TriUtils.orient2d(A, B, C);
				double cosb = angleCosb(A, B, C);

				if (area > 0 && max < cosb) {
					nexte_ccw = it;
					max = cosb;
				} else if (min > cosb) {
					nexte_cw = it;
					min = cosb;
				}
			}

			nexte = (nexte_ccw != 0) ? nexte_ccw : nexte_cw;
		}

		return nexte;
	}

	public boolean searchMonotones() {
		int loop = 0;

		HashMap edges = (HashMap) _edges.clone();

		ArrayList poly;
		int[] edgesKeys;
		int i;
		int it;
		Linebase itEdge;

		Pointbase startp, endp;
		Linebase next;
		int nexte;

		while (edges.size() > _diagonals.size()) {
			loop++;
			// typedef list<unsigned int> Monopoly;
			poly = new ArrayList();

			edgesKeys = getSorted(edges.keySet());

			it = edgesKeys[0];
			itEdge = (Linebase) edges.get(it);

			// Pointbase* startp=startp=it.second.endPoint(0); // ??? startp=startp :-O
			startp = itEdge.endPoint(0);
			endp = null;
			next = itEdge;

			poly.add(startp.id);

			if (_debug) {
				writeToLog("Searching for loops:" + loop + "\n");
				writeToLog("vertex index:" + startp.id + " ");
			}

			for (; ; ) {

				endp = next.endPoint(1);

				if (next.type() != TriUtils.INSERT) {
					edges.remove(next.id());
					getSetFromStartAdjEdgeMap(next.endPoint(0).id).remove(next.id());
				}
				if (endp == startp) break;
				poly.add(endp.id);

				writeToLog(endp.id + " ");

				nexte = selectNextEdge(next);

				if (nexte == 0) {
					System.out.println("Please check your input polygon:\n");
					System.out.println("1)orientations?\n2)with duplicated points?\n3)is a simple one?\n");
					System.out.println("poly2tri stopped.\n");
					return false;
				}

				next = (Linebase) edges.get(nexte);
				if (!(next.endPoint(0).equals(endp))) next.reverse();
			}

			writeToLog("\nloop closed!\n\n");

			_mpolys.add(poly);
		}
		return true;
	}

	private void triangulateMonotone(ArrayList mpoly) {
		PriorityQueue qvertex = new PriorityQueue(30, new PointbaseComparatorCoordinatesReverse());
		int i, it, itnext;
		Pointbase point;
		Pointbase pointnext;
		for (it = 0; it < mpoly.size(); it++) {
			itnext = it + 1;
			if (itnext == mpoly.size()) itnext = 0;
			point = new Pointbase(getPoint((Integer) mpoly.get(it)));
			pointnext = new Pointbase(getPoint((Integer) mpoly.get(itnext)));
			point.left = (point.compareTo(pointnext) > 0) ? true : false;
			qvertex.add(point);
		}

		Stack spoint = new Stack();

		for (i = 0; i < 2; i++) spoint.push(qvertex.poll());

		Pointbase topQueuePoint;
		Pointbase topStackPoint;
		Pointbase p1, p2;
		Pointbase stack1Point, stack2Point;

		double[] pa = {0, 0}, pb = {0, 0}, pc = {0, 0};
		double area;
		boolean left;
		ArrayList v;

		while (qvertex.size() > 1) {

			topQueuePoint = (Pointbase) qvertex.peek();
			topStackPoint = (Pointbase) spoint.peek();

			if (topQueuePoint.left != topStackPoint.left) {

				while (spoint.size() > 1) {

					p1 = (Pointbase) spoint.peek();
					spoint.pop();
					p2 = (Pointbase) spoint.peek();

					// typedef vector<unsigned int> Triangle;
					v = new ArrayList(3);
					v.add(topQueuePoint.id - 1);
					v.add(p1.id - 1);
					v.add(p2.id - 1);
					_triangles.add(v);

					writeToLog("Add triangle:" + ((Integer) v.get(0) + 1) + " " + ((Integer) v.get(1) + 1) + " " + ((Integer) v.get(2) + 1) + "\n");

				}
				spoint.pop();
				spoint.push(topStackPoint);
				spoint.push(topQueuePoint);

			} else {

				while (spoint.size() > 1) {

					stack1Point = (Pointbase) spoint.peek();
					spoint.pop();
					stack2Point = (Pointbase) spoint.peek();
					spoint.push(stack1Point);

					pa[0] = topQueuePoint.x;
					pa[1] = topQueuePoint.y;
					pb[0] = stack2Point.x;
					pb[1] = stack2Point.y;
					pc[0] = stack1Point.x;
					pc[1] = stack1Point.y;

					if (_debug) {
						writeToLog("current top queue vertex index=" + topQueuePoint.id + "\n");
						writeToLog("Current top stack vertex index=" + stack1Point.id + "\n");
						writeToLog("Second stack vertex index=" + stack2Point.id + "\n");
					}

					area = TriUtils.orient2d(pa, pb, pc);
					left = stack1Point.left;

					if ((area > 0 && left) || (area < 0 && !left)) {
						v = new ArrayList(3);
						v.add(topQueuePoint.id - 1);
						v.add(stack2Point.id - 1);
						v.add(stack1Point.id - 1);
						_triangles.add(v);
						writeToLog("Add triangle:" + ((Integer) v.get(0) + 1) + " " + ((Integer) v.get(1) + 1) + " " + ((Integer) v.get(2) + 1) + "\n");
						spoint.pop();
					} else
						break;
				}
				spoint.push(topQueuePoint);
			}
			qvertex.poll();
		}

		Pointbase lastQueuePoint = (Pointbase) qvertex.peek();
		Pointbase topPoint, top2Point; // C++ code ... copy construtors
		while (spoint.size() != 1) {
			topPoint = (Pointbase) spoint.peek();
			spoint.pop();
			top2Point = (Pointbase) spoint.peek();

			v = new ArrayList(3);
			v.add(lastQueuePoint.id - 1);
			v.add(topPoint.id - 1);
			v.add(top2Point.id - 1);
			_triangles.add(v);

			writeToLog("Add triangle:" + ((Integer) v.get(0) + 1) + " " + ((Integer) v.get(1) + 1) + " " + ((Integer) v.get(2) + 1) + "\n");
		}
	}

	public boolean triangulation() {
		if (!partition2Monotone()) return false;
		if (!searchMonotones()) return false;

		for (int i = 0; i < _mpolys.size(); ++i) {
			triangulateMonotone((ArrayList) _mpolys.get(i));
		}

		setDebugOption(false); // possibly closing the log file

		return true;
	}

	public ArrayList triangles() {
		return _triangles;
	}

	public void setDebugOption(boolean debug) {
		if (debug == _debug) return;
		if (_debug) {
			try {
				_logfile.close();
			} catch (IOException e) {
				System.out.println("Problem closing logfile.");
				e.printStackTrace();
				System.out.println("Continueing the work");
			}
		} else {
			try {
				_logfile = new FileWriter(_debugFileName);
			} catch (IOException e) {
				System.out.println("Error creating file polygon_triangulation_log.txt, switchin debug off, continuing.");
				e.printStackTrace();
				_debug = false;
			}
		}
		_debug = debug;
	}

	public void setDebugFile(String debugFileName) {
		_debugFileName = debugFileName;
	}
}
