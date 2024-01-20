package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;



public class LinkedListDequeTest {


    static Deque<Integer> lld = new LinkedListDeque<Integer>();

    @Test

    public void addIsEmptySizeTest() {
        assertTrue("A newly initialized LLDeque should be empty", lld.isEmpty());
        lld.addFirst(0);

        assertFalse("lld should now contain 1 item", lld.isEmpty());

        assertEquals(1, lld.size());

        lld = new LinkedListDeque<>();
    }

    @Test
    public void lldAddFirstTest() {
        assertEquals(0, lld.size());
        assertNull("Nothing exists in the LLD, should return null", lld.get(1));

        lld.addFirst(1);
        lld.addFirst(2);
        assertEquals(2, lld.size());
        assertEquals(2, (int) lld.get(0));
        assertEquals(1, (int) lld.get(1));
        lld.printDeque(); // Should show 2 1

        assertEquals(2, lld.size());

        lld = new LinkedListDeque<>();
    }

    @Test
    public void lldAddLastTest() {
        assertEquals(0, lld.size());
        assertNull("Nothing exists in the LLD, should return null", lld.get(1));

        lld.addLast(1);
        lld.addLast(2);
        assertEquals(2, lld.size());
        assertEquals(1, (int) lld.get(0));
        assertEquals(2, (int) lld.get(1));
        lld.printDeque(); // Should show 1 2

        assertEquals(2, lld.size());

        
        lld = new LinkedListDeque<>();
    }

    @Test
    public void lldRemoveFirstTest() {
        assertEquals(0, lld.size());
        assertNull("Nothing exists in the LLD, should return null", lld.get(1));

        // Check if negative
        assertNull(lld.removeFirst());
        assertEquals(0, lld.size());

        lld.addLast(1);
        lld.addLast(2);
        assertEquals(2, lld.size());
        assertEquals(1, (int) lld.get(0));
        assertEquals(2, (int) lld.get(1));
        lld.printDeque(); // Should show 1 2

        assertEquals(2, lld.size());

        assertEquals(1, (int) lld.removeFirst());
        assertEquals(1, lld.size());
        assertEquals(2, (int) lld.get(0));
        lld.printDeque(); // Should show 2

        assertEquals(2, (int) lld.removeFirst());
        assertEquals(0, lld.size());
        assertNull(lld.get(0));
        assertTrue("LLDeque with no items should be empty", lld.isEmpty());

        lld = new LinkedListDeque<>();
    }

    @Test
    public void lldRemoveLastTest() {
        assertEquals(0, lld.size());
        assertNull("Nothing exists in the LLD, should return null", lld.get(1));

        assertNull(lld.removeLast());
        assertEquals(0, lld.size());

        lld.addLast(1);
        lld.addLast(2);
        assertEquals(2, lld.size());
        assertEquals(1, (int) lld.get(0));
        assertEquals(2, (int) lld.get(1));
        lld.printDeque(); // Should show 1 2

        // Checks get method doesn't modify the deque.
        assertEquals(2, lld.size());

        assertEquals(2, (int) lld.removeLast());
        assertEquals(1, lld.size());
        assertEquals(1, (int) lld.get(0));
        lld.printDeque(); // Should show 1

        assertEquals(1, (int) lld.removeLast());
        assertEquals(0, lld.size());
        assertNull(lld.get(0));
        assertTrue("LLDeque with no items should be empty", lld.isEmpty());

        lld = new LinkedListDeque<>();
    }

    @Test
    public void equalsTest() {
        LinkedListDeque<Integer> lldTest = new LinkedListDeque<Integer>();
        assertTrue("A newly initialized LLDeque should be empty", lldTest.isEmpty());
        assertTrue("lld should be reset at the end of the previous test", lld.isEmpty());

        lldTest.addFirst(2);
        lldTest.addLast(1);
        lldTest.printDeque(); // Should show 2 1

        lld.addFirst(1);
        lld.addLast(2);
        lld.printDeque(); // Should show 1 2

        assertFalse("lldTest and lld modified; no longer equal", lld.equals(lldTest));
        assertEquals(2, (int) lldTest.removeFirst());
        assertEquals(2, (int) lld.removeLast());
        assertTrue("Differing elements removed; should now equal", lld.equals(lldTest));

        lld = new LinkedListDeque<>();
    }

    @Test
    public void equalsStrTest() {
        LinkedListDeque<String> lldTest = new LinkedListDeque<>();
        LinkedListDeque<String> lldStrTest = new LinkedListDeque<>();
        assertTrue("A newly initialized LLDeque should be empty", lldTest.isEmpty());
        assertTrue("lld should be reset at the end of the previous test", lldStrTest.isEmpty());

        lldTest.addFirst("String");
        lldTest.addLast("Different");
        lldTest.printDeque();

        lldStrTest.addFirst("Different");
        lldStrTest.addLast("String");
        lldStrTest.printDeque(); // Should show 1 2

        assertFalse("lldTest and lld modified; no longer equal", lldStrTest.equals(lldTest));
        assertEquals("String", lldTest.removeFirst());
        assertEquals("String", lldStrTest.removeLast());
        assertTrue("Differing elements removed; should now equal", lldStrTest.equals(lldTest));

        lld = new LinkedListDeque<>();
    }

    @Test
    public void lldLongerTest() {
        lld.addFirst(7);
        lld.addFirst(5);
        lld.addFirst(3);
        lld.addFirst(1);
        lld.addLast(2);
        lld.addLast(4);
        lld.addLast(6);
        lld.addLast(8);
        lld.printDeque(); // Should show 1 3 5 7 2 4 6 8
        assertEquals(2, (int) lld.get(4));

        LinkedListDeque<Integer> lldTest = new LinkedListDeque<Integer>();
        lldTest.addLast(2);
        lldTest.addLast(4);
        lldTest.addLast(6);
        lldTest.addLast(8);
        lldTest.addFirst(7);
        lldTest.addFirst(5);
        lldTest.addFirst(3);
        lldTest.addFirst(1);
        lldTest.printDeque(); // Should also show 1 3 5 7 2 4 6 8
        assertEquals(2, (int) lldTest.get(4));

        lld.equals(lldTest);
        assertTrue("Element order should be the same", lld.equals(lldTest));

        lld = new LinkedListDeque<>();

        LinkedListDeque<Integer> lldInterweave = new LinkedListDeque<Integer>();
        lldInterweave.addLast(0);
        lldInterweave.addFirst(1);
        lldInterweave.addLast(2);
        lldInterweave.addFirst(3);
        lldInterweave.addLast(4);
        lldInterweave.addFirst(5);
        lldInterweave.addLast(6);
        lldInterweave.addFirst(7);
        lldInterweave.printDeque(); // Should show 7 5 3 1 0 2 4 6
        assertEquals(7, (int) lldInterweave.get(0));

        lld.addFirst(1);
        lld.addFirst(3);
        lld.addFirst(5);
        lld.addFirst(7);
        lld.addLast(0);
        lld.addLast(2);
        lld.addLast(4);
        lld.addLast(6);
        lld.printDeque(); // Should also show 7 5 3 1 0 2 4 6
        assertEquals(7, (int) lld.get(0));

        assertTrue("Interweave and linear should be the same", lld.equals(lldInterweave));

    }

}
