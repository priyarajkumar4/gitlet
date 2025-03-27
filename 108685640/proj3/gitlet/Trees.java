package gitlet;
import java.io.Serializable;
import java.util.TreeMap;

public class Trees implements Serializable {
    /** the name of the file.*/
    private TreeMap<String, String> branch;
    public Trees() {
        branch = new TreeMap<>();
    }

    public TreeMap<String, String> branch() {
        return branch;
    }

}

