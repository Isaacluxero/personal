package deque;

public class ArrayDeque<T> implements Deque<T> {
    private int size;
    private T[] items;
    private int nextFirst;
    private int nextLast;
    private int arraySize = 8;

    public ArrayDeque() {
        size = 0;
        items = (T[]) new Object[arraySize];
        nextFirst = 7;
        nextLast = 0;
    }

    private void resizer(double scale) {
        arraySize *= scale;
        T[] newItems = (T[]) new Object[arraySize];
        if (scale < 1) {
            if (nextFirst > nextLast && nextFirst != items.length - 1) {
                System.arraycopy(items, nextFirst + 1, newItems, 0, items.length - nextFirst - 1);
                System.arraycopy(items, 0, newItems, items.length - nextFirst,
                        size - items.length + nextFirst + 1);
            } else {
                System.arraycopy(items, (nextFirst + 1) % (items.length), newItems, 0, size);
            }
        } else {
            if (nextFirst != items.length - 1) {
                System.arraycopy(items, nextFirst + 1, newItems, 0, size - nextFirst - 1);
                System.arraycopy(items, 0, newItems, size - nextLast, nextLast);
            } else {
                System.arraycopy(items, 0, newItems, 0, items.length);
            }
        }
        nextFirst = arraySize - 1;
        nextLast = size();
        items = newItems;
    }

    @Override
    public void addFirst(T item) {
        if (size == arraySize) {
            resizer(2);
        }
        items[nextFirst] = item;
        size++;
        if (nextFirst <= 0) {
            nextFirst = items.length - 1;
        } else {
            nextFirst--;
        }
    }

    @Override
    public void addLast(T item) {
        if (size == arraySize) {
            resizer(2);
        }
        items[nextLast] = item;
        size++;
        nextLast = (nextLast + 1) % items.length;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (size != other.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void printDeque() {
        for (int i = size; i > 0; i--) {
            int index = (nextFirst + i) % items.length;
            if (items[index] != null) {
                System.out.print(items[index]);
            }
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if (size <= arraySize / 4 && items.length > 8) {
            resizer(0.5);
        }
        nextFirst = (nextFirst + 1) % items.length;
        size--;
        return items[nextFirst];
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if (size <= arraySize / 4 && items.length > 8) {
            resizer(0.5);
        }
        if (nextLast <= 0) {
            nextLast = items.length - 1;
        } else {
            nextLast--;
        }
        size--;
        return items[nextLast];
    }

    @Override
    public T get(int index) {
        if (index > size) {
            return null;
        } else if (size == 0) {
            return null;
        }
        return items[(nextFirst + index + 1) % items.length];
    }
}
