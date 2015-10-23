/**
 *  Search Stats Class.
 *
 * @author Matthew Stone
 * @version 1.0
 * 
 * Data structure for monitoring the results of search.
 * For search homework for CS 440, Oct 2015.
 * 
 */
public class SearchStats {
	
	/** The start time for the ongoing search. */
	private long startTime;
	
	/** The end time of the ongoing search. */
	private long endTime;
	
	/** The number of leaves explored. */
	private int leafCt;
	
	/** The total depths of the leaves. */
	private int leafDepths;
	
	/** The number of solutions obtained (0 or 1). */
	private int solutionCt;
	
	/** The depth of the solution found. */
	private int solutionDepth;
	
	/** The number of internal nodes expanded. */
	private int expansions;
	
	/** The total depths of the nodes expanded. */
	private int expansionDepths;
	
	/** A counter for user-defined events, incremented on calls to tick(). */
	private int userEvents;
	
	/**
	 * Initialize the statistics for a new search.
	 */
	public SearchStats() {
		startTime = System.nanoTime();
		endTime = startTime;
		leafCt = 0;
		leafDepths = 0;
		solutionCt = 0;
		solutionDepth = 0;
		expansions = 0;
		expansionDepths = 0;
		userEvents = 0;
	}
	
	/**
	 * Record that a new node is being expanded.
	 *
	 * @param depth the depth of the current node
	 */
	public void expand(int depth) {
		expansions += 1;
		expansionDepths += depth;
	}

	/**
	 * Record that you've reached a dead end in search.
	 *
	 * @param depth the depth of the dead end
	 */
	public void deadEnd(int depth) {
		leafCt += 1;
		leafDepths += depth;
	}
	
	/**
	 * Record that you've found a solution in search.
	 *
	 * @param depth the depth of the solution
	 */
	public void succeed(int depth) {
		solutionCt = 1;
		solutionDepth = depth;
		endTime = System.nanoTime();
	}
	
	/**
	 * Record that the search has been exhausted without 
	 * finding a solution.
	 */
	public void fail() {
		endTime = System.nanoTime();
	}
	
	/**
	 * Update the number of user defined events by one.
	 */
	public void tick() {
		userEvents += 1;
	}
	
	/**
	 * Sample: return true after one tick() out of every interval.
	 *
	 * @param interval how frequently to sample
	 * @return true, if this tick() is a multiple of the interval
	 */
	public boolean sample(int interval) {
		return (userEvents % interval == 0);
	}
	
	/**
	 * Summary.  
	 *
	 * @return a textual description of the statistics
	 * accumulated during this search.
	 */
	public String summary() {
		StringBuilder b = new StringBuilder();
		if (solutionCt == 0) {
			b.append("Failed search.\n");
		}
		else {
			b.append("Successful search.\n");
			b.append("Solution found at depth " + 
					Integer.toString(solutionDepth) +
					".\n");
		}
		b.append("Elapsed time: " + 
				Long.toString((endTime - startTime)/1000000) +
				" ms.\n");
		if (expansions > 0) {
			b.append("Expanded " + 
				Integer.toString(expansions) +
				" nodes of average depth " +
				Float.toString(expansionDepths / (float) expansions) +
				".\n");
		}
		if (leafCt > 0) {
			b.append("Reached " + 
				Integer.toString(leafCt) +
				" leaves at average depth " +
				Double.toString(leafDepths / (double) leafCt) +
				".\n");
			b.append("Imputed branching factor: " +
				Double.toString(Math.exp(leafCt * Math.log((double) leafCt) / leafDepths)) +
				".\n");
		}
		if (userEvents > 0) {
			b.append("Executed " + Integer.toString(userEvents) +
					"calls to tick.\n");
		}
		return b.toString();
	}

}
