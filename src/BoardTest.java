import ParseFile.*;

public class BoardTest {
    public static void main(String[] args) {
        try {
            Board board = new Board("..\\testFiles\\A00.txt");
            board.printBoard();
            board.printCarObjects();
        } catch (Exception e) {
            System.out.println("Failed." + e);
        }
    }
}
