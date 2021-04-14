package ParseFile;

public class CarProjection {
    private Car car; 
    private char[][] board;

    public CarProjection(Car car, char[][] board) {
        this.car = car;
        this.board = board;
    }
    
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public char[][] getBoard() {
        return board;
    }

    public void setBoard(char[][] board) {
        this.board = board;
    }
}
