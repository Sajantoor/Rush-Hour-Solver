package Graph;

import ParseFile.Board;

import java.util.*;

public class Graph {
    private BoardState rootState;
    private int numberOfVisitedStates;

    public Graph(Board board) {
        rootState = new BoardState(board);
    }

    public BoardState AStarTraversal() {
        // min priorityQueue for A* traversal
        // top element - state with least heuristics
        // since we are comparing custom classes, need to provide a comparator: compare
        // by totalDistance heuristic
        var openQueue = new PriorityQueue<BoardState>(100, Comparator.comparing(state -> state.getTotalDistance()));
        // states that are in the queue, or were already processed
        var explored = new HashMap<Integer, BoardState>();

        openQueue.add(rootState);
        while (!openQueue.isEmpty()) {
            // v has the lowest totalDistance from all nodes
            var currentNode = openQueue.poll();
            explored.put(currentNode.hashCode(), currentNode);

            // TODO: remove this in final version, used only in testing
            numberOfVisitedStates++;

            // if v is target, then target.totalDistance is lowest in the queue
            // so, we have found the optimal solution.
            if (currentNode.isTarget()) {
                return currentNode;
            }
            var reachableStates = currentNode.getReachableStates();

            // look at all states reachable from current
            reachableStates.forEach(child -> {
                // do not process parent
                if (currentNode.getParent() != null && currentNode.getParent().equals(child))
                    return;

                if (explored.containsKey(child.hashCode())) {
                    // child is already in closedSet, check if totalDistance can be decreased
                    var prev = explored.get(child.hashCode());

                    if (prev.getTotalDistance() > child.getTotalDistance()) {
                        // if yes, send back to queue
                        // commented because takes O(N)
                        // if(openQueue.contains(prev)) openQueue.remove(prev);

                        // prev.setCurrentDistance(child.getCurrentDistance());

                        // set parent to current node
                        child.setParent(currentNode);

                        openQueue.add(child);
                    }
                } else {
                    openQueue.add(child);
                    explored.put(child.hashCode(), child);
                }

            });

        }

        // win condition not found
        return null;
    }
    public BoardState Bfs() {
        var queue = new LinkedList<BoardState>();
        var explored = new HashMap<Integer, BoardState>();

        queue.add(rootState);
        explored.put(rootState.hashCode(), rootState);

        while (!queue.isEmpty()){
            var currentNode = queue.poll();

            numberOfVisitedStates++;

            if (currentNode.isTarget()) {
                return currentNode;
            }
            var reachableStates = currentNode.getReachableStates();

            reachableStates.forEach(s -> {
                if(explored.containsKey(s.hashCode())) return;

                explored.put(s.hashCode(), s);

                s.setParent(currentNode);
                queue.add(s);
            });
        }

        return null;
    }
    public int getNumberOfVisitedStates() {
        return numberOfVisitedStates;
    }
}
