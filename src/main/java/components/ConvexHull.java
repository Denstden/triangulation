package components;

import components.avltree.AVLNode;
import components.avltree.AVLTree;
import components.avltree.ComparableComparator;

import java.util.*;

public class ConvexHull extends AbstractCollection<VoronoiPoint> implements Collection<VoronoiPoint> {
    AVLTree<VoronoiPoint> lowerHull;
    AVLTree<VoronoiPoint> upperHull;
    AVLTree<VoronoiPoint> hull;

    private ConvexHull() {
        lowerHull = new AVLTree<>(new ComparableComparator<>());
        upperHull = new AVLTree<>(new ComparableComparator<>().reversed());
        hull = new AVLTree<>();

        updateComparator();
    }

    public ConvexHull(VoronoiPoint p) {
        this();
        lowerHull.add(p);
        upperHull.add(p);
        hull.add(p);

        updateComparator();
    }

    public ConvexHull(ConvexHull convexHull) {
        lowerHull = new AVLTree<>(convexHull.lowerHull);
        upperHull = new AVLTree<>(convexHull.upperHull);
        hull = new AVLTree<>(convexHull.hull);

        updateComparator();
    }

    // p must be sorted. Use for this Collections.sort(p)
    public ConvexHull(List<VoronoiPoint> p) {
        this();
        ConvexHull convexHull = buildConvexHull(p);
        lowerHull = convexHull.lowerHull;
        upperHull = convexHull.upperHull;
        hull = convexHull.hull;
    }

    private ConvexHull buildConvexHull(List<VoronoiPoint> p) {
        if (p.size() == 1) {
            return new ConvexHull(p.get(0));
        }
        ConvexHull left = buildConvexHull(p.subList(0, p.size() / 2));
        ConvexHull right = buildConvexHull(p.subList(p.size() / 2, p.size()));
        return  merge(left, right);
    }

    public static List<VoronoiPoint> getUpperSupport(ConvexHull left, ConvexHull right) {
        AVLNode<VoronoiPoint> leftUpper = null;
        AVLNode<VoronoiPoint> rightUpper = null;

        if (left != null && right != null) {
            leftUpper = left.upperHull.getHead();
            VoronoiPoint leftMin = left.upperHull.get(0);
            VoronoiPoint leftMax = left.upperHull.get(left.upperHull.size() - 1);

            rightUpper = right.upperHull.getHead();
            VoronoiPoint rightMin = right.upperHull.get(0);
            VoronoiPoint rightMax = right.upperHull.get(right.upperHull.size() - 1);

            Line support = new Line(leftUpper.value, rightUpper.value);

            boolean resFound = false;
            while (!resFound) {
                resFound = true;
                if (!leftMin.equals(leftUpper.value) && support.isLower(leftUpper.prev.value)) {
                    leftUpper = leftUpper.left != null ? leftUpper.left : leftUpper.prev;
                    support = new Line(leftUpper.value, rightUpper.value);
                    resFound = false;
                } else if (!leftMax.equals(leftUpper.value) && !support.isHigher(leftUpper.next.value)) {
                    leftUpper = leftUpper.right != null ? leftUpper.right : leftUpper.next;
                    support = new Line(leftUpper.value, rightUpper.value);
                    resFound = false;
                } else if (!rightMax.equals(rightUpper.value) && support.isLower(rightUpper.next.value)) {
                    rightUpper = rightUpper.right != null ? rightUpper.right : rightUpper.next;
                    support = new Line(leftUpper.value, rightUpper.value);
                    resFound = false;
                } else if (!rightMin.equals(rightUpper.value) && !support.isHigher(rightUpper.prev.value)) {
                    rightUpper = rightUpper.left != null ? rightUpper.left : rightUpper.prev;
                    support = new Line(leftUpper.value, rightUpper.value);
                    resFound = false;
                }
            }
        }

        List<VoronoiPoint> res = new ArrayList<>();
        res.add(leftUpper == null ? null : leftUpper.value);
        res.add(rightUpper == null ? null : rightUpper.value);
        return res;
    }

    public static List<VoronoiPoint> getLowerSupport(ConvexHull left, ConvexHull right) {
        AVLNode<VoronoiPoint> leftLower = null;
        AVLNode<VoronoiPoint> rightLower = null;

        if (left != null && right != null) {
            leftLower = left.lowerHull.getHead();
            VoronoiPoint leftMin = left.lowerHull.get(0);
            VoronoiPoint leftMax = left.lowerHull.get(left.lowerHull.size() - 1);

            rightLower = right.lowerHull.getHead();
            VoronoiPoint rightMin = right.lowerHull.get(0);
            VoronoiPoint rightMax = right.lowerHull.get(right.lowerHull.size() - 1);

            Line support = new Line(leftLower.value, rightLower.value);

            boolean resFound = false;
            while (!resFound) {
                resFound = true;
                if (!leftMax.equals(leftLower.value) && support.isHigher(leftLower.next.value)) {
                    leftLower = leftLower.right != null ? leftLower.right : leftLower.next;
                    support = new Line(leftLower.value, rightLower.value);
                    resFound = false;
                } else if (!leftMin.equals(leftLower.value) && !support.isLower(leftLower.prev.value)) {
                    leftLower = leftLower.left != null ? leftLower.left : leftLower.prev;
                    support = new Line(leftLower.value, rightLower.value);
                    resFound = false;
                } else if (!rightMin.equals(rightLower.value) && support.isHigher(rightLower.prev.value)) {
                    rightLower = rightLower.left != null ? rightLower.left : rightLower.prev;
                    support = new Line(leftLower.value, rightLower.value);
                    resFound = false;
                } else if (!rightMax.equals(rightLower.value) && !support.isLower(rightLower.next.value)) {
                    rightLower = rightLower.right != null ? rightLower.right : rightLower.next;
                    support = new Line(leftLower.value, rightLower.value);
                    resFound = false;
                }
            }
        }

        List<VoronoiPoint> res = new ArrayList<>();
        res.add(leftLower == null ? null : leftLower.value);
        res.add(rightLower == null ? null : rightLower.value);
        return res;
    }

    public static ConvexHull merge(ConvexHull left, ConvexHull right) {
        List<VoronoiPoint> lowerSupport = ConvexHull.getLowerSupport(left, right);
        List<VoronoiPoint> upperSupport = ConvexHull.getUpperSupport(left, right);

        VoronoiPoint leftLower = lowerSupport.get(0);
        VoronoiPoint rightLower = lowerSupport.get(1);
        VoronoiPoint leftUpper = upperSupport.get(0);
        VoronoiPoint rightUpper = upperSupport.get(1);

        ConvexHull res = new ConvexHull();
        res.lowerHull = mergeLower(left.lowerHull, right.lowerHull, leftLower, rightLower);
        res.upperHull = mergeUpper(left.upperHull, right.upperHull, leftUpper, rightUpper);
        res.hull = mergeFull(left.hull, right.hull, leftLower, leftUpper, rightLower, rightUpper);
        res.hull.removeInterval(res.lowerHull.get(res.lowerHull.size() - 1), res.upperHull.get(0));
        res.hull.removeInterval(res.upperHull.get(res.upperHull.size() - 1), res.lowerHull.get(0));
        updateComparator(res.hull);

        return res;
    }

    private static AVLTree<VoronoiPoint> mergeLower(AVLTree<VoronoiPoint> leftLower, AVLTree<VoronoiPoint> rightLower,
                                                    VoronoiPoint leftSupport, VoronoiPoint rightSupport) {
        if (leftSupport != null && rightSupport != null && leftSupport.x == rightSupport.x) {
            if (leftSupport.y < rightSupport.y) {
                rightLower.remove(rightSupport);
            } else {
                leftLower.remove(leftSupport);
            }

            return AVLTree.join(leftLower, rightLower);
        }

        if (leftLower != null && !leftLower.isEmpty()) {
            leftLower.retainSegment(leftLower.get(0), leftSupport);
        }

        if (rightLower != null && !rightLower.isEmpty()) {
            rightLower.retainSegment(rightSupport, rightLower.get(rightLower.size() - 1));
        }

        return AVLTree.join(leftLower, rightLower);
    }

    private static AVLTree<VoronoiPoint> mergeUpper(AVLTree<VoronoiPoint> leftUpper, AVLTree<VoronoiPoint> rightUpper,
                                                    VoronoiPoint leftSupport, VoronoiPoint rightSupport) {
        if (leftSupport != null && rightSupport != null && leftSupport.x == rightSupport.x) {
            if (leftSupport.y > rightSupport.y) {
                rightUpper.remove(rightSupport);
            } else {
                leftUpper.remove(leftSupport);
            }

            return AVLTree.join(rightUpper, leftUpper);
        }

        if (rightUpper != null && !rightUpper.isEmpty()) {
            rightUpper.retainSegment(rightUpper.get(0), rightSupport);
        }

        if (leftUpper != null && !leftUpper.isEmpty()) {
            leftUpper.retainSegment(leftSupport, leftUpper.get(leftUpper.size() - 1));
        }

        return AVLTree.join(rightUpper, leftUpper);
    }

    private static AVLTree<VoronoiPoint> mergeFull(AVLTree<VoronoiPoint> left, AVLTree<VoronoiPoint> right,
                                                   VoronoiPoint leftLowerSupport, VoronoiPoint leftUpperSupport,
                                                   VoronoiPoint rightLowerSupport, VoronoiPoint rightUpperSupport) {
        if (left != null) {
            left.retainSegment(leftUpperSupport, leftLowerSupport);
        }

        if (right != null) {
            right.retainSegment(rightLowerSupport, rightUpperSupport);
        }

        AVLTree<VoronoiPoint> res = AVLTree.join(left, right);
        updateComparator(res);

        return res;
    }

    private void updateComparator() {
        updateComparator(this.hull);
    }

    private static void updateComparator(AVLTree<VoronoiPoint> hull) {
        if (hull.isEmpty()) {
            hull.setComparator(new PolarAngleComparator());
        } else if (hull.size() == 1) {
            hull.setComparator(new PolarAngleComparator(hull.get(0)));
        } else {
            hull.setComparator(new PolarAngleComparator(hull.get(0), hull.get(1)));
        }
    }

    public VoronoiPoint get(int index) {
        return hull.get(index);
    }

    public AVLNode<VoronoiPoint> find(AVLNode<VoronoiPoint> p, VoronoiPoint value) {
        return hull.find(p, value);
    }

    public AVLNode<VoronoiPoint> find(VoronoiPoint value) {
        return hull.find(hull.getHead(), value);
    }

    public AVLNode<VoronoiPoint> getHead() {
        return hull.getHead();
    }

    @Override
    public int size() {
        return hull.size();
    }

    @Override
    public Iterator<VoronoiPoint> iterator() {
        return hull.iterator();
    }

    @Override
    public boolean contains(Object o) {
        return hull.contains(o);
    }

    @Override
    public boolean remove(Object o) {
        lowerHull.remove(o);
        upperHull.remove(o);

        boolean res = hull.remove(o);
        updateComparator();

        return res;
    }

    @Override
    public boolean add(VoronoiPoint voronoiPoint) {
        return false;
    }

    public int getNextClockwiseIndex(int i) {
        return i == 0 ? this.size() - 1 : i - 1;
    }

    public int getNextAnticlockwiseIndex(int i) {
        return i == this.size() - 1 ? 0 : i + 1;
    }

}
