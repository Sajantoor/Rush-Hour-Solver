# Rush Hour Solver
> Final project for CMPT 225

#### Table of Contents
 * [Teammates](#Teammates)
 * [About](#About)
 * [The Code](#The-Code)
 * [Getting Started](#Getting-Started)

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
