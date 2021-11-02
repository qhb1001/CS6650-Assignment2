package util.range;

import java.util.Iterator;

public class DisjointRangeGenerator implements Iterator<Range> {
    private final int max;
    private int left;
    private final int interval;

    public DisjointRangeGenerator(int max, int segments) {
        this.max = max;
        this.left = 1;
        this.interval = max / segments;
    }

    @Override
    public boolean hasNext() {
        return left != max;
    }

    @Override
    public Range next() {
        Range range = new Range(left, Math.min(left + interval - 1, max));
        left = Math.min(left + interval, max);
        return range;
    }

    @Override
    public void remove() {
    }

}
