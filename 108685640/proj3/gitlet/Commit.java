package gitlet;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
import java.util.Date;

/**@author Priya Rajkumar
 * This holds the message of whatever you put after the "commit" here.*/
public class Commit implements Serializable {

    /**This holds the message of whatever you put after the "commit" here.*/
    private String message;
    /**This holds the all the hashes of the blobs in the commit container.*/
    private TreeMap<String, String> blobContainer;
    /**This is the date in which a commit occurs. we have
     * this for checkout.*/
    private String date;
    /** if the parent before is null, meaning it's a new commit, then the
     * commit file holds a blob hashmap with nothing in it (new hashmap).
     * else, it will be the parents commit blob hashmap with all the blob files
     * of the parent in a hashmap.*/
    private Commit parentOne;
    /**This holds the message of whatever you put after the "commit" here.*/
    private Commit parentTwo;
    /**this is simply a string holding a hash for the commit itself. so the
     * blobs have an id and the commit has an id*/
    private String id;
    /**This holds the message of whatever you put after the "commit" here.*/
    private boolean isNull;

    public Commit() {
        isNull = true;
    }


    public Commit(String m, Date d,
                  Commit p1, Commit p2) {
        isNull = false;
        this.message = m;
        d = new Date(0);
        this.date = Instant.EPOCH.atZone(ZoneId.systemDefault()).format(
                DateTimeFormatter.ofPattern("EEE MMM d kk:mm:ss uuuu xxxx"));
        this.parentOne = p1;
        if (parentOne == null) {
            blobContainer = new TreeMap<>();
        } else {
            blobContainer = new TreeMap<>(parentOne.getBlobMap());
        }
        if (parentTwo != null) {
            this.parentTwo = p2;
        }
        if (m == "initial commit") {
            id = Utils.sha1(m, date.toString());
        } else {
            setID();
        }
    }

    public String getDate() {
        return this.date;
    }

    public TreeMap<String, String> getBlobMap() {
        return blobContainer;

    }
    public String getID() {
        return id;
    }

    public Commit getParentOne() {
        return parentOne;
    }

    public Commit getParentTwo() {
        return parentTwo;
    }

    public String getMessage() {
        return message;
    }


    public void setID() {
        String blobString = blobContainer.toString();
        String dateString = date;
        String parentString = parentOne.toString();
        id = Utils.sha1(blobString, message, dateString, parentString);
    }


}
