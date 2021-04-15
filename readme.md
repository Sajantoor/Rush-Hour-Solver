# Rush Hour Solver

> Final project for CMPT 225

#### Table of Contents
- [Teammates](#Teammates)
- [About](#About)
- [The Code](#The-Code)
- [Getting Started](#Getting-Started)
- [Test Results](#Test-Results)

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

---

George Shramko:

- Add your contributions here.

---

Sajan Toor:

- Wrote board parsing logic and all the objects that are created after parsing the board.
- Rewrote projections to be far more efficent.
- Wrote hash codes and equals for each object parsing object.
- Wrote Solver class which is just responsible for input and ouput.

---

Overall work was split pretty evenly with about 50 - 50 contributions in terms of number of additions and deletions. We didn't just strictly do what was listed, there was some overlap but these were our main contributions.

## About

The basic idea was to parse the board, create easily modifyiable board objects which could be used to create projections and new states and traverse said states using BFS, DFS or A* until a solved state was found. Pretty quickly we decided on using the A* algorithm since we could use heuristics to speed things up and make finding the solution faster. The basic overview of the heuristic is finding the distance from the "X" car is to the end and then figure out which cars are blocking the way and recursively figure out which of those cars are blocked.

## The Code

---

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

---

`Car` is the next major object. Instead of parsing a char array in each iterator and using for creating reachable states and for the graph traversal, _imagine how much of a pain that would've been_...

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

---

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

---

One of the major changes in the the parsing sections was how information was stored. It started pretty basic with using `int`s to store, x and y coordinates, size, and `char` for name. We quickly realized this would be inefficient for the memory and decided to switch to something else. Based of a stackoverflow post, `boolean` arrays were decided upon. Instead of storing a range of small values in an `int`, we stored them by converting to binary to decimal and converting back when needed. This made it so `Car` only used memory of 15 bits total. However, converting from decimal to binary and binary to decimal was done so often, with at least hundreds of thousands of function calls. Even with the most efficent algorithms to do so, it was inefficent and wasted quite a bit of time. So it was decided on to use the `byte` data type instead, resulting in speed increases of board solving of minimum 100%.

### Graphing

## Getting Started

---

Add your test files in the folder called `testFiles` and compile `src/rushhourtest/TestRushHour.java`, this will solve all the tests in the folder and provide a solution in the solutions folder named `Solutions`, the file is named after the test with the extension `".sol"`.

## Test Results
---
> The test results are specifically from CSIL "asb9700u-b07.csil.sfu.ca"

| Test    | Time (ms) |
| ------- | :-------: |
| A00.txt |    193    |
| A01.txt |    44     |
| A02.txt |    26     |
| A03.txt |    42     |
| A04.txt |    28     |
| A05.txt |    28     |
| A06.txt |    40     |
| A07.txt |    147    |
| A08.txt |    26     |
| A09.txt |    27     |
| A10.txt |    71     |
| B11.txt |    41     |
| B12.txt |    39     |
| B13.txt |    137    |
| B14.txt |    183    |
| B15.txt |    42     |
| B16.txt |    94     |
| B17.txt |    59     |
| B18.txt |    48     |
| B19.txt |    42     |
| B20.txt |    29     |
| C21.txt |    30     |
| C22.txt |    64     |
| C23.txt |    56     |
| C24.txt |    162    |
| C25.txt |    274    |
| C26.txt |    159    |
| C27.txt |    91     |
| C28.txt |    176    |
| C27.txt |    91     |
| C29.txt |    59     |
| D30.txt |    151    |
| D31.txt |    43     |
| D32.txt |    121    |
| D33.txt |    168    |
| D34.txt |    184    |
| D35.txt |    95     |

<br/>

> This gives us a total elapsed time of 3310 ms or 3.31 seconds! 