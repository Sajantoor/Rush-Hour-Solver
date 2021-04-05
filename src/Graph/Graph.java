package Graph;

import ParseFile.Board;

import java.util.*;

public class Graph {
    private ArrayList<BoardState> nodes;
    private BoardState rootState;

    public Graph(Board board){
        rootState = new BoardState(board);
    }
    
    private BoardState AStarTraversal(){
        // min priorityQueue for A* traversal
        // top element - state with least heuristics
        // since we are comparing custom classes, need to provide a comparator: compare by totalDistance heuristic
        var openQueue = new PriorityQueue<BoardState>(100, Comparator.comparing(state -> state.getTotalDistance()));
        // states that are in the queue, or were already processed
        var explored = new HashMap<Integer, BoardState>();

        openQueue.add(rootState);
        while(!openQueue.isEmpty()){
            // v has the lowest totalDistance from all nodes
            var currentNode = openQueue.poll();

            explored.put(currentNode.hashCode(), currentNode);

            // if v is target, then target.totalDistance is lowest in the queue
            // so, we have found the optimal solution.
            if(currentNode.isTarget()){
                return currentNode;
            }
            var reachableStates = currentNode.getReachableStates();

            // look at all states reachable from current
            reachableStates.forEach(child -> {
                // do not process parent
                if(currentNode.getParent().equals(child)) return;

                if(explored.containsKey(child.hashCode())){
                    // child is already in closedSet, check if totalDistance can be decreased
                    var prev = explored.get(child.hashCode());

                    if(prev.getTotalDistance() > child.getTotalDistance()){
                        // if yes, send back to queue
                        if(openQueue.contains(prev)) openQueue.remove(prev);

                        prev.setCurrentDistance(child.getCurrentDistance());

                        // set parent to current node
                        child.setParent(currentNode);

                        openQueue.add(child);
                    }
                }

            });

        }

        return null;
    }

}
