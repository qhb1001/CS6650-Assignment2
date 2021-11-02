package phase.listener;


/**
 * The listener for next phase. It will automatically execute the next phase
 * when the internal counter reaches zero.
 */
public interface PhaseListener {
    /**
     * Decrease the counter by one. Execute the next phase when the internal counter reaches zero.
     */
    void countDown();

    /**
     * Check if the next phase has started or not.
     * @return a bool to indicate whether the next phase has started or not.
     */
    boolean hasNext();
}
