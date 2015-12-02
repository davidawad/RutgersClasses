import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.ArrayList;

/**
 * Class to manage the queue for a search space.  
 * Keep track of what states have been visited,
 * what the cost is for each state, 
 * what nodes are pending; 
 * Get the next node in priority order.
 *
 * Corresponds to util.py in Roy Frostig's Stanford 
 * text reconstruction assignment.
 *
 * Slightly redesigned from the version in the search assignment.  
 * Key changes:
 * - don't bother to remove nodes from the queue when 
 *   you find a better path; just make sure at removal time 
 *   that you are about to return the best path for its state
 * - track nodes in a priority queue (a simpler data structure)
 * - pair states with their best cost, or null if the state
 *   has been visited (rather than the best node)
 * 
 * In general, a search frontier has to be able to perform three
 * operations quickly:
 * - get the node with the lowest priority
 * - test whether there is a node in the frontier that visits
 *   a given state
 * - change (and specifically lower) the priority of a node
 * There are specialized data structures for this, but
 * the Java utilities don't implement any of them.
 * Rather than lowering the priority of a node, we have a way
 * of marking duplicate nodes as defunct so that we discard
 * them when they are at the top of the queue.
 * 
 * The variables here are PROTECTED, meaning that subclasses 
 * can take advantage of these variables as they create
 * specialized behavior for more specific kinds of frontiers.
 * 
 * @author Matthew Stone
 * @version 2.0
 *
 */
public class Frontier {

    /** 
     * stores a mapping from search states 
     * to the cost of the best path to the state
     * in the frontier, or null if the state
     * has already been visited and should not 
     * be considered further
     */
    protected HashMap<SearchState,Double> priorities;
    
    /**
     * gives the nodes in the frontier 
     * in the ordering given by Node.CMP.
     * Note that java specs dictate that
     * the correct operation of this structure
     * as a priority queue depends on two requirements:
     * - Node.CMP should return 0 (equal)
     *   only when the two nodes are 
     *   really the same.
     * - If n1 has lower priority than n2 
     *   then CMP(n1, n2) should be -1.
     */
    protected PriorityQueue<Node> queue;
    
    /**
     * Constructor.
     * Initializes both table and queue to empty structures.
     */
    public Frontier() {
        priorities = new HashMap<SearchState,Double>();
        queue = new PriorityQueue<Node>(11, Node.CMP);
    }
    
    /**
     * Adds node n to the frontier, if needed, by:
     * - listing it in queue (in priority order)
     * - indexing its state in priorities
     * 
     * @param n - new node
     */
    public void update(Node n) {
	SearchState s = n.getState();
	if (priorities.containsKey(s)) {
	    Double d = priorities.get(s);
	    if (d == null) {
		return; // already visited 
	    } else if (d.doubleValue() < n.getPriority()) {
		return; // better way to get there
	    }
	}
        queue.add(n);
        priorities.put(s, n.getPriority());
    }
    
    /**
     * removes and returns the lowest priority node
     * with a state that has not yet been processed
     * (states that are processed get priority null).
     * 
     * @return lowest priority node if any, null otherwise
     */
    public Node next() {
	Node n = null;
        do {
	    n = queue.poll();
	} while (n != null && priorities.get(n.getState()) == null);
	if (n != null) {
	    priorities.put(n.getState(), null);
	}
	return n;
    }
    
    /**
     * check if the frontier is empty
     * 
     * @return true if empty
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }
    
}
