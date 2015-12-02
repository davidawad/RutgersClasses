import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class representing a node in a search space.
 * Refactored from search assignment to eliminate drawing functionality,
 * and bake in least-cost-first search.
 * 
 * @author Matthew Stone
 * @version 3.0
 */
public class Node {
    
    ////////////////////////////////////////////////////////////////////
    // Static Parameters.
    ////////////////////////////////////////////////////////////////////

    /** allows nodes to be assigned a unique identifier */
    private static int counter = 0;
    
    /** Procedure to generate new nodes in the search space */
    public static interface NodeFactory {
        public Node extend(Node n, String s);
    }
    
    /** Default Node Factory */
    public static NodeFactory extender = 
	new NodeFactory() {
	    public Node extend(Node n, String t) {
		return new Node(n, t);
	    };
	};

    ////////////////////////////////////////////////////////////////////
    // Instance Members.
    ////////////////////////////////////////////////////////////////////

    /** unique identifier for node */
    protected final int id;
    
    /** search path that precedes the most recent transition */
    protected final Node parent;
    
    /** state where this search path concludes */
    protected final SearchState state;
    
    /** most recent transition on search path */
    protected final String lastAction;
    
    /** actual cost accrued so far along search path */
    protected final double pathCost;
    
    /** priority associated with node, e.g., (A*) cost + heuristic */
    protected final double priority;

    ////////////////////////////////////////////////////////////////////
    // Search functionality.
    ////////////////////////////////////////////////////////////////////

    /**
     * Comparator object 
     * that gives an ordering over nodes such that
     * i) first node precedes second whenever first has lower priority
     * ii) two nodes are unordered only if they are identical
     */
    static final Comparator<Node> CMP = new Comparator<Node>() {
        public int compare(Node n1, Node n2) {
            if (n1.priority < n2.priority)
                return -1;
            if (n1.priority > n2.priority)
                return 1;
            if (n1.id > n2.id)
                return -1;
            if (n1.id < n2.id)
                return 1;
            return 0;
        }
    };

    /**
     * Method to calculate the priority of a node
     * Should be overridden by node types for specific
     * search algorithms
     * 
     * @return priority for new node
     */
    protected double score() {
        return this.pathCost;
    }
    
    /**
     * Constructor for the initial node of a search problem.
     * 
     * @param state the starting vertex (start or goal)
     */
    public Node(SearchState state) {
        this.id = counter++;
        this.state = state;
        parent = null;
        this.lastAction = null;
        this.pathCost = 0;
        this.priority = score();
    }
    
    /**
     * Constructor to extend a node by one step
     * 
     * @param parent node being extended
     * @param lastAction additional edge to traverse
     */
    public Node(Node parent, String action) {
        this.id = counter++;
	this.state = parent.state.applyAction(action);
        this.parent = parent;
        this.lastAction = action;
        this.pathCost = parent.pathCost + parent.state.getActionCost(action);
        this.priority = score();
    }
    
    /**
     * @return the state at which this search path concludes
     */
    public SearchState getState() {
        return state;
    }
    
    /**
     * Get the priority of the node
     * 
     * @return node's priority
     */
    public double getPriority() {
	return this.priority;
    }

    /**
     * @return a text summary of the actions applied in node in order
     */
    public String history() {
	ArrayList<String> description = new ArrayList<String>();
	Node n = this;
	while (n.lastAction != null) {
	    description.add(n.lastAction);
	    n = n.parent;
	}
	Collections.reverse(description);
	return LangUtil.join(description, " : ");
    }
}

