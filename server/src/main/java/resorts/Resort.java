package resorts;

/**
 * The data structure for the resorts.
 */
public class Resort {
    private final String resortName;
    private final int resortID;

    /**
    The constructor for the resort object by setting the name and the id of resort.
     */
    public Resort(String resortName, int resortID) {
        this.resortID = resortID;
        this.resortName = resortName;
    }
}
