package util.range;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represent a range of values.
 */
public class Range {
    private final int start;
    private final int end;

    /**
     * Initialize the range with the given boundary.
     * @param start the start of range
     * @param end the end of range
     */
    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Get a random value for the given range in the format of string.
     * @return a random value in the range.
     */
    public String getRandomValue() {
        int value = ThreadLocalRandom.current().nextInt(start, end + 1);
        return String.valueOf(value);
    }

    /**
     * Get the size of the range.
     * @return the size of the range
     */
    public int size() {
        return end - start + 1;
    }

    @Override
    public String toString() {
        return String.format("[%d, %d]", start, end);
    }
}
