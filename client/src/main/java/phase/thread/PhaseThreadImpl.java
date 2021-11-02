package phase.thread;

import com.google.gson.Gson;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import phase.listener.PhaseListener;
import util.range.Range;
import util.records.RequestRecord;
import util.records.RequestRecords;
import util.request.SkierPostRequestBody;

import java.io.IOException;
import java.util.Arrays;

/**
 * Implementation for the phase thread with sample statistic function.
 * It will calculate the response time, number of successful requests, number of failed requests.
 */
public class PhaseThreadImpl extends Thread implements PhaseThread {
    private String phaseName;
    private int index;
    private int success, failure;
    private final int numberOfRequest;
    private Range skierID, timestamp, liftNumber;
    private String url;
    private RequestRecords requestRecords;
    private PhaseListener phaseListener;
    private HttpClient client;

    /**
     * Initialize the phase thread by setting the number of POST requests to send,
     * range of skier ID, range of the timestamp, range of lift number,
     * resort ID, season, day ID.
     */
    public PhaseThreadImpl(int numberOfRequest, Range skierID, Range timestamp, Range liftNumber,
                           String url, PhaseListener phaseListener, String phaseName, int index) {
        this.numberOfRequest = numberOfRequest;
        this.skierID = skierID;
        this.timestamp = timestamp;
        this.liftNumber = liftNumber;
        this.url = url;
        this.requestRecords = new RequestRecords();
        this.phaseListener = phaseListener;
        this.phaseName = phaseName;
        this.index = index;
        this.client = new HttpClient();
    }

    private void sendPostRequest() {
        RequestRecord requestRecord = new RequestRecord();
        requestRecord.start();
        url = String.format(url, skierID.getRandomValue());
        PostMethod method = new PostMethod(url);
        HttpMethodRetryHandler httpMethodRetryHandler = new DefaultHttpMethodRetryHandler(5, false);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, httpMethodRetryHandler);

        try {
            SkierPostRequestBody requestBody = new SkierPostRequestBody(
                Integer.parseInt(timestamp.getRandomValue()),
                Integer.parseInt(liftNumber.getRandomValue())
            );

            StringRequestEntity requestEntity = new StringRequestEntity(
                new Gson().toJson(requestBody),
                "application/json",
                "UTF-8");

            method.setRequestEntity(requestEntity);

            int statusCode = client.executeMethod(method);
            requestRecord.setStatusCode(statusCode);

            if (statusCode != HttpStatus.SC_OK) {
                System.out.println("Method failed: " + method.getStatusLine());
                requestRecord.end();
                requestRecords.addRequestRecord(requestRecord);
                failure += 1;
                return ;
            }

            requestRecord.end();
            requestRecords.addRequestRecord(requestRecord);
            success += 1;
        } catch (HttpException e) {
            System.out.println("Fatal protocol violation: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
    }

    @Override
    public void run() {
        System.out.printf("The %d-th thread in %s starts running.\n", index + 1, phaseName);
        for (int i = 0; i < numberOfRequest; i++) {
            sendPostRequest();
        }

        System.out.printf("The %d-th thread in %s is completed.\n", index + 1, phaseName);

        if (phaseListener != null && phaseListener.hasNext()) {
            phaseListener.countDown();
        }
    }

    @Override
    public RequestRecords getRequestRecords() {
        return requestRecords;
    }

    @Override
    public int getSuccessfulRequestCount() {
        return success;
    }

    @Override
    public int getFailedRequestCount() {
        return failure;
    }
}
