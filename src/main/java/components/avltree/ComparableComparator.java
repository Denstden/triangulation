package components.avltree;

import java.util.Comparator;

public class ComparableComparator<E> implements Comparator<E> {
    public ComparableComparator() { }

    @Override
    public int compare(E o1, E o2) {
        if (o1 == null || o2 == null) {
            throw new NullPointerException();
        }

        @SuppressWarnings("unchecked")
        int res = ((Comparable<E>) o1).compareTo(o2);
        return res;
    }
}
