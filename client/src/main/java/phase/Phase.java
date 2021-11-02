package phase;

import util.records.RequestRecords;

import java.util.concurrent.CountDownLatch;

/**
 * Represent the high-level phase for the client. Each phase has similar behavior like
 * being initialized by setting the parameters, starting the execution.
 */
public interface Phase {
    /**
     * Add the next phase for the current phase. The next phase will begin to execute when
     * $percentage$ of the threads in the current phase have terminated.
     * @param phase the next phase
     * @param percentage the percentage necessary to start the next phase
     */
    void addNextPhase(Phase phase, double percentage);

    /**
     * Start the phase.
     */
    void start();

    /**
     * Wait for all the threads to finish.
     * @throws InterruptedException
     */
    void join() throws InterruptedException;

    /**
     * Get the number of successful requests.
     * @return the number of successful requests.
     */
    int getNumberOfSuccess();

    /**
     * Get the number of failed requests.
     * @return the number of failed requests.
     */
    int getNumberOfFailure();

    /**
     * Get all the request records that the phase initialized.
     * @return the request records
     */
    RequestRecords getRequestRecords();
}
