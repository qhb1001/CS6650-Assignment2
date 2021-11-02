package util.records;

import util.Timer;

import java.util.Comparator;

public class RequestRecord implements Comparable<RequestRecord> {
    private Timer timer;
    private int statusCode;

    public RequestRecord() {
        timer = new Timer();
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void start() {
        timer.start();
    }

    public void end() {
        timer.end();
    }

    public Timer getTimer() {
        return timer;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public int compareTo(RequestRecord o) {
        if (timer.getElapsedTimeInMillisecond() < o.getTimer().getElapsedTimeInMillisecond()) {
            return -1;
        } else if (timer.getElapsedTimeInMillisecond() == o.timer.getElapsedTimeInMillisecond()) {
            return 0;
        } else {
            return 1;
        }
    }
}
