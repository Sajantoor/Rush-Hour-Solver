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

- Wrote classes related to graph representation of the game.
- Wrote the A* traversal algorithm.
- Wrote methods related to and experimented with computation of heuristic distance for the board.
- Added static Utility.Constants class to reduce memory load of instances.
- Wrote the initial methods for getting projections and reachable states for the board.
- Wrote bfs and performance tests to help with developing and optimization of the A* solution.

Sajan Toor:

- Wrote board parsing logic and all the objects that are created after parsing the board.
- Rewrote projections to be far more efficent.
- Wrote hash codes and equals for each object parsing object.
- Wrote Solver class which is just responsible for input and ouput.
- Refactoring code: removing any "dead" code and made things more presentable. 

---

Overall work was split pretty evenly with about 50 - 50 contributions in terms of number of additions and deletions. We didn't just strictly do what was listed, there was some overlap, but these were our main contributions. 

## About

The basic idea was to parse the board, create easily modifiable board objects which could be used to create projections and new states and traverse said states using BFS, DFS or A* until a solved state was found. Pretty quickly we decided on using the A* algorithm since we could use heuristics to speed things up and make finding the solution faster. The basic overview of the heuristic is finding the distance from the "X" car is to the end and then figure out which cars are blocking the way and recursively figure out which of those cars are blocked.

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

[View Board.java](../main/src/ParseFile/Board.java)

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

One of the major changes in the parsing sections was how information was stored. It started pretty basic with using `int`s to store, x and y coordinates, size, and `char` for name. We quickly realized this would be inefficient for the memory and decided to switch to something else. Based of a stackoverflow post, `boolean` arrays were decided upon. Instead of storing a range of small values in an `int`, we stored them by converting to binary to decimal and converting back when needed. This made it so `Car` only used memory of 15 bits total. However, converting from decimal to binary and binary to decimal was done so often, with at least hundreds of thousands of function calls. Even with the most efficent algorithms to do so, it was inefficent and wasted quite a bit of time. So it was decided on to use the `byte` data type instead, resulting in speed increases of board solving of minimum 100%.

## Graphing

---
As mentioned above, we are solving the board using the A* algorithm.
Let's start with an overview of related classes.


Class representing a vertex in the graph is called `BoardState`.

```java
public class BoardState {

/**
 *
 * @param board creates a new board state from a board
 */
public BoardState(Board board) {
    ...
}
```
[View BoardState.java](../main/src/Graph/BoardState.java)

---
Some terminology:

- `target` - board state where the 'X' car touches the right side of the board
- `root state` - initial configuration of the board
- `reachable states` - boards that are within 1 car move from the current board
- `current distance` - number of moves to get from `root state` to the given one.
- `approximate distance` - heuristic approximation on the number of moves required to solve the board, or, in other words, get to `target` vertex.
---
Aside from the `Board` itself, `BoardState` also stores the parent state, `currentDistance`, `approximateDistance` and `hash` of the board.

Before turning to the methods of this class, we want to describe how the heuristic distance is computed.


### Heuristics

---
Our first heuristic logic was very simple: compute the distance between the right-end of the 'X' car and the exit. While fast to compute and implement, such approximation made the algorithm very reluctant to move the 'X' car to the left, which is often an optimal move.

Next heuristic tried to also take into account the cars blocking the way to the exit: we were increasing the approximate distance by 1 for each of such cars. This approximation was considerably better, but was giving a low performance in cases where in order to free the way, the solver needed to temporarily obstruct it by another car.

Therefore, we have updated the heuristic to include the cars which are preventing the freeing of the main path. In order to do this, we BFS through the board, and increase the heuristic everytime a car is blocked from the front or from the back. 
While developing this heuristic we have thought of a few tweaks:

- if a car obstructing the main path is of length 3, then the path can be freed only by moving this car all the way down. So we do not increase heuristic if it is blocked from above, yet we add to the BFS queue and increase the approximate distance for all the cars obstructing the way down.
- if a car obstructing the main path is of length 2, then, to free the path, it can either be moved 1 cell in one direction, or 2 cells in another direction. 
```java 
     switch (size) {
            case 3: {
                // player car is always on row #2
                // if a len-3 car is blocking its way, then
                // it can be removed from the way only by going all the way down to the bottom
                // of the board
                blockingCars = car.getBlockingForwardCarList(carBoard);

                break;
            }
            case 2:
            default: {
                // len-2 car is obstructing way
                // to free the road it needs to either move 1 step in one way, or 2 steps in the
                // other way
                if (car.getStart().getY() == Constants.PLAYER_CAR_Y_COORD) {
                    blockingCars = car.getBlockingNStepsBackwardsCarList(carBoard, 2);
                    var carBlockingForward = car.getBlockingForwardCar(carBoard);
                    if (carBlockingForward.isPresent())
                        blockingCars.add(carBlockingForward.get());
                } else {

                    blockingCars = car.getBlockingNStepsForwardCarList(carBoard, 2);
                    var carBlockingBackwards = car.getBlockingBackwardsCar(carBoard);
                    if (carBlockingBackwards.isPresent())
                        blockingCars.add(carBlockingBackwards.get());
                }
                break;
            }
      }
```
- We have also made an attempt to account for number of 1-step moves needed to remove a car from the main path. Unfortunately we had to abandon this idea, as the cars were very reluctant to go in the oppozite direction, even if the whole column was empty except for the car in question.

Since the heuristics computation has grown to be quite sophisticated, it was to precompute it once when the state is created, then store and be able to access it in O(1). This was even further optimize in the future, and now we are computing the heuristics for the board only if:
1) We are confident that we are going to be using it in A* traversal, and 
2) We have not computed the heuristics for the given board configuration before

In the computation of the approximate distance, we needed to be able to know whether a car will hit other cars, if it were to move forward or backwards n steps.
In order to achieve this, we have introduced these 5 methods: 
- `car.getMoveForwardProjection()`
- `car.getMoveBackwardsProjection()`
- `car.getOneMoveInBoardProjectionList()`
- `car.getMoveForwardList()`
- `car.getMoveBackwardsList()`

The first 2 were to return a copy of the Car (`null` if the projeciton goes out of the bounds of the board), but with its coordinates changed as the name suggests. The third is just the wrapper about the first 2, which would call both of them and return a list containing up to two non-null projections.
The last 2 would return all the possible projections if the car was to go in a given direction.

These same methods were used in getting the `reachable states` for each board configuration.

Next, to actually get the wreck, we were using the method `car.getPotentialWreck()`, which was returning `Optional<Car>`.
`Optional` is just a wrapper around the object, which would have the method `.isPresent()` to check if the stored value is not null, and `.get()` to get the stored value. 
The reason to use it was to avoid dealing with nulls, which is both hard to read and tedious to code.
Furthermore, it was very pleasant to use. 
For example:
```java
    ...
    var wreck = car.getPotentialWreck();
    if (wreck.isPresent()){
        var blockingCar = wreck.get()
    }
    ...
```
The main problem with this implementation was that in order to get a wreck, we would have to go through the whole list of cars on the board, and check whether their coordinates intersect and such procedure needed to be done with each of the car projections.
There cannot be more than 18 cars on the board, so the complexity of the method can still be viewed as O(1). Yet, it is worth noting that at the time we were optimizing this our solution was incredibly slow for a reason unknown to us. So, frankly, we were optimizing every bit where something could be optimized (up to the point of changing divisions by 2 by right shifts, powers of 2 by left shifts etc), and this optimization would theoretically remove a constant multiple of 20, which is something we definitely could not ignore.
Anyhow, we view the optimizations we have applied for this case as quite smart. We decided to re-compute the board 2d-array and store it on the stack, so that garbage collector would clean up the excessive memory later. In this array checking whether a wreck is present is O(1), but it would still take some time to get the Car object if we were storing only it's `name` in the array, so instead we are generating a `Car[][]` array, where each cell is either null, or a reference to the car object occupying this and adjacent cell(s). That way we can get a car object producing a wreck in 1 array look-up.
With this in mind, we realized that we don't actually need to create projections for the cars anymore, and can just check for the blocking cars in this array. 

Hence, the following 7 methods were introduced in the `Car` class:
```java
public ArrayList<Car> getBlockingForwardCarList(Car[][] board) {...}
```
```java
public ArrayList<Car> getBlockingBackwardsCarList(Car[][] board) {...} 
```
```java
 public Optional<Car> getBlockingForwardCar(Car[][] board) {...}
 ```
```java
 public Optional<Car> getBlockingBackwardsCar(Car[][] board) {...}
 ```
```java
 public ArrayList<Car> getOneStepBlockingCars(Car[][] board) {...}
``` 
```java
public ArrayList<Car> getBlockingNStepsForwardCarList(Car[][] board) {...}
```
```java
public ArrayList<Car> getBlockingNStepsBackwardsCarList(Car[][] board) {...}
```

Another important tweak in the heuristics, is that when creating a new board state, we added a choice to whether generate a sophisticated heuristics, or not generate a simple one / not generate any.
This is needed to compare the performance of A* with actual performance of BFS, as BFS should not waste time computing some heuristics. Also this supports the idea of not generating the heuristics eveytime we create a new state, as we might have already computed heuristics for such board configuration before.


### A* Traversal

---
Another class in our solution is called `Graph`. It contains implementations of BFS and A* traversals.

We will not talk about BFS, as it is a simple algorithm, and we did not add any modifications to it.

A few things to note about our A* implementation:

- We have modified the 'closedSet' to store the nodes that we have ever seen as a 'reachable state', and not only those who were removed from the 'openQueue'.
- We are using a `HashMap` as a `closedSet`, since we can retrieve an element in O(1) this way.
- We do not remove elements from `openQueue` if we find a more optimal state. The reason is that removing from a `priorityQueue` takes O(n). Rather, we check if the `currentNode`, popped from the openQueue has bigger `current distance` than the one with the same board configuration stored in the `closedSet`, and if this is the case, we proceed to next iteration.
- We compute the heuristic distance for a new state, only if it isn't yet present in the `closedSet`, and we are to push this state to the `openQueue`. In other cases we would simply copy the heuristics from the board stored in the `closedSet`.


### Getting Reachable States

---
Now, let's get back to the `boardState` class. The only method worth mentioning in here is
```java
 /**
 * @return an ArrayList of all board states that can be reached from this one
 */
public ArrayList<BoardState> getReachableStates(boolean shouldCreateDecentHeuristics) {
        ...
}
```
`shouldCreateDecentHeuristics`, as name suggests, represents whether sophisticated heuristic should be computed for the boards upon creation, or no.

The functionality of this method is rather straighforward: generate all possible car projections based on the `carList`, and generate new board states based on the new carList.
The projections are generated via the methods like `Car.getMoveForwardList(char[][] board)` and `Car.getMoveBackwardsList(char[][] board)`. These are the optimized versions of those mentioned in heuristics computation. The main difference is that now they return only the permissible projections (i.e. without wrecks) and use a `char[][] board` argument to do so fast.

`Car.getMoveBackwardsProjection` and `Car.getMoveForwardProjection` now return a new class called `CarProjection`, which is fundamentally a wrapper around `Car`, but also stores the `char[][] board`, so that we can generate next step projections from a given projection easily.

There are 2 things we would like to note about this method.

First, looking at this method one is likely to think that it breaks the DRY (Don't Repeat Yourself) principle of software development, as it has 2 almost identical loops, one operation on forward and the other one on backwards projections.

```java 
 // for each forward projection create new state
 for (Car forwardProjection : forwardList) {
     var nextList = carListClone(carList);
     nextList.set(index, forwardProjection);
     // generate a new board state with this projection instead of the car
     var newState = new BoardState(nextList);
     newState.setParent(this);
     newState.setCurrentDistance(currentDistance + 1);
     // add it to the reachable states list
     states.add(newState);
 
 // for each backward projection create new state
 for (Car backwardsProjection : backwardsList) {
     var nextList = carListClone(carList);
     nextList.set(index, backwardsProjection);
     // generate a new board state with this projection instead of the car
     var newState = new BoardState(nextList);
     newState.setParent(this);
     newState.setCurrentDistance(this.currentDistance + 1);
     // add it to the reachable states list
     states.add(newState);
 }
```

The reason we did not "merge" it into one loop is, once again, because we were looking to optimize things as much as possible. We know that processors actually execute everything not sequentially, but concurrently. That said, loops on linked lists normally cannot be run concurrently, as values in each iteration depends (value of LLnode pointer) on the value of a previous iteration.

If we have 2 loops, however, the processor can do iteration of each loops in the same clock cycle, with should speed things up, at least in theory (unfortunately, we are not familiar with the way Java compiler works).


The second thing we wanted to talk about here is an extremely hard to notice bug, that was slowing the performance by more than 9000%, which, to be completely frank, we fixed on accident.
The way we were generating the carList for projections of a given car was:

- remove the `Car` for the copy of the list
```java
    var restOfTheCarsStream = carList.stream().filter(c -> !c.equals(car));
    var restOfTheCars = restOfTheCarsStream.collect(Collectors.toList());
  ```
- for each `carProjection`, create another copy of the list, and add the projection to the end of the list.
```java 
var newCarList = new ArrayList<>(restOfTheCars);
newCarList.add(forwardProjection);
```
- generate a `boardState` based on this list.
```java
var newState = new BoardState(newCarList);
```

After applying myriads of optimizations to our code, and still not able to pass most of the boards in 20 seconds (even not all of the A-boards), we were getting rather desperate. We were thinking that perhaps our architecture is to complex, and it takes way to much time to create each boardState. The heuristic seemed decent, and the hashing was working correctly, so at some point we started to run out of ideas on what is going on.

By luck, or by some Programming God providence, Sajan decided to stop trusting the promised performance of Java Streams, and so he has re-written the above lines as follows:

- find the index of a needed `Car` in the list
```java
// get index of car, linear search
for (int i = 0; i < size; i++) {
    if (carList.get(i).getName() == name) {
        index = i;
        break;
    }
}
```
- for each `CarProjection`, clone the list, and change the element at index to the projection
```java
var nextList = carListClone(carList);
nextList.set(index, forwardProjection);
```
- generate a new `BoardState` based on this list
```java
var newState = new BoardState(nextList);
```

That's it...

From being able to pass only 12/40 tests in 15 seconds, we got a performance spike to mere 3.3 seconds for all 40 tests together!

Imagine the looks on our faces, considering we have spent more than 40 hours of coding on optimizations only, to have something like this fix all our problems.

It took us a few days to figure out what has happened. Allow us to explain:

Consider a car list `[A, X, Q]`, and let `X'` be a projection of `X`. Then, to generate next state, our algorithm would remove `X`, and add `X'` to the end, resulting in the list `[A, Q, X']`.
Then, later on, if we were to make a projection of the car A on this board, we would get the list `[Q, X', A']`.
Alternatively, if we were first to make a projection of `A`, and based on that make a projection on `X`, we would get `[Q, X, A']` and then `[Q, A', X']`. These two lists are not equal, and so their hashes are not equal either. Then, we would have 2 states with the same board configuration and hence the same heuristic, but different hashes.
A list on `n` cars has `n!` permutations. By creating all the possible states, we were very likely to generate perhaps not exactly a factorial, but still an **enormous** number of literally the same states.
Then, we would process each of them one by one, computing the same reachable states over and over again.

> Please recall that number of cars can go up to 18, so this little change has basically removed the `18!` constant from the code...

There are people on the internet who would brag about their code solving the hardest puzzle in 6 seconds.

Well, guess what, we have been so zealous with our optimizations, that now we are able to solve it in 0.125 seconds. By looking at only a thousand states, our code goes straight to the solution.


## Getting Started

---
There is probably nothing else to say about the implementation. Here is a guide on how to test it.

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
