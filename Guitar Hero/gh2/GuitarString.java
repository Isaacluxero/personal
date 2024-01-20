package gh2;

import deque.ArrayDeque;
import deque.Deque;

public class GuitarString {

    private static final int SR = 44100;      
    private static final double DECAY = .996; 

    private Deque<Double> buffer = new ArrayDeque<>();

    public GuitarString(double frequency) {
        int capacity = (int) Math.round(SR / frequency);
        for (int i = capacity; i > 0; i--) {
            buffer.addLast(0.0);
        }
    }

    public void pluck() {
        for (int i = buffer.size(); i > 0; i--) {
            double r = Math.random() - 0.5;
            buffer.removeFirst();
            buffer.addLast(r);
        }

    }


    public void tic() {
        buffer.addLast((buffer.get(0) + buffer.get(1)) / 2 * DECAY);
        buffer.removeFirst();
    }

    public double sample() {
        return buffer.get(0);
    }
}
