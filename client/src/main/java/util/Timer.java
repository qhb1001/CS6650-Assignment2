package util;

/**
 * Timer to record the start time, end time and the elapsed time.
 */
public class Timer {
    private long start, end;

    /**
     * Set the start time of the timer.
     */
    public void start() {
        start = System.currentTimeMillis();
    }

    /**
     * Set the end time of the timer.
     */
    public void end() {
        end = System.currentTimeMillis();
    }

    /**
     * Get the elapsed time in second.
     * @return the elapsed time in timer.
     */
    public double getElapsedTimeInSecond() {
        return (double) (end - start) / 1000;
    }

    /**
     * Get the elapsed time in millisecond.
     * @return the elapsed time in timer.
     */
    public long getElapsedTimeInMillisecond() {
        return end - start;
    }

    /**
     * Get the start of timer.
     * @return the start timestamp
     */
    public long getStart() {
        return start;
    }
}
