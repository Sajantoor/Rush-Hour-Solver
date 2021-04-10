import Graph.*;
import ParseFile.*;
import java.io.File;

public class BoardTest {
    public static void main(String[] args) {
        String folderName = "../testFiles/";
        boolean printing = false;
        if (args.length > 1) {
            printing = args[1].equals("print") ? true : false;
        }

        // run all tests
        if (args[0].equals("all")) {
            File dir = new File(folderName);
            File[] directoryListing = dir.listFiles();

            // if directory exists
            if (directoryListing != null) {
                // go through all files in directly
                for (File file : directoryListing) {
                    String name = file.getName();
                    int length = name.length();

                    // ignore .sol files
                    if (name.charAt(length - 1) == 'l') {
                        continue;
                    }

                    // run test
                    if (printing) {
                        RunTestPrinting(folderName + file.getName());
                    } else {
                        RunTest(folderName + file.getName());
                    }
                }
            }
        // run test with filename
        } else {
            if (printing) {
                RunTestPrinting(folderName + args[0]);
            } else {
                RunTest(folderName + args[0]);
            }
        }
    }

    public static void RunTestPrinting(String filename) {
        try {
            System.out.println("Testing: " + filename);
            long start = System.currentTimeMillis();
            Board board = new Board(filename);
            System.out.println("Parse board: " + (System.currentTimeMillis() - start));
            board.printBoard();
            var boardState = new BoardState(board);
            var reachableStates = boardState.getReachableStates();
            reachableStates.forEach(state -> {
                state.getBoard().printBoard();
            });

            var graph = new Graph(board);
            var t = graph.AStarTraversal();

            t.getBoard().printBoard();

            System.out.println("Elapsed time: " + (System.currentTimeMillis() - start));
            System.out.println("=========================================================");
        } catch (Exception e) {
            System.out.println("Failed." + e);
            System.out.println("Stack trace: ");
            e.printStackTrace();
        }

        return;
    }

    public static void RunTest(String filename) {
        try {
            System.out.println("Testing: " + filename);
            long start = System.currentTimeMillis();

            Board board = new Board(filename);
            System.out.println("Parsed board: " + (System.currentTimeMillis() - start));
            var graph = new Graph(board);
            var t = graph.AStarTraversal();

            if (t != null) { // null means it failed
                System.out.println("Success!");
                System.out.println("Elapsed time: " + (System.currentTimeMillis() - start));
            } else {
                System.out.println("Failure... :(");
                return;
            }

            System.out.println("=========================================================");
        } catch (Exception e) {
            System.out.println("Failed." + e);
            System.out.println("Stack trace: ");
            e.printStackTrace();
        }

        return;
    }
}
