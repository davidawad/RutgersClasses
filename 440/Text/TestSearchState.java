import java.util.ArrayList;
import java.util.Collection;

/**
 * Search, learning and problem solving: TestSearchState.java
 * Sample search problem as a model for your implementations.
 * This file describes the following situation.
 *
 * You have access to an arbitarily large supply of flour and a work
 * area.  You have a 5 cup measure and a 3 cup measure.  You want to
 * measure out precise quantities of flour.
 *
 * The work area starts out empty. You can fill the measures to move
 * the measured quantity of flour from the supply to the work area or
 * to dump out quantities from the work area into the trash.  
 *
 * You can only move full measures (and there needs to be enough flour
 * to fill them).  It costs $6 to get a 3 cup measure and $9 to get a
 * 5 cup measure.
 *
 * @author Matthew Stone
 * @version 1.0
 */

public class TestSearchState implements SearchState {

    /** action name: move three cups of flour from supply to work area */
    private static final String ADD_THREE = "add three";
    /** action name: move five cups of flour from supply to work area */
    private static final String ADD_FIVE = "add five";
    /** action name: move three cups of flour from work area to trash */
    private static final String DUMP_THREE = "dump three";
    /** action name: move five cups of flour from work area to trash */
    private static final String DUMP_FIVE = "dump five";

    /** how much flour is in the work area */
    final int quantity;
    /** how much flour do you want to have in the work area */
    final int goal;

    /**
     * Constructor
     * @param quantity current amount of flour you have in this state
     * @param goal desired amount of flour in this state
     */
    private TestSearchState(int quantity, int goal) {
	this.quantity = quantity;
	this.goal = goal;
    }

    /*
     * Note: SearchState classes should override the methods
     * equals and hashCode that are attributes of every Java Object.
     * = equals() should make sure that SearchStates that 
     *   represent the same problem-solving situation
     *   are treated as equal; this enables the frontier
     *   structure to correctly detect and avoid duplicates
     *   in search (which is key to efficiency in this assignment).
     * = hashCode() should assign each SearchState an integer
     *   using only the semantic information used by equals().
     *   Basic Java data structures require the guarantee that
     *   x.hashCode() == y.hashCode() whenever x.equals(y).
     */

    /**
     *   Test for equality that reflects the semantics of search states.
     *   Two TestSearchState objects that have the same quantity of flour
     *   and the same target amount of flour are the same.
     *   (The default implementation of equals() is true only if
     *   two objects are stored at the same location in memory.)
     *   This code illustrates the general strategy for overriding
     *   equals: make sure you have instances of the right type,
     *   then make sure that their attributes agree.
     *
     * @param other Object to test for equality
     */
    @Override 
    public boolean equals(Object other) {
	if (other == null) return false;
	if (other == this) return true;
	if (!(other instanceof TestSearchState)) return false;
	TestSearchState otherState = (TestSearchState) other;
	return (otherState.quantity == this.quantity &&
		otherState.goal == this.goal);
    }

    /**
     *  Assign a hash value to be used in indexing TestSearchState
     *  objects.  Uses only the attributes that matter for 
     *  equality: the two fields quantity and goal in the state.
     *  The typical strategy for overriding hashCode() is to use XOR (^) 
     *  to combine together the results of calling hashCode() on the
     *  meaningful fields of the data structure - in this case though
     *  we just have integers so we just XOR them together directly.
     *
     * @return hash value for indexing TestSearchState
     */
    @Override 
    public int hashCode() {
	return quantity ^ goal;
    }

    /**
     * This Builder class illustrates the builder pattern
     * to get the initial state 
     * corresponding to a search problem.
     */
    public static class Builder extends SearchState.Builder {
	/**
	 * Make the search space to get a specified targetQuantity of
	 * flour into the workspace by the available operations.
	 *
	 * @param targetQuantity specification of the problem instance to solve
	 * @return initial search state corresponding to this problem
	 */
	public SearchState makeInitialState(String targetQuantity) 
	    throws IllegalArgumentException {
	    try {
		return new TestSearchState(0, Integer.parseInt(targetQuantity));
	    } catch (NumberFormatException ex) {
		throw new IllegalArgumentException("Need number for initial test state, got " +
						   targetQuantity);
	    }
	}
    }

    /**
     * We have a goal state when we have actually measured out
     * the target quantity of flour into the workspace.
     *
     * @return true if this search state instance represents a solution
     */
    public boolean isGoal() {
	return quantity == goal;
    }

    /**
     * We can always add more flour.  We can also dump a measured amount flour out,
     * provided that we have at least that much flour in the workspace already.
     *
     * @return the names of all the actions that are possible in this state
     */
    public Collection<String> getApplicableActions() {
	ArrayList<String> actions = new ArrayList<String>();
	actions.add(ADD_THREE);
	actions.add(ADD_FIVE);
	if (quantity >= 3) actions.add(DUMP_THREE);
	if (quantity >= 5) actions.add(DUMP_FIVE);
	return actions;
    }

    /**
     * @param action the name of an action that could be applied in this state
     * @return the cost of taking that action in this state
     */
    public double getActionCost(String action) {
	if (action.equals(ADD_THREE)) {
	    return 6;
	} else if (action.equals(ADD_FIVE)) {
	    return 9;
	} else {
	    return 0;
	}
    }

    /**
     * When we apply an action, we change the amount of flour that we have,
     * but do not change the amount of flour that we are looking for...
     *
     * @param action the name of an action that could be applied in this state
     * @return the successor state obtained by applying that action next
     */
    public TestSearchState applyAction(String action) {
	if (action.equals(ADD_THREE)) {
	    return new TestSearchState(quantity + 3, goal);
	} else if (action.equals(ADD_FIVE)) {
	    return new TestSearchState(quantity + 5, goal);
	} else if (action.equals(DUMP_THREE)) {
	    return new TestSearchState(quantity - 3, goal);
	} else if (action.equals(DUMP_FIVE)) {
	    return new TestSearchState(quantity - 5, goal);
	} else {
	    return this;
	}
    }	
}
