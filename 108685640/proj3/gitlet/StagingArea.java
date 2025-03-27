package gitlet;
import java.io.Serializable;
import java.util.TreeMap;
public class StagingArea implements Serializable {
    /**Added set.*/
    private TreeMap<String, String> added;
    /**Removed set.*/
    private TreeMap<String, String> removed;

    public StagingArea() {
        added = new TreeMap<>();
        removed = new TreeMap<>();
    }
    public TreeMap<String, String> getAdded() {
        return added;
    }
    public TreeMap<String, String> getRemoved() {
        return removed;
    }
}
