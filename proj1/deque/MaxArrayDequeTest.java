package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {

    static class IntMax implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            if (o2 > o1) {
                return -1;
            }
            return 0;
        }
    }

    static class StrMax implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            if (o2.length() > o1.length()) {
                return -1;
            }
            return 0;
        }
    }

    static Comparator<Integer> intComparator = new IntMax();

    static MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(intComparator);

    @Test
    public void testIntMaxArrayDequeTest() {
        mad.addFirst(5);
        mad.addFirst(9);
        mad.addLast(100);
        mad.max();
        assertEquals((Integer) 100, mad.max());
    }


    static Comparator<String> strComparator = new StrMax();

    static MaxArrayDeque<String> madStr = new MaxArrayDeque<>(strComparator);

    @Test
    public void testStrMaxArrayDequeTest() {
        madStr.addFirst("Hello");
        madStr.addFirst("Iasdjnwkjadnk");
        madStr.addLast("a");
        madStr.max();
        assertEquals((String) "Iasdjnwkjadnk", madStr.max());
    }
}
