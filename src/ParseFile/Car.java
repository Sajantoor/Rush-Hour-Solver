package ParseFile;

import Utility.Constants;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class Car {
    private Point coords; // x and y coords, first instance of car => this will be the top most, left most
                          // instance
    private byte size; // size of the car
    private byte name; // values A - Z (26 letters) 
    private boolean isHorizontal; // the direction the car can move

    /*
     * ===================== Constructors =====================
     */

    /**
     * Copy contructor: Assumes source is source is not null
     */
    private Car(Car source) {
        isHorizontal = source.isHorizontal;
        this.size = source.size;
        this.name = source.name;
        coords = new Point(source.coords.getX(), source.coords.getY());
    }

    /**
     * Regular constructor, assumes name is not null.
     */
    public Car(char name) {
        this.name = (byte) (name - Constants.ASCII_CAPITAL);
        this.coords = null;
    }

    /**
     * @param name  The name of the car
     * @param start The starting (first instance) coordinates of the car of as the
     *              class Point
     * @param end   The last (last instance) coordinates of the car of as the class
     *              Point
     */
    public Car(char name, Point start, Point end) {
        this.name = (byte) (name - Constants.ASCII_CAPITAL);
        this.coords = start;
        calcDirection(end);
        // calc size requires the end coordinate
        calcSize(end);
    }

    /*
     * ===================== Setters and getters =====================
     */

    /**
     * @param start Coordinates of the first instance of the car as class Point.
     */
    public void setStart(Point start) {
        this.coords = start;
    }

    /**
     * @param end: Coordinate of the last instance of the car as class Point.
     */
    public void setEnd(Point end) {
        // re calculate the direction and calculate size
        calcDirection(end);
        calcSize(end);
    }

    /**
     * @return name of the car
     */
    public char getName() {
        return (char) (name + Constants.ASCII_CAPITAL);
    }

    /**
     * @return Coordinates of the first (top most/ left most) instance of the car of
     *         class Point
     */
    public Point getStart() {
        return this.coords;
    }

    /**
     * @return Cooridnates of the last (bottom most / right most) instance of the
     *         car of class Point
     */
    public Point getEnd() {
        int x = this.coords.getX();
        int y = this.coords.getY();

        if (this.isHorizontal) {
            x += this.size;
        } else {
            y += this.size;
        }

        return new Point(x, y);
    }

    /**
     * Gets the direction of the car.
     * 
     * @return Returns true if the car is horizontal, false if vertical.
     */
    public boolean isHorizontal() {
        return this.isHorizontal;
    }

    /*
     * ============== Helper functions for constructing =======================
     */

    /**
     * Calculates the size of the car, requires the direction of the car to be
     * calculated first. This can be done using {@link #calcDirection(Point)}
     * 
     * @param end Cooridnates of the last instance of the car of class Point
     */
    private void calcSize(Point end) {
        int size;

        if (this.isHorizontal) {
            size = end.getX() - this.coords.getX();
        } else {
            size = end.getY() - this.coords.getY();
        }

        this.size = (byte) size;
    }

    /**
     * Calculates the direction and changes the value of this.isHorizontal. Uses
     * it's starting coordinates and ending coordinates to calculate
     * 
     * @param end Cooridnates of the last instance of the car of class Point
     */
    private void calcDirection(Point end) {
        if (this.coords == null && this.size != 0) {
            return;
        }

        if (this.coords.getX() != end.getX()) {
            this.isHorizontal = true;
        } else {
            this.isHorizontal = false;
        }
    }

    /*
     * =========================== Overrides ===================================
     */

    @Override
    /**
     * @return String with the name, start and end coordinates, and direction of the
     *         car (on seperate lines).
     */
    public String toString() {
        return new String("Name: " + this.getName() + "\nStart: " + coords + "\nEnd: " + this.getEnd() + "\nDir: "
                + (isHorizontal ? "Horizontal" : "Vertical"));
    }

    /**
     * Compares two car objects and determine if they are equal or not
     * 
     * @param o Other car
     * @return Returns true if the two car objects are equal, false otherwise.
     */

    public boolean equals(Car o) {
        if (this == o)
            return true;

        if (o == null)
            return false;

        if (this.hashCode() != o.hashCode())
            return false;

        if (this.getCoordsHashcode() != o.getCoordsHashcode())
            return false;

        return isHorizontal == o.isHorizontal() && coords.equals(o.coords)
                && this.size == o.size && this.name == o.name;
    }

    @Override
    public int hashCode() {
        int result = this.name;
        result = 31 * result + this.size;
        result = 31 * result + this.getCoordsHashcode();
        return result;
    }

    /**
     * @return Hashcode of the coords (Type point)
     */
    public int getCoordsHashcode() {
        return this.coords.hashCode();
    }

    /*
     * ================== Graph projections ==========================
     */

    /**
     * @return Returns true if the two cars have cells in common
     */
    public boolean isWreckedInto(Car another) {
        var startX = getStart().getX();
        var startY = getStart().getY();
        var endX = getEnd().getX();
        var endY = getEnd().getY();
        var anotherStartX = another.getStart().getX();
        var anotherStartY = another.getStart().getY();
        var anotherEndX = another.getEnd().getX();
        var anotherEndY = another.getEnd().getY();

        if (isHorizontal) {
            if (another.isHorizontal) {
                // both cars are horizontal and are on different rows
                // wreck is impossible
                if (startY != anotherStartY)
                    return false;

                // both cars are on the same row
                // there is a wreck if their ends are in the same column
                if (startX == anotherEndX || endX == anotherStartX)
                    return true;
                else
                    return false;
            } else {
                if (startY >= anotherStartY && startY <= anotherEndY) {
                    if (startX <= anotherStartX && endX >= anotherStartX) {
                        return true;
                    }
                }

                return false;
            }
        } else {
            if (!another.isHorizontal) {
                // both cars are horizontal and are on different rows
                // wreck is impossible
                if (startX != anotherStartX)
                    return false;

                // both cars are on the same row
                // there is a wreck if their ends are in the same column
                if (startY == anotherEndY || endY == anotherStartY)
                    return true;
                else
                    return false;
            } else {
                if (startX >= anotherStartX && startX <= anotherEndX) {
                    if (startY <= anotherStartY && endY >= anotherStartY) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    /**
     * @param cars - list of cars to check
     * @return Returns true if the given car is wrecked into any of the cars from
     *         the list
     */
    public boolean isWreckedIntoAnyOf(List<Car> cars) {
        // for every car check whether there is a wreck
        boolean isWreckFound = cars.stream().anyMatch(c -> this.isWreckedInto(c));
        return isWreckFound;
    }

    /**
     * Looks for the potential car this one is wrecked into
     * @return Returns {@link Optional} of a Car.
     */
    public Optional<Car> getPotentialWreck(List<Car> cars){
        return cars.stream()
                .filter(c -> !this.equals(c) && this.isWreckedInto(c))
                .findFirst();
    }

    /**
     * Checks whether the car's coordinates do not go outside of the board
     * 
     * @return true if the car coordinates are legal, false otherwise
     */
    public boolean isWithinBounds() {
        var start = getStart();
        var end = getEnd();
        // overboard on the x-coordinate
        if (start.getX() < 0 || end.getX() >= Constants.SIZE)
            return false;
        // overboard on the y-coordinate
        if (start.getY() < 0 || end.getY() >= Constants.SIZE)
            return false;

        return true;
    }

    /**
     * Creates a projection of this car, as if it moved forward
     * 
     * @return null if going overboard, a new car otherwise
     *
     */
    public CarProjection getMoveForwardProjection(char[][] board) {
        var boardCopy = boardClone(board);
        var copy = new Car(this);
        var x = this.getStart().getX();
        var y = this.getStart().getY(); 

        if (isHorizontal) {
            // translate by one
            x += 1;
            var x_end = this.getEnd().getX() + 1; 

            // out of bounds error.
            if (x_end >= Constants.SIZE)
                return null;
            
            // check if x_end doesn't hit another car, moving forward therefore end will be hitting the car first
            if (boardCopy[y][x_end] != Constants.EMPTY_FIELD) {
                return null;
            }

            copy.getStart().setX(x);
            // update board
            boardCopy[y][copy.getEnd().getX()] = this.getName(); // add instance at end 
            boardCopy[y][x - 1] = Constants.EMPTY_FIELD; // remove the starting car instance
        } else {
            // translate by one
            y += 1;
            var y_end = this.getEnd().getY() + 1;
            
            // out of bounds error.
            if (y_end >= Constants.SIZE)
                return null;
            
            // check if y_end doesn't hit another car, moving down therefore y_end would be hit first.
            if (boardCopy[y_end][x] != Constants.EMPTY_FIELD) {
                return null;
            }
            
            copy.getStart().setY(y);
            // update board
            boardCopy[copy.getEnd().getY()][x] = this.getName(); // add instance at end 
            boardCopy[y - 1][x] = Constants.EMPTY_FIELD; // remove the starting car instance
        }

        return new CarProjection(copy, boardCopy);
    }

    /**
     * Creates a projection of this car, as if it moved backwards
     * 
     * @return null if going overboard, a new car otherwise
     */
    public CarProjection getMoveBackwardsProjection(char[][] board) {
        var boardCopy = boardClone(board);
        var copy = new Car(this);
        var x = this.getStart().getX();
        var y = this.getStart().getY();

        if (isHorizontal) {
            x -= 1; // translate by 1

            // out of bounds error 
            if (x < 0)
                return null;
            
            // if there is an accident
            if (boardCopy[y][x] != Constants.EMPTY_FIELD) {
                return null;
            }

            copy.getStart().setX(x);
            boardCopy[y][x] = this.getName(); // add instance at start
            boardCopy[y][this.getEnd().getX()] = Constants.EMPTY_FIELD; // remove the last instance.
        } else {
            y -= 1;

            if (y < 0)
                return null;
            
            if (boardCopy[y][x] != Constants.EMPTY_FIELD) {
                return null;
            }

            copy.getStart().setY(y);
            boardCopy[y][x] = this.getName(); // add instance at start
            boardCopy[this.getEnd().getY()][x] = Constants.EMPTY_FIELD; // remove last instance.
        }

        return new CarProjection(copy, boardCopy);
    }

    /**
     * Wrapper around {@link #getMoveForwardProjection} and {@link #getMoveBackwardsProjection()}.
     * Generates a list of one move projections for this car.
     * Projections are guaranteed to be within the board
     *
     * @return list of projections, all within the board bounds, none are null
     */
    public ArrayList<Car> getOneMoveInBoardProjectionList(char[][] board) {
        var projections = new ArrayList<Car>();

        var forwardProjection = getMoveForwardProjection(board);

        if (forwardProjection != null) 
            projections.add(forwardProjection.getCar());

        var backwardsProjection = getMoveBackwardsProjection(board);

        if (backwardsProjection != null)
            projections.add(backwardsProjection.getCar());

        return projections;
    }

    /**
     * Extention of {@link #getMoveForwardProjection()}, instead gets all forward
     * projections and adds them to a list, does this until getMoveForwardProjection
     * returns null.
     * 
     * @return List of cars, containing forward projections, none are NULL.
     */
    public ArrayList<Car> getMoveForwardsList(char[][] board) {
        char[][] boardCopy = boardClone(board);
        ArrayList<Car> projectionList = new ArrayList<Car>();
        Car projection = this;

        // create forward projections until hit out of bounds, then add to list!
        while (projection != null) {
            CarProjection carProjection = projection.getMoveForwardProjection(boardCopy);
            if (carProjection != null) {
                projection = carProjection.getCar(); // get next car
                boardCopy = carProjection.getBoard(); // get next board 
                projectionList.add(projection); // add to list
            } else {
                projection = null; // stopping condition for the loop.
            }
        }

        return projectionList;
    }

    /**
     * Extention of {@link #getMoveBackwardsProjection()}, instead gets all
     * backwards projections and adds them to a list, does this until
     * getMoveBackwardsProjection returns null.
     * 
     * @return List of cars, containing backwards projections, none are NULL.
     */

    public ArrayList<Car> getMoveBackwardsList(char[][] board) {
        char[][] boardCopy = boardClone(board);
        ArrayList<Car> projectionList = new ArrayList<Car>();
        Car projection = this;

        // create forward projections until hit out of bounds, then add to list!
        while (projection != null) {
            CarProjection carProjection = projection.getMoveBackwardsProjection(boardCopy);
            
            if (carProjection != null) {
                projection = carProjection.getCar(); // get the next car
                boardCopy = carProjection.getBoard(); // get the next board
                projectionList.add(projection); // add to the list iff it's not null
            } else {
                projection = null; // stopping condition for the loop.
            }
        }

        return projectionList;
    }

    public boolean nameEquals(Car other){
        return other.getName() == getName();
    }

    // move to board.java later
    public static char[][] boardClone(char[][] board) {
        char[][] clone = new char[Constants.SIZE][Constants.SIZE];
        
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                clone[i][j] = board[i][j];
            }   
        }

        return clone;
    }
}