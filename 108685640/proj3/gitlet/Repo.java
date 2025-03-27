package gitlet;
import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.Deque;
import static gitlet.Utils.*;
public class Repo {
    /**init created a new commit and makes the directory
     * for all the files we instantiated
     * only if that gitFile doesnt exist already.
     * The only was the gitFile can exist is if
     * the init function was already called within
     * our directory. Then is gets the ID of the
     * initial commit for the head, which is a  string
     * to be equal to that hash id so that it can point
     * to that specific head.*/
    public static void init() {
        if (gitDir.exists()) {
            System.out.println("A Gitlet version-control "
                    + "system already exists in "
                    + "the current directory.");
            return;
        }
        gitDir.mkdir();
        Commit initial = new Commit("initial commit", new Date(0), null, null);
        stage = new StagingArea();
        head = initial.getID();
        commitFile.mkdir();
        blobFile.mkdir();
        Utils.writeContents(headFile, head);

        Utils.writeObject(new File(commitFile, head), initial);
        Trees b = new Trees();
        b.branch().put("master", initial.getID());
        writeObject(TREEFILE, b);
        writeContents(CURRENT, "master");
        Utils.writeObject(stagingFile, stage);
        isRemoved = false;
    }
    public static void add(String file) {
        File isExist = join(directory, file);
        if (!isExist.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        addStage();
        Blob adding = new Blob(isExist, file);
        TreeMap<String, String> headMap = readObject(new File(".gitlet/commit/"
                + readContentsAsString(headFile)), Commit.class).getBlobMap();
        if (stage.getRemoved().containsKey(file)) {
            stage.getRemoved().remove(file);
        }
        if (!adding.getHash().equals(headMap.get(file))) {
            stage.getAdded().put(file, adding.getHash());
        } else if (stage.getAdded().containsKey(file)
                && adding.getHash().equals(headMap.get(file))) {
            stage.getAdded().remove(file);

        }
        writeContents(stagingFile, serialize(stage));
        writeContents(new File(blobFile, adding.getHash()), serialize(adding));
    }

    public static void commit(String message, Commit...args) {
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
        }
        addStage();
        Commit commit;
        if (args.length > 2) {
            commit = new Commit(message,
                    new Date(System.currentTimeMillis()), args[1], args[2]);

        } else {
            commit = new Commit(message,
                    new Date(System.currentTimeMillis()),
                    readObject(new File(".gitlet/commit/"
                                    + readContentsAsString(headFile)),
                            Commit.class), new Commit());
        }
        TreeMap<String, String> added = stage.getAdded();
        TreeMap<String, String> removed = stage.getRemoved();
        if (added.isEmpty() && removed.isEmpty()) {
            System.out.println("No changes added to the commit.");
        }
        for (String x: added.keySet()) {
            commit.getBlobMap().put(x, added.get(x));
        }

        for (String x: removed.keySet()) {
            commit.getBlobMap().remove(x, removed.get(x));
        }

        commit.setID();
        writeObject(join(commitFile, commit.getID()), commit);
        writeContents(headFile, commit.getID());
        Trees tree = readObject(TREEFILE, Trees.class);
        tree.branch().put(readContentsAsString(CURRENT), commit.getID());
        writeObject(TREEFILE, tree);
        clearStage();
    }




    public static void log() {
        Commit h = readObject(new File(".gitlet/commit/"
                + readContentsAsString(headFile)), Commit.class);
        while (h != null) {
            System.out.println("===");
            System.out.println("commit " + h.getID());
            System.out.println("Date: " + h.getDate());
            System.out.println(h.getMessage());
            System.out.println();
            h = h.getParentOne();

        }
    }

    public static Trees getTree() {
        return readObject(TREEFILE, Trees.class);
    }

    public static void branch(String b) {
        Trees rv = getTree();
        if (rv.branch().containsKey(b)) {
            System.out.println("A branch with that name already exists.");
        } else {
            rv.branch().put(b, head().getID());
            writeObject(TREEFILE, rv);
        }
    }

    public static void branchRemoval(String b) {
        String str = readContentsAsString(CURRENT);
        Trees rv = getTree();
        if (!rv.branch().containsKey(b)) {
            System.out.println("A branch with that name does not exist.");
        } else {
            if (str.equals(b)) {
                System.out.println("Cannot remove the current branch.");
                return;
            } else {
                rv.branch().remove(b);
                writeObject(TREEFILE, rv);
            }
        }
    }


    public static void reset1(Commit var, Commit var2) {
        for (String x : plainFilenamesIn(directory)) {
            if (var.getBlobMap().containsKey(x)
                    && !var2.getBlobMap().containsKey(x)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                System.exit(0);
                return;
            }
        }
    }

    public static void reset2(Commit var2) {
        for (String x : var2.getBlobMap().keySet()) {
            Utils.restrictedDelete(new File(directory, x));
        }
    }
    public static void reset3(Commit var) {
        for (String add : stage.getAdded().keySet()) {
            if (!var.getBlobMap().containsKey(add)) {
                stage.getAdded().remove(add);
            }
        }
        for (String remove : stage.getRemoved().keySet()) {
            if (!var.getBlobMap().containsKey(remove)) {
                stage.getRemoved().remove(remove);
            }
        }
    }
    public static void reset(String idreset) {
        Trees b = getTree();
        addStage();

        if (!join(commitFile, idreset).exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }

        Commit var = readObject(new File(commitFile, idreset), Commit.class);
        Commit var2 = readObject(new File(commitFile,
                b.branch().get(readContentsAsString(CURRENT))), Commit.class);

        reset1(var, var2);
        reset2(var2);
        reset3(var);

        if (!join(commitFile, idreset).exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        for (String x : var.getBlobMap().keySet()) {
            Repo.checkout(var.getID(), x);
        }
        clearStage();
        b.branch().put(readContentsAsString(CURRENT), var.getID());
        writeObject(TREEFILE, b);
        writeContents(headFile, var.getID());
    }

    public static void clearStage() {
        stage.getAdded().clear();
        stage.getRemoved().clear();
        writeObject(stagingFile, stage);
    }



    public static void find(String s) {
        boolean flip = false;
        for (String x : getFiles()) {
            Commit c = readObject(join(commitFile, x), Commit.class);
            if (s.equals(c.getMessage())) {
                String print = c.getID();
                System.out.println(print);
                flip = true;
            }
        }
        if (!flip) {
            System.out.println("Found no commit with that message.");
        }
    }


    public static void checkoutMain(String... args) {
        Trees tree = readObject(TREEFILE, Trees.class);
        if (args.length == 3 && args[1].equals("--")) {
            checkout(args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            checkout(args[1], args[3]);
        } else if (args.length == 2) {
            String a = args[1];
            Commit c = readObject(join(commitFile,
                    readObject(TREEFILE, Trees.class).branch().
                            get(readContentsAsString(CURRENT))), Commit.class);
            if (!readObject(TREEFILE, Trees.class).branch().containsKey(args[1])
                    || readContentsAsString(CURRENT).equals(args[1])) {
                if ((!readObject(TREEFILE, Trees.class).branch().
                        containsKey(args[1]))) {
                    System.out.println("No such branch exists.");
                }
                if (readContentsAsString(CURRENT).equals(args[1])) {
                    System.out.println("No need to"
                            + " checkout the current branch.");
                }
                return;
            }
            Commit rv = readObject(new File(commitFile,
                    readObject(TREEFILE, Trees.class).
                            branch().get(a)), Commit.class);
            for (String x : plainFilenamesIn(directory)) {
                if (rv.getBlobMap().containsKey(x)
                        && !c.getBlobMap().containsKey(x)) {
                    System.out.println("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    System.exit(0);
                    return;
                }
            }
            if (readObject(TREEFILE, Trees.class).branch().
                    containsKey(args[1])) {
                for (String fileName : c.getBlobMap().keySet()) {
                    Utils.restrictedDelete(join(directory, fileName));
                }
                for (String x : rv.getBlobMap().keySet()) {
                    checkout(rv.getID(), x);
                }
                addStage();
                clearStage();
                writeContents(CURRENT, args[1]);
                writeContents(headFile, rv.getID());

            } else {
                System.out.println("No such branch exists.");
            }
        }

    }

    public static StagingArea addStage() {
        stage = readObject(stagingFile, StagingArea.class);
        return stage;
    }



    public static void checkout(String file) {
        checkout(head().getID(), file);
    }

    /**The Directory is a File that holds all the other files.*/
    private static File isFile = null;

    public static void checkout(String id, String file) {
        for (String x : getFiles()) {
            if (x.startsWith(id)) {
                isFile = join(commitFile, x);
            }
        }
        boolean exists = isFile != null;
        if (exists) {
            Commit c = readObject(isFile, Commit.class);
            if (!c.getBlobMap().containsKey(file)) {
                System.out.println("File does not exist in that commit.");
                return;
            }
            File f = new File(directory, file);
            writeContents(f, readObject(join(blobFile, c.getBlobMap().
                    get(file)), Blob.class).getContents());
        } else {
            System.out.println("No commit with that id exists.");
        }
        isFile = null;


    }



    public static void globalLog() {
        for (String x : getFiles()) {
            Commit c = readObject(join(commitFile, x), Commit.class);
            System.out.println("===");
            System.out.println("commit " + c.getID());
            System.out.println("Date: " + c.getDate());
            System.out.println(c.getMessage() + "\n");
        }
    }
    public static List<String> getFiles() {
        return plainFilenamesIn(commitFile);
    }

    public static void branchStatus() {
        System.out.println("=== Branches ===");
        Trees a = readObject(TREEFILE, Trees.class);
        ArrayList<String> tree = new ArrayList<>(a.branch().keySet());
        sortList(tree);
        for (String x : tree) {
            if (x.equals(Utils.readContentsAsString(CURRENT))) {
                System.out.println("*" + x);
            } else {
                System.out.println(x);
            }
        }
    }
    public static void sortList(List<String> list) {
        Collections.sort(list);
    }

    public static void stagedStatus() {
        System.out.println("\n=== Staged Files ===");
        List<String> staging = new ArrayList<>(stage.getAdded().keySet());
        sortList(staging);
        for (String x : staging) {
            System.out.println(x);
        }
    }



    public static void removedStatus() {
        System.out.println("\n=== Removed Files ===");
        List<String> remove = new ArrayList<>(stage.getRemoved().keySet());
        sortList(remove);
        for (String x : remove) {
            System.out.println(x);
        }
    }


    public static void untrackedStatus() {
        System.out.println("\n=== Untracked Files ===");
        Commit current = getCurrent();
        TreeSet<String> untracked = new TreeSet<>();
        List<String> traverse = Utils.plainFilenamesIn(directory);
        for (String fileName : traverse) {
            if (!addStage().getAdded().containsKey(fileName)
                    && !current.getBlobMap().containsKey(fileName)) {
                untracked.add(fileName);
            }
        }
        for (String x : untracked) {
            System.out.println(x);
        }
        System.out.println("");
    }

    public static Commit getCurrent() {
        return readObject(new File(commitFile,
                readObject(TREEFILE, Trees.class).
                        branch().get(Utils
                                .readContentsAsString
                                        (CURRENT))), Commit.class);
    }


    public static void status() {
        if (!gitDir.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        branchStatus();
        addStage();
        stagedStatus();
        addStage();
        removedStatus();
        modStatus();
        untrackedStatus();

    }

    public static void modStatus() {
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        for (String x : modHelper()) {
            System.out.println(x);
        }
    }

    public static Commit getStatus(Trees t) {
        return readObject(join(commitFile,
                t.branch().get(readContentsAsString(CURRENT))), Commit.class);
    }




    private static TreeSet<String> modHelper() {
        TreeSet<String> modRV = new TreeSet<>();
        Trees t = readObject(TREEFILE, Trees.class);
        for (String x : getDirectory()) {
            String dc = readContentsAsString(new File(directory, x));
            if (getStatus(t).getBlobMap().get(x) != null) {
                String comp = getStatus(t).getBlobMap().get(x);
                Blob commitBlob = readObject(new File(blobFile, comp),
                        Blob.class);
                if (!dc.equals(commitBlob.getStringContent())
                        && !addStage().getRemoved().containsKey(x)) {
                    modRV.add(x + " " + "(modified)");
                }
            }
            boolean isNull = stage.getAdded().get(x) != null;
            if (isNull) {
                String str = stage.getAdded().get(x);
                Blob fileBlob = readObject(new File(blobFile, str), Blob.class);
                if (!fileBlob.getStringContent().equals(dc)) {
                    modRV.add(x + " " + "(modified)");
                }
            }
        }

        for (String x : stage.getAdded().keySet()) {
            if (isContained(x)) {
                modRV.add(x + " " + "(deleted)");
            }
        }
        for (String x : getStatus(t).getBlobMap().keySet()) {
            if (isDeleted(x)) {
                modRV.add(x + " " + "(deleted)");
            }
        }
        return modRV;
    }


    public static boolean isDeleted(String x) {
        return !addStage().getRemoved().containsKey(x)
                && !getDirectory().contains(x);
    }

    public static boolean isContained(String x) {
        return !getDirectory().contains(x);
    }





    public static void remove(String file) {
        addStage();
        boolean isSet = stage.getAdded().containsKey(file);
        boolean isHeld = head().getBlobMap().containsKey(file);
        if (isSet) {
            stage.getAdded().remove(file);
            writeObject(stagingFile, stage);
            isRemoved = true;
        }
        if (isHeld) {
            File rv = join(directory, file);
            if (!rv.exists()) {
                stage.getRemoved().put(file, head().getBlobMap().get(file));
            } else {
                stage.getRemoved().put(file,
                        new Blob(join(directory, file), file).getHash());
                rv.delete();
            }
            writeObject(stagingFile, stage);
            isRemoved = true;



        }
        if (!isRemoved) {
            System.out.println("No reason to remove the file.");
        }
        isRemoved = false;
    }



    public static void merge(String merger) {
        Trees t = getTree();
        String strCurrent = readContentsAsString(CURRENT);
        Commit c = readObject(join(commitFile,
                t.branch().get(strCurrent)), Commit.class);
        boolean existingBranch = t.branch().containsKey(merger);
        boolean emptyAdd = addStage().getAdded().isEmpty();
        boolean emptyRemove = addStage().getRemoved().isEmpty();
        if (!existingBranch) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (!emptyAdd || !emptyRemove) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        Commit sorting = readObject(join(commitFile,
                t.branch().get(merger)), Commit.class);
        boolean cannotMerge = merger.equals(readContentsAsString(CURRENT));
        if (cannotMerge) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        for (String x : getDirectory()) {
            boolean untracked = sorting.getBlobMap().containsKey(x);
            boolean prob = !c.getBlobMap().containsKey(x);
            if (untracked && prob) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, " + "or add and commit it first.");
                System.exit(0);
                return;
            }
        }
        Commit yeehaw = bfs(c, sorting);
        if (yeehaw.getID().equals(sorting.getID())) {
            System.out.println("Given branch is an "
                    + "ancestor of the current branch.");
            return;
        }
        if ((c.getID()).equals(yeehaw.getID())) {
            checkout(merger);
            t.branch().put(readContentsAsString(CURRENT), c.getID());
            findCurrent();
            writeContents(headFile, sorting.getID());

            System.out.println("Current branch fast-forwarded.");
            return;
        }

        help(c, sorting, yeehaw);

        checkConflict(c, yeehaw, sorting);
        String mergeMessage = "Merged " + merger
                + " into " + readContentsAsString(CURRENT) + ".";
        commit(mergeMessage, c, sorting);


    }
    private static void help(Commit c, Commit sorting, Commit yeehaw) {
        for (String x : c.getBlobMap().keySet()) {
            String d = sorting.getBlobMap().get(x);
            String a = yeehaw.getBlobMap().get(x);
            String b = c.getBlobMap().get(x);
            if ((d != null && !d.equals(b) && a != null && a.equals(b))
                    || (a == null
                    && b == null && d != null)) {
                checkout(sorting.getID(), x);
                add(x);
            }
        }
        for (String x : yeehaw.getBlobMap().keySet()) {
            String b = c.getBlobMap().get(x);
            String d = sorting.getBlobMap().get(x);
            String a = yeehaw.getBlobMap().get(x);

            if (d == null && a != null
                    && a.equals(b)) {
                remove(x);
            }
        }
        for (String x : sorting.getBlobMap().keySet()) {
            String a = yeehaw.getBlobMap().get(x);
            String d = sorting.getBlobMap().get(x);
            String b = c.getBlobMap().get(x);
            if (a == null && b == null && d != null) {
                checkout(sorting.getID(), x);
                add(x);
            }
        }

    }

    private static void checkConflict(Commit c,
                                      Commit yeehaw,
                                      Commit pru) {
        HashSet<String> set = new HashSet<>();
        set.addAll(yeehaw.getBlobMap().keySet());
        boolean bool = false;
        set.addAll(pru.getBlobMap().keySet());
        set.addAll(c.getBlobMap().keySet());
        for (String x : set) {
            String findS = yeehaw.getBlobMap().get(x);
            String findC = c.getBlobMap().get(x);
            String findV = pru.getBlobMap().get(x);
            String a = "";
            String b = "";

            if ((findS != null)
                    && ((findC != null && findV == null
                    && !findC.equals(findS))
                    || (findV != null
                    && findC == null
                    && !findV.equals(findS)))) {
                bool = true;
            }
            if (findS == null
                    && findV != null
                    && findC != null
                    && !findV.equals(findC)) {
                bool = true;
            }

            if (findS != null
                    && findC != null
                    && findV != null && !findC.equals(findV)
                    && !findC.equals(findS) && !findV.equals(findS)) {
                bool = true;
            }
            if (bool) {
                a = "";
                b = "";
                if (findC != null) {
                    a = readObject(new File(blobFile, findC),
                            Blob.class).getStringContent();
                }
                if (findV != null) {
                    b = readObject(new File(blobFile, findV),
                            Blob.class).getStringContent();
                }

                String str = "<<<<<<< HEAD\n"
                        + a + "=======\n" + b + ">>>>>>>\n";
                writeContents(join(directory, x),
                        str);
                add(x);
            }
        }
        if (bool) {
            System.out.println("Encountered a merge conflict.");
        }
    }


    public static Commit bfs(Commit c, Commit sorting) {
        deque = new ArrayDeque<>();
        hashString = new HashSet<>();
        deque.addLast(c.getID());
        deque.addLast(sorting.getID());
        while (!emptyDeque(deque)) {
            String various = deque.removeFirst();
            if (hashString.contains(various)) {
                return readObject(join(commitFile, various), Commit.class);
            }
            Commit rv = readObject(join(commitFile, various), Commit.class);
            hashString.add(various);
            if (rv.getParentOne() != null) {
                deque.addLast(rv.getParentOne().getID());
            }
        }
        return null;
    }

    public static Commit head() {
        return readObject(join(commitFile,
                readContentsAsString(headFile)), Commit.class);
    }



    public static List<String> getDirectory() {
        return plainFilenamesIn(directory);
    }

    /**The Directory is a File that holds all the other files.*/
    private static Deque<String> deque = new ArrayDeque<>();
    /**The Directory is a File that holds all the other files.*/
    private static HashSet<String> hashString = new HashSet<>();

    public static boolean emptyDeque(Deque s) {
        return s.isEmpty();
    }

    public static void findCurrent() {
        writeContents(CURRENT, readContentsAsString(CURRENT));
    }


    /**The Directory is a File that holds all the other files.*/
    private static HashSet<String> containingFile;
    /**The Directory is a File that holds all the other files.*/
    private static File directory = new File(System.getProperty("user.dir"));
    /**The GitFile is a File that shows that Directory.*/
    private static File gitDir = new File(directory, ".gitlet");
    /**The BlobFile is a child of the gitFile.*/
    private static File blobFile = new File(gitDir, "blobs");
    /**The StagingFile is a child of the gitFile.*/
    private static File stagingFile = new File(gitDir, "staging");
    /**The commit file holds all the blobs.*/
    private static File commitFile = new File(gitDir, "commit");
    /**The headFile points to most current branch which is master.*/
    private static File headFile = new File(gitDir, "head");
    /**The Directory is a File that holds all the other files.*/
    public static final File TREEFILE = join(gitDir, "branches");
    /**The Directory is a File that holds all the other files.*/
    public static final File CURRENT = join(gitDir, "currentBranch");
    /**The Directory is a File that holds all the other files.*/
    private static String head;
    /**The Directory is a File that holds all the other files.*/
    private static StagingArea stage;
    /**The Directory is a File that holds all the other files.*/
    private static boolean isRemoved;




}
