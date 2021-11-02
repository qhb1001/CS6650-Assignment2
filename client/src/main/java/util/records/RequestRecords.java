package util.records;

import util.Timer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Maintains a list of request records with the response data.
 */
public class RequestRecords {
    List<RequestRecord> requestRecordList;

    /**
     * Initializes the request records object by allocating memory for the internal data structure.
     */
    public RequestRecords() {
        requestRecordList = new LinkedList<>();
    }

    /**
     * Add a request record to the list.
     * @param requestRecord the record to add
     */
    public void addRequestRecord(RequestRecord requestRecord) {
        requestRecordList.add(requestRecord);
    }

    /**
     * Add a list of request records to the current list.
     * @param requestRecords the records list to add
     */
    public void addRequestRecords(RequestRecords requestRecords) {
        this.requestRecordList.addAll(requestRecords.requestRecordList);
    }

    /**
     * Get the response time that is larger than 99-th percentile of the response time.
     * @return 99-th percentile response time
     */
    public long get99thPercentile() {
        Collections.sort(requestRecordList);
        int size = requestRecordList.size();
        return requestRecordList.get((int) (size * 0.99)).getTimer().getElapsedTimeInMillisecond();
    }

    /**
     * Get the mean response time from the list of records.
     * @return the mean response time.
     */
    public long getMeanResponseTime() {
        long responseTime = 0;
        for (RequestRecord requestRecord: requestRecordList) {
            responseTime += requestRecord.getTimer().getElapsedTimeInMillisecond();
        }

        return responseTime / requestRecordList.size();
    }

    /**
     * Get the median response time from the list of records.
     * @return the median response time.
     */
    public long getMedianResponseTime() {
        Collections.sort(requestRecordList);
        int size = requestRecordList.size();
        return requestRecordList.get(size / 2).getTimer().getElapsedTimeInMillisecond();
    }

    /**
     * Get the maximum response time from the list of records.
     * @return the maximum response time.
     */
    public long getMaximumResponseTime() {
        long max = 0;
        for (RequestRecord requestRecord: requestRecordList) {
            max = Math.max(max, requestRecord.getTimer().getElapsedTimeInMillisecond());
        }
        return max;
    }

    private String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    private String convertToCSV(String[] data) {
        return Stream.of(data)
            .map(this::escapeSpecialCharacters)
            .collect(Collectors.joining(","));
    }

    private List<String[]> getDataLines() {
        List<String[]> dataLines = new LinkedList<>();
        long start = requestRecordList.get(0).getTimer().getStart();
        for (RequestRecord requestRecord: requestRecordList) {
            Timer timer = requestRecord.getTimer();
            dataLines.add(new String[]{String.valueOf(timer.getStart() - start), String.valueOf(timer.getElapsedTimeInMillisecond())});
        }

        return dataLines;
    }

    /**
     * Write the data in RequestRecords to the given CSV file.
     * @param filePath the path to the file
     */
    public void writeToCSV(String filePath) {
        File csvOutputFile = new File(filePath);
        List<String[]> dataLines = getDataLines();
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                .map(this::convertToCSV)
                .forEach(pw::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
