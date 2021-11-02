package phase;

import phase.listener.PhaseListener;
import phase.listener.PhaseListenerImpl;
import phase.thread.PhaseThread;
import phase.thread.PhaseThreadImpl;
import util.range.DisjointRangeGenerator;
import util.range.Range;
import util.records.RequestRecords;

import java.util.LinkedList;
import java.util.List;

/**
 * The basic phase implementation to capture the requirement for the resort-skier simulation.
 */
public class PhaseImpl implements Phase {
    private final String phaseName;
    private final int THREAD_NUM;
    private final int REQUEST_PER_THREAD;
    private final int numSkiers;
    private final Range liftRange;
    private final Range timeRange;
    private PhaseListener phaseListener;
    private List<PhaseThread> threads;
    private String url;
    private static final String root = "server_war";

    /**
     * Initialize the Phase object by configuring the necessary parameters.
     * @param phaseName name of the phase
     * @param threadsNum number of threads
     * @param requestPerThread number of POST request every thread should send
     * @param skierNum number of skiers
     * @param liftNum number of lifts
     * @param timeStart start time
     * @param timeEnd end time
     */
    public PhaseImpl(String phaseName, int threadsNum, int requestPerThread, int skierNum, int liftNum, int timeStart,
                     int timeEnd, String ipAddress, String port) {
        if (threadsNum <= 0) {
            throw new IllegalArgumentException("The number of threads is invalid");
        }
        if (requestPerThread <= 0) {
            throw new IllegalArgumentException("The number of requests to send per thread " +
                "should be larger than zero");
        }
        if (skierNum < threadsNum || skierNum > 100000) {
            throw new IllegalArgumentException("The number of skiers is invalid");
        }
        if (timeStart > timeEnd || timeStart <= 0) {
            throw new IllegalArgumentException("The given time duration is invalid");
        }

        System.out.printf("One phase is initialized with parameters threadsNum: %d, " +
            "requestPerThread: %d, skierNum: %d, liftNum: %d, timeStart: %d, timeEnd: %d, " +
            "ip address: %s, port: %s\n", threadsNum, requestPerThread, skierNum, liftNum, timeStart,
            timeEnd, ipAddress, port);

        this.phaseName = phaseName;
        this.THREAD_NUM = threadsNum;
        this.REQUEST_PER_THREAD = requestPerThread;
        this.numSkiers = skierNum;
        this.liftRange = new Range(1, liftNum);
        this.timeRange = new Range(timeStart, timeEnd);
        this.phaseListener = null;
        this.threads = new LinkedList<>();
        this.url = String.format("http://%s:%s/%s", ipAddress, port, root) + "/skiers/11/seasons/22/days/33/skiers/%s";
    }

    @Override
    public void addNextPhase(Phase phase, double percentage) {
        if (percentage <= 0 || percentage > 1) {
            throw new IllegalArgumentException("The given percentage is invalid. It should be " +
                "in the range of (0, 1].");
        }
        if (phase == null) {
            throw new IllegalArgumentException("The given phase should not be null");
        }
        int counter = (int) Math.ceil(THREAD_NUM * percentage);
        this.phaseListener = new PhaseListenerImpl(phase, counter);
    }

    @Override
    public void start() {
        DisjointRangeGenerator disjointRangeGenerator = new DisjointRangeGenerator(numSkiers, THREAD_NUM);
        for (int i = 0; i < THREAD_NUM; i++) {
            if (!disjointRangeGenerator.hasNext()) {
                throw new IllegalArgumentException("The disjoint range generator is not working");
            }
            Range skierIDRange = disjointRangeGenerator.next();
            PhaseThread thread = new PhaseThreadImpl(REQUEST_PER_THREAD, skierIDRange, timeRange,
                liftRange, url, phaseListener, phaseName, i);
            threads.add(thread);
            thread.start();
        }
    }

    @Override
    public void join() throws InterruptedException {
        for (PhaseThread thread: threads) {
            thread.join();
        }
    }

    @Override
    public int getNumberOfSuccess() {
        int success = 0;
        for (PhaseThread thread: threads) {
            success += thread.getSuccessfulRequestCount();
        }
        return success;
    }

    @Override
    public int getNumberOfFailure() {
        int failure = 0;
        for (PhaseThread thread: threads) {
            failure += thread.getFailedRequestCount();
        }
        return failure;
    }

    @Override
    public RequestRecords getRequestRecords() {
        RequestRecords requestRecords = new RequestRecords();
        for (PhaseThread thread: threads) {
            RequestRecords threadRequestRecords = thread.getRequestRecords();
            requestRecords.addRequestRecords(threadRequestRecords);
        }
        return requestRecords;
    }


}
