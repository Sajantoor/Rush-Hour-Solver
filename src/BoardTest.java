import Graph.*;
import ParseFile.*;

public class BoardTest {
    public static void main(String[] args) {
        try {
            Board board = new Board("..\\testFiles\\A00.txt");
            board.printCarObjects();
            board.printBoard();
            var boardState = new BoardState(board);
            var reachableStates = boardState.getReachableStates();
            reachableStates.forEach(state -> {
                state.getBoard().printBoard();
            });

            var graph = new Graph(board);
            var t = graph.AStarTraversal();

            System.out.println(t);
        } catch (Exception e) {
            System.out.println("Failed." + e);
            System.out.println("Stack trace: ");
            e.printStackTrace();
        }

        return;
    }
}
