package Graph;

import ParseFile.Board;

import java.util.*;

public class Graph {
    private BoardState rootState;

    public Graph(Board board) {
        rootState = new BoardState(board);
    }

    /**
     * A star traversal of the graph
     * 
     * @return The final solution or null if it fails
     */
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
            if (explored.containsKey(currentNode.hashCode())) {
                var stored = explored.get(currentNode.hashCode());
                if (stored.getCurrentDistance() < currentNode.getCurrentDistance())
                    continue;
            } else
                explored.put(currentNode.hashCode(), currentNode);

            // if v is target, then target.totalDistance is lowest in the queue
            // so, we have found the optimal solution.
            if (currentNode.isTarget()) {
                return currentNode;
            }

            var reachableStates = currentNode.getReachableStates(false);

            // look at all states reachable from current
            reachableStates.forEach(child -> {
                // do not process parent
                if (currentNode.getParent() != null && currentNode.getParent().equals(child))
                    return;

                if (explored.containsKey(child.hashCode())) {
                    // child is already in closedSet, check if totalDistance can be decreased
                    var stored = explored.get(child.hashCode());
                    if (stored.getCurrentDistance() > child.getCurrentDistance()) {
                        // if yes, send back to queue
                        stored.setCurrentDistance(child.getCurrentDistance());
                        child.setApproximateDistance(stored.getApproximateDistance());
                        // set parent to current node
                        child.setParent(currentNode);
                        stored.setParent(currentNode);
                        openQueue.add(child);
                    }
                } else {
                    child.computeApproximateDistance();
                    openQueue.add(child);
                    explored.put(child.hashCode(), child);
                }
            });

        }

        // win condition not found
        return null;
    }

    /**
     * BFS traversal of the graph
     * 
     * @return The final solution or null if it fails
     */
    public BoardState Bfs() {
        var queue = new LinkedList<BoardState>();
        var explored = new HashMap<Integer, BoardState>();

        queue.add(rootState);
        explored.put(rootState.hashCode(), rootState);

        while (!queue.isEmpty()) {
            var currentNode = queue.poll();

            if (currentNode.isTarget()) {
                return currentNode;
            }

            var reachableStates = currentNode.getReachableStates(false);

            reachableStates.forEach(s -> {
                if (explored.containsKey(s.hashCode()))
                    return;

                explored.put(s.hashCode(), s);

                s.setParent(currentNode);
                queue.add(s);
            });
        }

        return null;
    }
}
