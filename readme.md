# Rush Hour Solver
> Final project for CMPT 225

#### Table of Contents
 * [Teammates](#Teammates)
 * [About](#About)
 * [The Code](#The-Code)
 * [Getting Started](#Getting-Started)
 * [Test Results](#Test-Results)

## Teammates

<table>
    <td align="center" >
        <div style="margin-right: 20px;">
        <a href="https://github.com/ShayGeko" ><img src="https://avatars.githubusercontent.com/u/25514702?v=4" width="100px;" alt=""/><br /><sub><b> George Shramko </b></sub></a><br/>
        </div>
        </td>
    <td align="center">
        <div style="margin-left: 20px;">
        <a href="https://github.com/Sajantoor"><img src="https://avatars.githubusercontent.com/u/25991050?v=4" width="100px;" alt=""/><br /><sub><b> Sajan Toor </b></sub></a><br/>
        </div>
    </td>
</table>

## Contributions from Each Person
___
George Shramko:

* Add your contributions here. 
___

Sajan Toor:

* Wrote board parsing logic and all the objects that are created after parsing the board. 
* Optmized parsed objects to use very little memory.
* Wrote hash codes and equals for each object parsing object.
* Wrote Solver class which is just responsible for input and ouput. 
___
Overall work was split pretty evenly with about 50 - 50 contributions in terms of number of additions and deletions. This can be seen on [Github's contributions](https://github.com/Sajantoor/Rush-Hour-Solver/graphs/contributors) as well as [Github's commits](https://github.com/Sajantoor/Rush-Hour-Solver/commits/main). We didn't just strictly do what was listed, there was some overlap but these were our main contributions.

## About
Basic overview of the project, strategies, etc.

## The Code 
___
### Parsing

The first step of the code was to parse the board. This introduces our first class named `Board`. 

```java 
    public class Board {
    /**
     * Constructor of the board, used to parse a board file.
     * 
     * @param fileName Filename of the board
     * @throws Exception Throws an exception if the filename is null or file is not
     *                   found
     */
    public Board(String fileName) throws Exception {
        ...
    }
```
[View Board.java](../blob/main/src/ParseFile/Board.java)

`Board` is responsible for parsing the file line by line, char by char. `Board` while parsing creates the other main parsing object `Car`. As shown in the main parsing algorithm here: 

```java
 /**
     * Creates Car objects from the char representation of the board array
     * 
     * @param board Char representation of the board
     */
    private void createObjectsFromBoard(char[][] board) {
        // parse board
        for (int i = 0; i < Constants.SIZE; i++) { // x
            for (int j = 0; j < Constants.SIZE; j++) { // y
                // locate car
                if (board[i][j] != '.') {
                    char car = board[i][j];
                    // check if car exists in carArray
                    boolean foundCar = false;
                    
                    // if it's already in the car array, don't add it.
                    for (int k = 0; k < carArray.size(); k++) {
                        if (carArray.get(k).getName() == car) {
                            foundCar = true;
                            break;
                        }
                    }

                    // if not create new car array
                    if (!foundCar) {
                        Point start = new Point(i, j);
                        Point end = findLastInstance(board, car, i, j);
                        Car carObject = new Car(car, start, end);
                        carArray.add(carObject);
                    }
                }
            }
        }
    }
```


`Board` is also responsible for much of the heuristics, but that will be mentioned in the graphing section. 
___

`Car` is the next major object. Instead of parsing a char array in each iterator and using for creating reachable states and for the graph traversal, *imagine how much of a pain that would've been*... 

Instead we use `Car` objects!

```java
    /**
     * @param name  The name of the car
     * @param start The starting (first instance) coordinates of the car of as the
     *              class Point
     * @param end   The last (last instance) coordinates of the car of as the class
     *              Point
     */
    public Car(char name, Point start, Point end) {
        ...
    }
```

`Car` is a useful class, with a lot of useful methods like `public Point getStart()`, returning a `Point` class with x and y coordinates of the `Car`. Another helpful is `public boolean isWreckedInto(Car another)`, where the `Car` checks if it hit another car, this is useful for creating new states. Some more interesting methods are `getMoveForwardProjection()` and `getMoveBackwardsProjection()`, this method creates a `new Car` while moving the car forward, also useful for creating new states.

In terms of memory the `Car` object doesn't use much, only using 4 bytes for each `Car`. While `char` uses 2 bytes and this excludes all the "empty" areas of the `char[][]` board representation. 

___

`Point` is the last parsing class, it's pretty basic. It's just used for x and y coordinates that are easy to access with `getX()` and `getY()` methods respectively. 

```java
    /**
     * Creates a new point
     * 
     * @param x x coordinate
     * @param y y coordinate
     */
    public Point(int x, int y) {
        ...
    }
```
___ 

One of the major changes in the the parsing sections was how information was stored. It started pretty basic with using `int`s to store, x and y coordinates, size, and `char` for name. We quickly realized this would be inefficient for the memory and decided to switch to something else. Based of a stackoverflow post, `boolean` arrays were decided upon. Instead of storing a range of small values in an `int`, we stored them by converting to binary to decimal and converting back when needed. This made it so `Car` only used memory of 15 bits total. However, converting from decimal to binary and binary to decimal was done so often, with at least hundreds of thousands of function calls. Even with the most efficent algorithms to do so, it was inefficent and wasted quite a bit of time. So it was decided on to use the `byte` data type instead, resulting in speed increases of board solving of minimum 100%.

### Graphing