package util.generator;

import resorts.Resort;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * The class to generate the dummy data for all kinds of object for resort.
 */
public class DummyDataGenerator {
    private static DummyDataGenerator dummyDataGenerator = null;

    private DummyDataGenerator() {}

    /**
     * Get the singleton object of dummy data generator.
     * @return the dummy data generator.
     */
    public static DummyDataGenerator getInstance() {
        if (dummyDataGenerator == null) {
            dummyDataGenerator = new DummyDataGenerator();
        }

        return dummyDataGenerator;
    }

    /**
     * Get a dummy resort object.
     * @return a dummy resort object.
     */
    public Resort getResort() {
        RandomStringGenerator stringGenerator = new RandomStringGenerator(9);
        String generatedString = stringGenerator.nextString();
        int generatedID = new Random().nextInt(Integer.MAX_VALUE);
        return new Resort(generatedString, generatedID);
    }

    /**
     * Get a list of dummy resort objects. The length of the list could be [0, 10].
     * @return a list of dummy resort objects.
     */
    public List<Resort> getResortList() {
        int length = new Random().nextInt(10);
        List<Resort> resortList = new LinkedList<>();
        DummyDataGenerator dummyDataGenerator = getInstance();
        for (int i = 0; i < length; i++) {
            resortList.add(dummyDataGenerator.getResort());
        }

        return resortList;
    }

    /**
     * Get a list of seasons for a resort.
     * @return a list of dummy seasons.
     */
    public List<String> getSeasons() {
        int length = new Random().nextInt(5);
        List<Integer> seasons = new LinkedList<>();
        for (int i = 0; i < length; i++) {
            seasons.add(ThreadLocalRandom.current().nextInt(2000, 2021));
        }
        Collections.sort(seasons);
        return seasons.stream()
            .map(String::valueOf)
            .collect(Collectors.toList());
    }
}
