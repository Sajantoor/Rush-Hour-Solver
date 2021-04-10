import Graph.*;
import ParseFile.*;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.*;

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
                var testsPassed = new ArrayList<String>();
                // go through all files in directly
                for (File file : directoryListing) {
                    String name = file.getName();
                    int length = name.length();

                    // ignore .sol files
                    if (name.charAt(length - 1) == 'l') {
                        continue;
                    }

                    // run test
                    boolean passed;
                    if (printing) {
                        passed = RunTestPrinting(folderName + file.getName());
                    } else {
                        passed = RunTest(folderName + file.getName());
                    }
                    if(passed) testsPassed.add(file.getName());
                }
                System.out.println("Passed " + testsPassed.size() + "/" + directoryListing.length + " tests:");
                testsPassed.forEach(t -> System.out.println(t));
            }

        // run test with filename
        } else {
            if (printing) {
                RunTestPrinting(folderName + args[0]);
            } else {
                RunTest(folderName + args[0]);
            }
        }

        return;
    }

    public static boolean RunTestPrinting(String filename) {
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

            var executorService = Executors.newSingleThreadExecutor();

            // allows to asynchronously track time and terminate if working too long
            var future = executorService.submit(graph::AStarTraversal);

            // to change the time limit change the constant. You can set the units to minutes or ms too
            var t = future.get(5, TimeUnit.SECONDS);

            executorService.shutdown(); // **java magic**

            t.getBoard().printBoard();

            System.out.println("Elapsed time: " + (System.currentTimeMillis() - start));
            System.out.println("=========================================================");

            return true;
        }
        catch (TimeoutException e) {
            System.out.println("Test failed. Time out");

        }
        catch (Exception e) {
            System.out.println("Failed." + e);
            System.out.println("Stack trace: ");
            e.printStackTrace();
        }

        System.out.println("=========================================================");
        return false;
    }

    public static boolean RunTest(String filename) {
        try {
            System.out.println("Testing: " + filename);
            long start = System.currentTimeMillis();

            Board board = new Board(filename);
            System.out.println("Parsed board: " + (System.currentTimeMillis() - start));
            var graph = new Graph(board);
            var executorService = Executors.newSingleThreadExecutor();

            // allows to asynchronously track time and terminate if working too long
            var future = executorService.submit(graph::AStarTraversal);

            // to change the time limit change the constant. You can set the units to minutes or ms too
            var t = future.get(20, TimeUnit.SECONDS);
            executorService.shutdown(); // **java magic**

            if (t != null) { // null means it failed
                System.out.println("Success!");
                System.out.println("Elapsed time: " + (System.currentTimeMillis() - start));

                System.out.println("=========================================================");
                return true;
            } else {
                System.out.println("Failure... :(");
            }
        }
        catch (TimeoutException e) {
            System.out.println("Test failed. Time out");

        }
        catch (Exception e) {
            System.out.println("Failed." + e);
            System.out.println("Stack trace: ");
            e.printStackTrace();
        }

        System.out.println("=========================================================");
        return false;
    }
}
