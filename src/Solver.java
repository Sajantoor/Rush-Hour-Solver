import Graph.*;
import ParseFile.*;
import java.io.FileWriter;
import java.io.IOException;

public class Solver {
    /**
     * 
     * @param args 2 arguments only, args[0] is the input, args[1] is the output
     *             file
     * @throws Exception Throws an exception if not enough arguments, invalid
     *                   arguments or unable to open or write to file.
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Expected 2 arguments: " + args.length + " provided.");
        }

        FileWriter file;

        // validate if it's a file that can be opened
        try {
            file = new FileWriter(args[1]);
        } catch (Exception e) {
            throw new IOException("Output file could not be created: " + e);
        }

        // solve the board
        // board validates the filename of arg[0]
        var board = new Board(args[0]);
        var graph = new Graph(board);
        var solution = graph.AStarTraversal();
        outputSolution(solution, file);
        file.close();
    }

    /**
     * 
     * @param solution BoardState returned from
     * 
     *                 <Pre>
     *                 Graph.AStarTraversal()
     *                 </Pre>
     * 
     * @param file     Filewriter that's a file to write to
     * @throws Exception Throws an exception if unable to write to the file
     */
    public static void outputSolution(BoardState solution, FileWriter file) throws Exception {
        BoardState state = solution;
        String output = "";

        // traverse backwards down the solution
        while (state != null && state.getParent() != null) {
            BoardState current = state;
            BoardState parent = state.getParent();
            // prepend since we are traversing backwards
            output = current.getBoard().findDifference(parent.getBoard()) + output;
            state = parent;
        }

        // remove the last '\n'
        int size = output.length();
        output = output.substring(0, size - 1);

        try {
            file.write(output);
        } catch (Exception e) {
            throw new IOException("File could not be written to: " + e);
        }
    }
}
