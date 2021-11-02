package client2;

import phase.Phase;
import phase.PhaseImpl;
import util.Timer;
import util.records.RequestRecords;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int numThreads = Integer.parseInt(args[0]);
        int numSkiers = Integer.parseInt(args[1]);
        int numLifts = Integer.parseInt(args[2]);
        int numRuns = Integer.parseInt(args[3]);
        String ipAddress = args[4];
        String port = args[5];
        System.out.println(numThreads + " " + numSkiers + " " + numLifts + " " + numRuns + " " + ipAddress + " " + port);
        Phase phase1 = new PhaseImpl(
            "phase 1",
            numThreads / 4,
            (int) (numRuns * 0.2 * numSkiers / (numThreads / 4)),
            numSkiers,
            numLifts,
            1,
            90,
            ipAddress,
            port
        );

        Phase phase2 = new PhaseImpl(
            "phase 2",
            numThreads,
            (int) (numRuns * 0.6 * numSkiers / numThreads),
            numSkiers,
            numLifts,
            91,
            360,
            ipAddress,
            port
        );

        Phase phase3 = new PhaseImpl(
            "phase 3",
            numThreads / 4,
            (int) (numRuns * 0.1) * numSkiers / (numThreads / 4),
            numSkiers,
            numLifts,
            361,
            420,
            ipAddress,
            port
        );

        phase1.addNextPhase(phase2, 0.1);
        phase2.addNextPhase(phase3, 0.1);

        Timer timer = new Timer();
        timer.start();
        phase1.start();
        phase1.join();
        phase2.join();
        phase3.join();
        timer.end();

        RequestRecords requestRecords = new RequestRecords();
        requestRecords.addRequestRecords(phase1.getRequestRecords());
        requestRecords.addRequestRecords(phase2.getRequestRecords());
        requestRecords.addRequestRecords(phase3.getRequestRecords());
        int success = phase1.getNumberOfSuccess() + phase2.getNumberOfSuccess() + phase3.getNumberOfSuccess();
        System.out.println("Wall time (second): " + timer.getElapsedTimeInSecond());
        System.out.println("Mean response time (milliseconds): " + requestRecords.getMeanResponseTime());
        System.out.println("Median response time (milliseconds): " + requestRecords.getMedianResponseTime());
        System.out.printf("The total throughput in requests per second: %f\n", success / timer.getElapsedTimeInSecond());
        System.out.println("p99 (99th percentile) response time (milliseconds): " + requestRecords.get99thPercentile());
        System.out.println("Maximum response time (milliseconds): " + requestRecords.getMaximumResponseTime());
        requestRecords.writeToCSV("request_records.csv");
    }
}
