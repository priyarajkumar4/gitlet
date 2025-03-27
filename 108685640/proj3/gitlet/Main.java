package gitlet;
/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Priya Rajkumar
 */
public class Main {


    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    private static boolean s = false;
    public static void main(String... args) {
        if (args.length == 0 || args[0].isEmpty()) {
            System.out.println("Please enter a command.");
            return;
        }
        switch1(args);
        if (s) {
            args[0] = "bre";
        }
        switch (args[0]) {
        case "reset": {
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                return;
            }
            Repo.reset(args[1]);
            break;
        }
        case "bre": {
            break;
        }
        case "commit": {
            Repo.commit(args[1]);
            break;
        }

        case "add": {
            Repo.add(args[1]);
            break;
        }
        case "find": {
            if (args.length == 2) {
                Repo.find(args[1]);
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
            break;
        }
        case "status": {
            if (args.length != 1) {
                System.out.println("Incorrect Operands.");
                return;
            }

            Repo.status();
            break;
        }
        case "rm": {
            if (args.length != 2) {
                System.out.println("Incorrect Operands.");
                return;
            }
            Repo.remove(args[1]);
            break;
        }
        default:
            System.out.println("No command with that name exists.");
        }
    }
    public static void switch1(String... args) {
        switch (args[0]) {
        case "checkout":
            if (args.length < 2 || args.length > 4) {
                System.out.println("Incorrect Operands");
            } else if ((args.length == 4 && !args[2].equals("--"))
                    || (args.length == 3 && !args[1].equals("--"))) {
                System.out.println("Incorrect Operands");
            } else {
                Repo.checkoutMain(args);
            }
            s = true;
            break;
        case "init": {
            Repo.init();
            s = true;
            break;
        }
        case "log": {
            Repo.log();
            s = true;
            break;
        }
        case "merge":
            if (args.length == 2) {
                Repo.merge(args[1]);
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
            s = true;
            break;
        case "global-log": {
            Repo.globalLog();
            s = true;
            break;
        }
        case "branch":
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                return;
            }
            Repo.branch(args[1]);
            s = true;
            break;
        case "rm-branch":
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                return;
            }
            Repo.branchRemoval(args[1]);
            s = true;
            break;
        default:
            break;
        }
    }
}

