import java.io.*;
import java.lang.Math;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

/**
 * Search, learning and problem solving: SearchState.java
 * Interface for constructing search problems.
 * This file summarizes the information that you have
 * to implement to solve a search problem using our
 * generic search method.
 *
 * Adapted by Matthew Stone from Roy Frostig's 
 * text reconstruction assignment (util.py)
 * for the Stanford AI class.
 *
 * @author Matthew Stone
 * @version 1.0
 */

public class Search {
    
    /** Exception raised when the search problem is done */
    static class SearchLimitReached extends Exception {
        static final long serialVersionUID = 1L;
    }

    /** Upper bound on the number of search nodes to explore */
    static final int SEARCH_LIMIT = 100000;

    /** How to build new nodes -- uniform cost search */
    private static Node.NodeFactory extender = Node.extender;
    
    ////////////////////////////////////////////////////////////////////
    // Instance Members.
    ////////////////////////////////////////////////////////////////////

    /** Counts the number of steps taken in search */
    private int stateVisits;

    /** Manages queue of nodes and states */
    private Frontier frontier;
    
    /**
     * Constructor - create and initialize a search process
     * to find a solution starting at the associated start state
     * 
     * @param start initial search state
     */
    public Search(SearchState start) {
	stateVisits = 0;
	frontier = new Frontier();
        frontier.update(new Node(start));
    }

    /**
     * Search for a solution
     * 
     * @return solution path if found, null otherwise
     * @throws SearchLimitReached
     */
    public Node solve() throws SearchLimitReached {

	while (stateVisits < SEARCH_LIMIT) {
	    Node currentNode = frontier.next();
	    if (currentNode == null) {
		return null;
	    } else if (currentNode.getState().isGoal()) {
		return currentNode;
	    } 
	    for (String action : currentNode.getState().getApplicableActions()) {
		Node next = extender.extend(currentNode, action);
		frontier.update(next);
		stateVisits++;
	    }
	}
	throw new SearchLimitReached();
    }
}
