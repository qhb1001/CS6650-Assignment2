package phase.thread;

import util.records.RequestRecords;

public interface PhaseThread {
    void start();
    void join() throws InterruptedException;
    RequestRecords getRequestRecords();
    int getSuccessfulRequestCount();
    int getFailedRequestCount();
}
