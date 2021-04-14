package rushhour;

import Graph.*;
import ParseFile.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;

public class Solver {
    /**
     * 
     * @param args 2 arguments only, args[0] is the input, args[1] is the output
     *             file
     * @throws Exception Throws an exception if not enough arguments, invalid
     *                   arguments or unable to open or write to file.
     */
    public static void solveFromFile(String input, String output) throws FileNotFoundException {
        if (input == null || output == null) {
            throw new FileNotFoundException("Input and output strings cannot be null.");
        }

        FileWriter file;

        // validate if it's a file that can be opened
        try {
            file = new FileWriter(output);
        } catch (Exception e) {
            throw new FileNotFoundException("Output file could not be created: " + e);
        }

        // solve the board
        // board validates the filename of arg[0]
        var board = new Board(input);
        var graph = new Graph(board);
        var solution = graph.AStarTraversal();
        outputSolution(solution, file);

        try {
            file.close();
        } catch (Exception e) {
            throw new FileNotFoundException("Could not close file correctly.");
        }
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
    public static void outputSolution(BoardState solution, FileWriter file) throws FileNotFoundException {
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
            throw new FileNotFoundException("File could not be written to: " + e);
        }
    }
}
