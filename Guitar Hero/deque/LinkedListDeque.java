package deque;

public class LinkedListDeque<T> implements Deque<T> {
    private class LLDNode {
        private T item;
        private LLDNode prev;
        private LLDNode next;

        LLDNode(T item, LLDNode prev, LLDNode next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }

        @Override
        public String toString() {
            return item + "";
        }

    }

    private final LLDNode sentinel;
    private int size;

    public LinkedListDeque() {
        size = 0;
        sentinel = new LLDNode(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    @Override
    public void addFirst(T item) {
        sentinel.next = new LLDNode(item, sentinel, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        sentinel.prev = new LLDNode(item, sentinel.prev, sentinel);
        sentinel.prev.prev.next = sentinel.prev;
        size += 1;
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
        for (LLDNode l = sentinel.next; l != sentinel; l = l.next) {
            System.out.print(l.item + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T first = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;
        return first;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T last = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;
        return last;
    }

    @Override
    public T get(int index) {
        if (index > size) {
            return null;
        }
        LLDNode currNode = sentinel.next;
        for (; index > 0; index--) {
            currNode = currNode.next;
        }
        return currNode.item;
    }

    private T getRecursiveHelper(LLDNode node, int index) {
        LLDNode currNode = node;
        if (index == 0) {
            return currNode.item;
        }
        currNode = currNode.next;
        return getRecursiveHelper(currNode, --index);
    }

    public T getRecursive(int index) {
        if (index > size) {
            return null;
        } else if (sentinel.next == sentinel) {
            return null;
        }
        return getRecursiveHelper(sentinel.next, index);
    }
}
