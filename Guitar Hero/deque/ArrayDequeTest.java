package deque;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ArrayDequeTest {
    static Deque<Integer> ad = new ArrayDeque<Integer>();
    @Test

    public void addIsEmptySizeTest() {
        assertTrue("A newly initialized ADeque should be empty", ad.isEmpty());
        ad.addFirst(0);

        assertFalse("ad should now contain 1 item", ad.isEmpty());

        assertEquals(1, ad.size());

        ad = new ArrayDeque<>();
    }

    @Test
    public void adAddFirstTest() {
        assertEquals(0, ad.size());
        assertNull("Nothing exists in the AD, should return null", ad.get(1));
        ad.addFirst(1);
        assertEquals(1, ad.size());
        ad.printDeque(); // Should show 1
        assertEquals(1, (int) ad.get(0));
        ad = new ArrayDeque<>();
    }

    @Test
    public void adAddLastTest() {
        assertEquals(0, ad.size());
        assertNull("Nothing exists in the AD, should return null", ad.get(1));

        ad.addFirst(1);
        ad.addLast(2);
        assertEquals(2, ad.size());
        assertEquals(1, (int) ad.get(0));
        assertEquals(2, (int) ad.get(1));
        ad.printDeque(); // Should show 1 2

        ad = new ArrayDeque<>();
    }

    @Test
    public void adRemoveFirstTest() {
        assertEquals(0, ad.size());
        assertNull("Nothing exists in the AD, should return null", ad.get(1));

        // Check if negative
        assertNull(ad.removeLast());
        assertEquals(0, ad.size());

        ad.addFirst(1);
        ad.addLast(2);
        assertEquals(2, ad.size());
        ad.printDeque();
        assertEquals(1, (int) ad.removeFirst());
        assertEquals(1, ad.size());
        assertEquals(2, (int) ad.get(0));
        ad.printDeque(); // Should show 2

        assertEquals(2, (int) ad.removeFirst());
        assertEquals(0, ad.size());
        assertNull(ad.get(0));
        assertTrue("AD with no items should be empty", ad.isEmpty());

        ad = new ArrayDeque<>();
    }

    @Test
    public void adRemoveLastTest() {
        assertEquals(0, ad.size());
        assertNull("Nothing exists in the AD, should return null", ad.get(1));

        // Check if negative
        assertNull(ad.removeLast());
        assertEquals(0, ad.size());


        ad.addFirst(1);
        ad.addLast(2);
        assertEquals(2, ad.size());
        ad.printDeque();
        assertEquals(2, (int) ad.removeLast());
        assertEquals(1, ad.size());
        assertEquals(1, (int) ad.get(0));
        ad.printDeque(); // Should show 1

        assertEquals(1, (int) ad.removeLast());
        assertEquals(0, ad.size());
        assertNull(ad.get(0));
        assertTrue("AD with no items should be empty", ad.isEmpty());

        ad = new ArrayDeque<>();
    }

    @Test
    public void adGetTest() {
        assertEquals(0, ad.size());
        assertNull("Nothing exists in the AD, should return null", ad.get(1));

        ad.addFirst(1);
        ad.addLast(2);
        assertEquals(2, ad.size());
        ad.printDeque();
        assertEquals(1, (int) ad.get(0));
        ad.get(1);
        assertEquals(2, (int) ad.get(1));
        ad.printDeque(); 

        assertEquals(2, ad.size());

        assertEquals(1, (int) ad.removeFirst());
        assertEquals(1, ad.size());
        assertEquals(2, (int) ad.get(0));
        ad.printDeque(); // Should show 2

        assertEquals(2, (int) ad.removeFirst());
        assertEquals(0, ad.size());
        assertNull(ad.get(0));
        assertTrue("AD with no items should be empty", ad.isEmpty());

      
        ad = new ArrayDeque<>();
    }

    @Test
    public void equalsTest() {
        ArrayDeque<Integer> adTest = new ArrayDeque<Integer>();
        assertTrue("A newly initialized ADeque should be empty", adTest.isEmpty());
        assertTrue("ad should be reset at the end of the previous test", ad.isEmpty());

        adTest.addFirst(2);
        adTest.addLast(1);
        adTest.printDeque(); // Should show 2 1

        ad.addFirst(1);
        ad.addLast(2);
        ad.printDeque(); // Should show 1 2

        assertFalse("adTest and ad modified; no longer equal", ad.equals(adTest));
        assertEquals(2, (int) adTest.removeFirst());
        assertEquals(2, (int) ad.removeLast());
        assertTrue("Differing elements  removed; should now equal", ad.equals(adTest));

        ad = new ArrayDeque<Integer>();
    }

    @Test
    public void equalsStrTest() {
        ArrayDeque<String> adTest = new ArrayDeque<>();
        ArrayDeque<String> adStrTest = new ArrayDeque<>();
        assertTrue("A newly initialized LLDeque should be empty", adTest.isEmpty());
        assertTrue("lld should be reset at the end of the previous test", adStrTest.isEmpty());

        adTest.addFirst("String");
        adTest.addLast("Different");
        adTest.printDeque();

        adStrTest.addFirst("Different");
        adStrTest.addLast("String");
        adStrTest.printDeque(); // Should show 1 2

        assertFalse("lldTest and lld modified; no longer equal", adStrTest.equals(adTest));
        assertEquals("String", adTest.removeFirst());
        assertEquals("String", adStrTest.removeLast());
        assertTrue("Differing elements removed; should now equal", adStrTest.equals(adTest));

        ad = new ArrayDeque<>();
    }

    @Test
    public void adLongerTest() {
        ad.addFirst(7);
        ad.addFirst(5);
        ad.addFirst(3);
        ad.addFirst(1);
        ad.addLast(2);
        ad.addLast(4);
        ad.addLast(6);
        ad.addLast(8);
        ad.printDeque(); // Should show 1 3 5 7 2 4 6 8

        ArrayDeque<Integer> adTest = new ArrayDeque<Integer>();
        adTest.addLast(2);
        adTest.addLast(4);
        adTest.addLast(6);
        adTest.addLast(8);
        adTest.addFirst(7);
        adTest.addFirst(5);
        adTest.addFirst(3);
        adTest.addFirst(1);
        adTest.printDeque(); // Should also show 1 3 5 7 2 4 6 8

        ad.equals(adTest);
        assertTrue("Element order should be the same", ad.equals(adTest));

        ad = new ArrayDeque<>();

        ArrayDeque<Integer> adInterweave = new ArrayDeque<Integer>();
        adInterweave.addLast(0);
        adInterweave.addFirst(1);
        adInterweave.addLast(2);
        adInterweave.addFirst(3);
        adInterweave.addLast(4);
        adInterweave.addFirst(5);
        adInterweave.addLast(6);
        adInterweave.addFirst(7);
        adInterweave.printDeque(); // Should show 7 5 3 1 0 2 4 6

        ad.addFirst(1);
        ad.addFirst(3);
        ad.addFirst(5);
        ad.addFirst(7);
        ad.addLast(0);
        ad.addLast(2);
        ad.addLast(4);
        ad.addLast(6);
        ad.printDeque(); // Should also show 7 5 3 1 0 2 4 6

        assertTrue("Interweave and linear should be the same", ad.equals(adInterweave));

        ad = new ArrayDeque<>();
    }

    @Test
    public void resizerTest() {
        // Adding and resizing
        for (int i = 0; i < 64; i++) {
            ad.addFirst(i);
        }
        assertEquals(64, ad.size());

        // Removing and downsizing
        for (int i = 0; i < 64; i++) {
            assertEquals((Integer) i, ad.removeLast());
        }
        assertEquals(0, ad.size());

        // Adding and resizing
        for (int i = 0; i < 64; i++) {
            ad.addLast(1);
        }
        assertEquals(64, ad.size());

        // Removing and downsizing
        for (int i = 0; i < 64; i++) {
            ad.removeFirst();
        }
        assertEquals(0, ad.size());

        for (int i = 0; i < 32; i++) {
            ad.addFirst(i);
            ad.addLast(i);
        }
        assertEquals(64, ad.size());

        // Removing and downsizing
        for (int i = 0; i < 16; i++) {
            ad.removeFirst();
            ad.removeLast();
        }
        assertEquals(32, ad.size());

        ad = new ArrayDeque<>();
    }
}
