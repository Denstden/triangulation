package components.avltree;


import java.util.*;

import static components.avltree.AVLNode.balance;
import static components.avltree.AVLNode.replaceSon;

public class AVLTree<E> extends AbstractCollection<E> implements Collection<E> {
    private Comparator<? super E> comparator;

    AVLNode<E> head;

    public AVLTree() {
        this.comparator = new ComparableComparator<>();
    }

    public AVLTree(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    public AVLTree(AVLNode<E> head) {
        this.comparator = new ComparableComparator<>();
        this.head = head;
    }

    public AVLTree(AVLNode<E> head, Comparator<? super E> comparator) {
        this.comparator = comparator;
        this.head = head;
    }

    public AVLTree(Collection<? extends E> c) {
        this.comparator = new ComparableComparator<>();
        this.addAll(c);
    }

    public AVLTree(Collection<? extends E> c, Comparator<? super E> comparator) {
        this.comparator = comparator;
        this.addAll(c);
    }

//    /*
    public AVLTree(AVLTree<E> tree) {
        this();
        if (tree.size() > 0) {
            HashMap<AVLNode<E>, AVLNode<E>> map = new HashMap<>();
            AVLNode<E> node, newNode;

            Queue<AVLNode<E>> queue = new LinkedList<>();
            queue.add(tree.head);
            while (!queue.isEmpty()) {
                node = queue.poll();
                newNode = new AVLNode<E>(node.value);
                map.put(node, newNode);

                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
            }

            for (Map.Entry<AVLNode<E>, AVLNode<E>> entry : map.entrySet()) {
                node = entry.getKey();
                newNode = entry.getValue();

                newNode.setChildrenCount(node.getChildrenCount());
                newNode.setHeight(node.getHeight());
                newNode.left = map.get(node.left);
                newNode.right = map.get(node.right);
                newNode.parent = map.get(node.parent);
                newNode.next = map.get(node.next);
                newNode.prev = map.get(node.prev);
//                System.out.println("trulala");
            }
            this.head = map.get(tree.head);
            this.comparator = tree.comparator;
        }
    }
//    */

    public void setHead(AVLNode<E> head) {
        this.head = head;
    }

    public AVLNode<E> getHead() {
        return head;
    }

    public Comparator<? super E> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    public int size() {
        return AVLNode.getNodeCount(head);
    }

    public static <E> AVLTree<E> join(AVLTree<E> left, AVLTree<E> right) {
        if (left == null) {
            return right != null ? right : new AVLTree<>();
        }

        if (right == null) {
            return left;
        }

        return new AVLTree<>(AVLNode.join(left.head, right.head), left.getComparator());
    }

    public void retainInterval(E min, E max, boolean minOpen, boolean maxOpen) {
        if (min == null || max == null) {
            throw new NullPointerException();
        }

        if (comparator.compare(min, max) > 0) {
            List<AVLNode<E>> maxList = split(head, max, !maxOpen);
            head = maxList.get(1);
            List<AVLNode<E>> minList = split(head, min, minOpen);
            head = AVLNode.join(minList.get(1), maxList.get(0));
            return;
        }

        List<AVLNode<E>> minList = split(head, min, minOpen);
        head = minList.get(1);
        List<AVLNode<E>> maxList = split(head, max, !maxOpen);
        head = maxList.get(0);
    }

    public void retainSegment(E min, E max) {
        this.retainInterval(min, max, false, false);
    }

    public void retainInterval(E min, E max) {
        this.retainInterval(min, max, true, true);
    }

    public void removeInterval(E min, E max, boolean minOpen, boolean maxOpen) {
        if (min == null || max == null) {
            throw new NullPointerException();
        }

        if (comparator.compare(min, max) > 0) {
            List<AVLNode<E>> maxList = split(head, max, !maxOpen);
            head = maxList.get(1);
            List<AVLNode<E>> minList = split(head, min, minOpen);
            head = minList.get(0);
            return;
        }

        List<AVLNode<E>> minList = split(head, min, minOpen);
        head = minList.get(1);
        List<AVLNode<E>> maxList = split(head, max, !maxOpen);
        head = AVLNode.join(minList.get(0), maxList.get(1));
    }

    public void removeSegment(E min, E max) {
        this.removeInterval(min, max, false, false);
    }

    public void removeInterval(E min, E max) {
        this.removeInterval(min, max, true, true);
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }

        try {
            @SuppressWarnings("unchecked")
            E e = (E) o;
            return find(head, e) != null;
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }

        try {
            @SuppressWarnings("unchecked")
            E e = (E) o;
            int prevSize = this.size();
            head = remove(head, e);
            return prevSize != this.size();
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean add(E e) {
        int prevSize = this.size();
        head = insert(head, e);
        return prevSize != this.size();
    }

    public E get(int index) {
        if (index < 0 || index >= this.size()) {
            throw new IndexOutOfBoundsException();
        }

        return AVLNode.get(head, index);
    }

    public AVLNode<E> insert(AVLNode<E> p, E value) {
        if (p == null) {
            return new AVLNode<>(value);
        }

        if (comparator.compare(value, p.value) < 0) {
            if (p.left != null) {
                p.left = insert(p.left, value);
            }
            else {
                AVLNode.setLeft(p, new AVLNode<>(value, null, null, p.prev, p));
            }
        } else {
            if (p.right != null) {
                p.right = insert(p.right, value);
            }
            else {
                AVLNode.setRight(p, new AVLNode<>(value, null, null, p, p.next));
            }
        }

        return balance(p);
    }

    public AVLNode<E> find(AVLNode<E> p, E value) {
        if (p == null) {
            return null;
        }

        int cmp = comparator.compare(value, p.value);
        if (cmp < 0) {
            return find(p.left, value);
        }
        else if (cmp > 0) {
            return find(p.right, value);
        }

        return p;
    }

    public AVLNode<E> remove(AVLNode<E> p, E value) {
        if (p == null) {
            return null;
        }

        int cmp = comparator.compare(value, p.value);
        if (cmp < 0) {
            p.left = remove(p.left, value);
        } else if (cmp > 0) {
            p.right = remove(p.right, value);
        } else {
            AVLNode<E> q = p.left;
            AVLNode<E> r = p.right;

            if (r == null) {
                replaceSon(p.parent, p, q);
                AVLNode.setNext(p.prev, p.next);
                return q;
            }

            AVLNode<E> min = AVLNode.findMin(r);
            replaceSon(p.parent, p, min);
            AVLNode.setNext(p.prev, p.next);
            AVLNode.setRight(min, AVLNode.removeMin(r));
            AVLNode.setLeft(min, q);
            return balance(min);
        }

        return balance(p);
    }

    public List<AVLNode<E>> split(AVLNode<E> head, E value, boolean valueLeft) {
        if (head == null) {
            List<AVLNode<E>> res = new ArrayList<>();
            res.add(null);
            res.add(null);
            return res;
        }

        AVLNode<E> min = AVLNode.findMin(head);
        AVLNode<E> max = AVLNode.findMax(head);
        AVLNode<E> left = null;
        AVLNode<E> right = null;
        AVLNode<E> node = head;
        while (node != null) {
            int cmp = comparator.compare(value, node.value);
            if (cmp < 0 || cmp == 0 && !valueLeft) {
                if (right == null) {
                    node.parent = null;
                }
                else {
                    AVLNode.setLeft(right, node);
                }

                right = node;
                node = node.left;
            }
            else {
                if (left == null) {
                    node.parent = null;
                }
                else {
                    AVLNode.setRight(left, node);
                }

                left = node;
                node = node.right;
            }
        }

        AVLNode.setNext(left, min);
        AVLNode.setPrev(right, max);

        AVLNode.setRight(left, null);
        AVLNode.setLeft(right, null);
        List<AVLNode<E>> res = new ArrayList<>();
        res.add(AVLNode.balanceBranch(left));
        res.add(AVLNode.balanceBranch(right));
        return res;
    }

    private class Itr implements Iterator<E> {
        AVLNode<E> start = AVLNode.findMin(head);
        AVLNode<E> next = start;

        int startingSize = AVLTree.this.size();
        int moveCount;

        boolean moved;

        @Override
        public boolean hasNext() {
            return moveCount < startingSize;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            next = next.next;
            ++moveCount;
            moved = true;
            return next.prev.value;
        }

        @Override
        public void remove() {
            if (!moved) {
                throw new IllegalStateException();
            }

            if (next != null) {
                head = AVLTree.this.remove(head, next.prev.value);
            }
            else {
                head = null;
            }

            moved = false;
        }
    }
}
