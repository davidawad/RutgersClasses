import java.util.Collection;

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

public interface SearchState {

    /*
     * Note: SearchState classes should override the methods
     * equals and hashCode that are attributes of every Java Object.
     * = equals() should make sure that SearchStates that 
     *   represent the same problem-solving situation
     *   are treated as equal; this enables the frontier
     *   structure to correctly detect and avoid duplicates
     *   in search (which is key to efficiency in this assignment).
     *   (The default implementation of equals() is true only if
     *   two objects are stored at the same location in memory.)
     *   See TestSearchState.java for an illustrative example.
     * = hashCode() should assign each SearchState an integer
     *   using only the semantic information used by equals().
     *   Basic Java data structures require the guarantee that
     *   x.hashCode() == y.hashCode() whenever x.equals(y).
     *   The typical strategy is to use XOR (^) to combine
     *   together the results of calling hashCode() on the
     *   meaningful fields of the data structure.
     *   See TestSearchState.java for an illustrative example.
     */

    /**
     * This Builder class illustrates the builder pattern
     * to get the initial state 
     * corresponding to a search problem.
     */
    abstract public static class Builder {
	/**
	 * @param sentence specification of the problem instance to solve
	 * @return initial search state corresponding to this problem
	 */
	abstract public SearchState makeInitialState(String problem);
    }

    /**
     * @return true if this search state instance represents a solution
     */
    public boolean isGoal();

    /**
     * @return the names of all the actions that are possible in this state
     */
    public Collection<String> getApplicableActions();

    /**
     * @param action the name of an action that could be applied in this state
     * @return the cost of taking that action in this state
     */
    public double getActionCost(String action);
    
    /**
     * @param action the name of an action that could be applied in this state
     * @return the successor state obtained by applying that action next
     */
    public SearchState applyAction(String action);

}
