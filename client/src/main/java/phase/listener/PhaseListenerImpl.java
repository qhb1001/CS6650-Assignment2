package phase.listener;

import phase.Phase;

/**
 * The phase listener implementation with a synchronized counter.
 */
public class PhaseListenerImpl implements PhaseListener {
    private int counter;
    private Phase phase;

    /**
     * Initialized the listener with the given phase and counter.
     *
     * @param phase the phase to execute
     * @param counter the counter
     */
    public PhaseListenerImpl(Phase phase, int counter) {
        if (phase == null) {
            throw new IllegalArgumentException("The phase should not be null");
        }
        if (counter <= 0) {
            throw new IllegalArgumentException("The counter should be larger than zero");
        }
        this.phase = phase;
        this.counter = counter;
    }

    @Override
    public synchronized void countDown() {
        this.counter -= 1;
        if (this.counter == 0) {
            System.out.println("One phase is triggered to start the execution");
            this.phase.start();
        }
    }

    @Override
    public boolean hasNext() {
        return counter > 0;
    }
}
