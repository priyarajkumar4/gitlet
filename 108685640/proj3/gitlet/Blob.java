package gitlet;
import java.io.File;
import java.io.Serializable;
public class Blob  implements Serializable {

    public Blob(File f, String givenStringName) {
        name = givenStringName;
        stringContent = Utils.readContentsAsString(f);
        contents = Utils.readContents(f);
        hash = Utils.sha1(contents);
    }

    public byte[] getContents() {
        return this.contents;
    }
    public String getHash() {
        return hash;
    }
    public String getStringContent() {
        return stringContent;
    }
    /** the name of the file.*/
    private String name;
    /** the code to get the file.*/
    private String hash;
    /**contents within the file were looking at. */
    private byte[] contents;
    /**contents within the file were looking at. */
    private String stringContent;

}
