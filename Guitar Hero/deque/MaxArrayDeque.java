package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }

    public T max() {
        return (T) max(comparator);
    }

    public T max(Comparator<T> c) {
        if (size() == 0) {
            return null;
        }
        T returnValue = (T) this.get(0);
        for (int i = 0; i < size(); i++) {
            if (c.compare(returnValue, (T) this.get(i)) < 0) {
                returnValue = (T) this.get(i);
            }
        }
        return returnValue;
    }
}
