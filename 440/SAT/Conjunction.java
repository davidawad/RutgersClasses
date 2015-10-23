import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Map;


// TODO: Auto-generated Javadoc
/**
 * The Class Conjunction.
 *
 * @author Matthew Stone
 * @version 1.0
 * 
 * A representation of a conjunction of clauses,
 * together with an assignment of truth values to
 * literals that have been considered so far,
 * and a list of the variables that need to be assigned,
 * as a state representation for doing propositional
 * satisfiability search.
 * For search homework for CS 440, Oct 2015.
 */
public class Conjunction {
	
	/** A list of disjunctions representing clauses to satisfy.
	 *  Stored in increasing order by size. */
	private LinkedList<Disjunction> disjunctions;
	
	/** The assignment of truth values to propositions so far. */
	private HashMap<String, Boolean> assignments;
	
	/** The decisions made so far in search. */
	private HashMap<String, Boolean> decisions;
	
	/**  Justification for decisions made. */
	private HashMap<String, TreeSet<String>> justifications;
	
	/** The unassigned variables that occur in the problem. */
	private TreeSet<String> variables;
	
	/**
	 * Instantiates a new conjunction given a passed collection
	 * of clauses.
	 *
	 * @param ds a collection of clauses
	 */
	Conjunction(Collection<Disjunction> ds) {
		disjunctions = new LinkedList<Disjunction>(ds);
		Collections.sort(disjunctions, Disjunction.disjunctionComparator);

		assignments = new HashMap<String, Boolean>();
		decisions = new HashMap<String, Boolean>();
		justifications = new HashMap<String, TreeSet<String>>();
		
		variables = new TreeSet<String>();
		for (Disjunction d : disjunctions) {
			variables.addAll(d.getVariables());
		}
	}

	/**
	 * Builds a new conjunction exactly like a model conjunction
	 * except that the variable v is assigned the truth value b.
	 *
	 * @param c the model conjunction
	 * @param v the variable to assign
	 * @param b the truth value to give
	 */
	@SuppressWarnings("unchecked")
	private Conjunction(Conjunction c, String v, Boolean b) {
		assignments = (HashMap<String, Boolean>)(c.assignments.clone());
		decisions = (HashMap<String, Boolean>)(c.decisions.clone());
		variables = (TreeSet<String>)(c.variables.clone());
		justifications = (HashMap<String, TreeSet<String>>)(c.justifications.clone());
		disjunctions = new LinkedList<Disjunction>(c.disjunctions);
		TreeSet<String> vj = new TreeSet<String>();
		vj.add(v);
		decisions.put(v, b);
		this.assign(v, b, vj);
	}

	/**
	 * Checks if the conjunction is transparently inconsistent,
	 * because it contains an empty (always false) disjunct.
	 *
	 * @return true, if is inconsistent
	 */
	public boolean isInconsistent() {
		Disjunction d = disjunctions.peek();
		if (d != null && d.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if is the conjunction is transparently consistent, 
	 * because it does not contain any disjuncts any more.
	 *
	 * @return true, if is consistent
	 */
	public boolean isConsistent() {
		Disjunction d = disjunctions.peek();
		if (d == null) {
			return true;
		}
		return false;
	}

	
	/**
	 * Create a clause summarizing what went wrong when an assignment
	 * turns out to be inconsistent.  Use the justification structure 
	 * of the current state to retrieve all the decisions that led to
	 * this inconsistency.  One of these decisions must be changed in
	 * a satisfying assignment, so return the disjunction of the negation
	 * of these decisions.
	 *
	 * @return a new clause derived from the decisions made summarizing
	 *   what has to change in a satisfying assignment.
	 */
	private Disjunction conflictInducedClause() {
		Disjunction d = disjunctions.peek();
		if (d != null && d.size() == 0) {
			Disjunction result = new Disjunction();
			for (String v : d.getSource().getVariables()) {
				for (String j : justifications.get(v)) {
					result.put(j, new Boolean(!assignments.get(j)));
				}
			}
			return result;
		}
		return null;
	}
	
	/**
	 * Get the alphabetically first variable not yet assigned
	 * a truth value.
	 *
	 * @return the name of the target variable
	 */
	private String alphabeticallyFirstVariable() {
		return variables.first();
	}
	
	/**
	 * Get the variable that occurs most often across the
	 * unsatisfied clauses that is not yet assigned
	 * a truth value.
	 *
	 * @return the name of the target variable
	 */
	private Assignment mostFrequentVariable() {
		HashMap<String, Integer> pos = new HashMap<String, Integer>();
		HashMap<String, Integer> neg = new HashMap<String, Integer>();
		for (Disjunction d: disjunctions) {
			d.updateCounts(pos, neg);
		}
		Assignment result = null;
		int value = 0;
		for (Map.Entry<String, Integer> e: pos.entrySet()) {
			int vt = e.getValue();
			int vf = 0;
			if (neg.containsKey(e.getKey())) {
				vf += neg.get(e.getKey());
			}
			if (vt + vf > value) {
				result = new Assignment(e.getKey(), new Boolean(vt > vf));
				value = vt + vf;
			}
		}
		for (Map.Entry<String, Integer> e: neg.entrySet()) {
			if (!pos.containsKey(e.getKey())) {
				int v = e.getValue();
				if (v > value) {
					result = new Assignment(e.getKey(), new Boolean(false));
					value = v;
				}
			}
		}
		return result;		
	}

	/**
	 * Get the variable that occurs most often across the
	 * unsatisfied clauses that is not yet assigned
	 * a truth value.
	 *
	 * @return the name of the target variable
	 */
	private Assignment mostFrequentLiteral() {
		HashMap<String, Integer> pos = new HashMap<String, Integer>();
		HashMap<String, Integer> neg = new HashMap<String, Integer>();
		for (Disjunction d: disjunctions) {
			d.updateCounts(pos, neg);
		}
		Assignment result = null;
		int value = 0;
		for (Map.Entry<String, Integer> e: pos.entrySet()) {
			int v = e.getValue();
			if (v > value) {
				result = new Assignment(e.getKey(), new Boolean(true));
				value = v;
			}
		}
		for (Map.Entry<String, Integer> e: neg.entrySet()) {
			int v = e.getValue();
			if (v > value) {
				result = new Assignment(e.getKey(), new Boolean(false));
				value = v;
			}
		}
		return result;		
	}

	/**
	 * Get the variable that occurs most often across the
	 * unsatisfied clauses that is not yet assigned
	 * a truth value.
	 *
	 * @return the name of the target variable
	 */
	private Assignment mostBalancedLiteral() {
		HashMap<String, Integer> pos = new HashMap<String, Integer>();
		HashMap<String, Integer> neg = new HashMap<String, Integer>();
		for (Disjunction d: disjunctions) {
			d.updateCounts(pos, neg);
		}
		Assignment result = null;
		int value = 0;
		for (Map.Entry<String, Integer> e: pos.entrySet()) {
			if (!neg.containsKey(e.getKey())) {
				continue;
			}
			int split = Math.min(e.getValue(), neg.get(e.getKey()));
			if (split > value) {
				result = new Assignment(e.getKey(), new Boolean(e.getValue() > neg.get(e.getKey())));
				value = split;
			}
		}
		return result;		
	}

	/**
	 * Get the justifications involved in inferring a value
	 * for variable v through inference from the passed disjunction.
	 *
	 * @param v the variable we want to set by inference
	 * @param clause the clause we are using to do the inference
	 * @return the set of decision variables required for the inference
	 */
	private TreeSet<String> inferFromClause(String v, Disjunction clause) {
		TreeSet<String> dependencies = new TreeSet<String>();
		for (String s: clause.getVariables()) {
			if (!s.equals(v)) {
				dependencies.addAll(justifications.get(s));
			}
		}			
		return dependencies;
	}
	
	/**
	 * Destructively update the current conjunction to reflect
	 * the constraint that variable v should take on truth 
	 * value b.  Because this method is destructive, it cannot be
	 * used for search; you cannot undo it to consider other
	 * possibilities.  However, you can use it in constraint
	 * propagation to make the logical consequences of the 
	 * current representation explicit.  If source is not null,
	 * then this assignment is derived by inference 
	 * (for example via unit propagation), so link the assignment
	 * to the variables that triggered the inference.  Otherwise,
	 * assume that this assignment is a standalone decision.
	 *
	 * @param v the variable to assign
	 * @param b the truth value to give it
	 * @param dependencies the dependencies
	 */
	private void assign(String v, Boolean b, TreeSet<String> dependencies) {
		assignments.put(v, b);
		variables.remove(v);
		justifications.put(v, dependencies);
		LinkedList<Disjunction> simplified = new LinkedList<Disjunction>();
		for (Disjunction x: disjunctions) {
			Disjunction r = x.simplifyWith(v, b);
			if (r != null) {
				simplified.add(r);
			}
		}
		disjunctions = simplified;
		Collections.sort(disjunctions, Disjunction.disjunctionComparator);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		LinkedList<String> clauses = new LinkedList<String>();
		for (Disjunction d: disjunctions) {
			clauses.add(d.toString());
		}
		return Disjunction.join("\n", clauses);
	}
	
	/**
	 * Parses a text representation of a satisfiability problem in 
	 * DIMACS CNF form.
	 *
	 * @param r a reader for accessing text data
	 * @return the problem specified in the text 
	 * @throws IOException Signals that we did not succeed in reading
	 *    a DIMACS CNF formatted satisfiability problem on r
	 */
	public static Conjunction parseCNF(BufferedReader r) throws IOException {
		LinkedList<Disjunction> ds = new LinkedList<Disjunction>();
		Disjunction d = null;
		String line = null;
		while ((line = r.readLine()) != null) {
			// break by whitespaces except when preceded by -
			String[] tokens = line.split("(?<!-)(\\b|^)\\s+");
			// skip over empty lines, comments and problem statement lines
			if (tokens.length == 0 || tokens[0].equals("c") || tokens[0].equals("p")) {
				continue;
			}
			for (int i = 0; i < tokens.length; i++) {
				// skip empty strings
				if (tokens[i].length() == 0) {
					continue;
				}
				// 0 is end of clause delimiter
				if (tokens[i].equals("0")) {
					if (d != null) {
						ds.add(d);
						d = null;
					}
					continue;
				}
				if (d == null) {
					d = new Disjunction();
				}
				// get the next literal, should consume the entire token
				int charsRead = d.putReadLiteral(tokens[i]);
				if (tokens[i].length() != charsRead) {
					throw new IOException("Bad clause format: " + tokens[i].substring(charsRead));
				}
			}
		}
		// maybe the last disjunction was not 0-terminated.
		if (d != null) {
			ds.add(d);
		}
		return new Conjunction(ds);
	}
	
	/**
	 * From string.
	 *
	 * @param s the s
	 * @return the conjunction
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Conjunction fromString(String s) throws IOException {
		String[] clauses = s.split("\n");
		LinkedList<Disjunction> ds = new LinkedList<Disjunction>();
		for (String c: clauses) {
			ds.add(Disjunction.fromString(c));
		}
		return new Conjunction(ds);
	}
	
	/**
	 * propagate unit clauses.
	 * 
	 * Simplify the current conjunction to take unit clauses
	 * into account: each unit clause specifies a value for
	 * a variable that has to hold if the conjunction is 
	 * consistent, so you should use that value everywhere
	 * in the conjunction.
	 */
	void propagateUnitClauses() {
		do {
			Disjunction d = disjunctions.peek();
			if (d == null || d.size() != 1) {
				break;
			}
			assign(d.unitVar(), d.unitVal(), inferFromClause(d.unitVar(), d.getSource()));
		} while (!this.isInconsistent());
	}

	/**
	 * Assume values for pure literals.
	 * 
	 * Pure literals occur with the same polarity everywhere
	 * in the disjunction: e.g., you always have x, or you 
	 * always have -x.  That means you can assume the value
	 * that occurs without loss of generality; if you only
	 * see 'x' there is no reason to try the possibility that
	 * x is false in search.  This function simplifies the
	 * passed conjunction iteratively so that there are no 
	 * pure literals remaining.  It performs the simplification
	 * iteratively: after you do one round of simplification,
	 * a whole bunch of clauses will generally drop out,
	 * so in the simpler problem there may be a bunch of 
	 * new pure variables.
	 */
	void assumePureLiterals() {
		do {
			HashMap<String, Boolean> pureVars = new HashMap<String, Boolean>();
			TreeSet<String> xVars = new TreeSet<String>();
			for (Disjunction d: disjunctions) {
				d.accumulatePureVars(pureVars, xVars);
			}
			if (pureVars.isEmpty()) {
				break;
			}
			for (Map.Entry<String, Boolean> e : pureVars.entrySet()) {
				this.assign(e.getKey(), e.getValue(), 
						new TreeSet<String>(decisions.keySet()));
			}
		} while (!this.isInconsistent());
	}

	/**
	 * Assignment Class.
	 * Helper class for pairing a string, representing a propositional
	 * variable, with a truth value, indicating the value we want to
	 * assume for the variable.  We need this as a return value for
	 * search control when we decide how to branch.  We want to pick
	 * a variable to split on and start by exploring what looks like
	 * its most promising truth value.
	 */
	static class Assignment {
		
		/** The variable. */
		public String v;
		
		/** The value to assign to the variable. */
		public Boolean b;
		
		/**
		 * Instantiates a new assignment.
		 *
		 * @param v variable
		 * @param b its value
		 */
		public Assignment (String v, Boolean b) {
			this.v = v;
			this.b = b;
		}
	}
	
	/**
	 * The Enum SearchStatus: the qualitatively different
	 * ways search can complete.
	 */
	public static enum SearchStatus {
		
		/** Search has succeeded and we have a result. */
		Success, 
		/** Search has failed and we have exhausted the search space 
		 * and may know what went wrong. */
		Failure, 
		/** We abandoned the search before getting an answer. */
		Timeout;
	};
	
	/**
	 * SearchResult Class.
	 * Helper class for encapsulating the results of search.
	 * When search succeeds, we want to return a specification
	 * of the solution.  Here this takes the form of an 
	 * assignment of truth values to the proposition variables
	 * that occur in the formula.  When search fails, we may
	 * also want to return an explanation for the failure
	 * to help guide future search.  Here this takes the form
	 * of a clause specifying how to avoid the conflict that 
	 * caused inconsistency at this level of search.
	 */
	public static class SearchResult {
		
		/** Whether the search has been successful. */
		private SearchStatus status;

		/** The assignment. */
		private HashMap<String, Boolean> assignment;
		
		/** The conflict. */
		private Disjunction conflictInducedClause;
		
		/**
		 * Instantiates a new search result in case of failure.
		 *
		 * @param conflictInducedClause a clause describing the how to avoid
		 *   the inconsistency that brought the search down
		 */
		public SearchResult(Disjunction conflictInducedClause) {
			this.status = SearchStatus.Failure;
			this.conflictInducedClause = conflictInducedClause;
			this.assignment = null;
		}

		/**
		 * Instantiates a new search result in case of success.
		 *
		 * @param assignment an assignment of truth values to
		 *   proposition letters that makes the formula true.
		 */
		public SearchResult(HashMap<String, Boolean> assignment) {
			this.status = SearchStatus.Success;
			this.conflictInducedClause = null;
			this.assignment = assignment;
		}

		/**
		 * Instantiates a new search result for general reasons.
		 *
		 * @param status the status
		 */
		public SearchResult(SearchStatus status) {
			this.status = status;
			this.conflictInducedClause = null;
			this.assignment = null;
		}
		
		/**
		 * Indicates if this search result represents a solution,
		 * a failure or a timeout.
		 *
		 * @return the status of the search result
		 */
		public SearchStatus getStatus() {
			return this.status;
		}

		/**
		 * Gets a clause recording how to avoid the last inconsistency.
		 * Only meaningful if status is failure.
		 * 
		 * @return the conflict
		 */
		public Disjunction getConflictInducedClause() {
			return conflictInducedClause;
		}

		/**
		 * Gets the satisfying assignment.
		 * Only meaningful if status is success.
		 *
		 * @return the assignment
		 */
		public HashMap<String, Boolean> getAssignment() {
			return assignment;
		}		
	}
	
	/**
	 * The Interface SearchControl.
	 * This HelperClass describes a particular strategy for carrying
	 * out DFS search to solve a propositional satisfiability problem.
	 * There are three things that can vary:
	 * 1. what inference you do to propagate values in the search.
	 * 2. what strategy you use to pick the next decision variable
	 *    and its most promising value
	 * 3. whether you do dependency-directed backtracking
	 */
	public static interface SearchControl {
		
		/**
		 * Carry out inferences necessary to reflect
		 * logical consequences in c.
		 *
		 * @param c the conjunction representing the search state
		 */
		public void simplify(Conjunction c);
		
		/**
		 * Pick the next decision variable and the truth value
		 * to explore first in search.
		 *
		 * @param c the conjunction representing the search state
		 * @return the assignment to try next
		 */
		public Assignment pick(Conjunction c);
		
		/**
		 * Should you keep track of the reasons for failures
		 * and try to use them to avoid similar failures in the future?.
		 *
		 * @return true, if you should try dependency-directed backtracking
		 */
		public boolean backjump();
	}
	
	/** A vanilla search. */
	public static SearchControl vanillaSearch = new SearchControl() {
		public void simplify(Conjunction c) {;}
		public Assignment pick(Conjunction c) { 
			return new Assignment(c.alphabeticallyFirstVariable(), new Boolean(true)); }
		public boolean backjump() { return false; }
	};
	
	/**
	 *  The most sophisticated search: with unit propagation, 
	 *  pure variable elimination, backjumping,
	 *  and a smart strategy for picking decision variables. 
	 */
	public static SearchControl superFancySearch = 
	    /* TBC: Your code here, replacing the line below */
	    vanillaSearch;
	
	/**
	 * Generic template for satisfiability search,
	 * parameterized by an instance of the SearchControl interface
	 * to specify details about the search.
	 * 
	 * @param control interface specifying how to manage search
	 * @param stats object to record search complexity
	 * @param depth number of decisions made (for statistics)
	 * @return search result structure describing what happened
	 */
	SearchResult search(SearchControl control, SearchStats stats, int depth) {
	    /* TBC: Your code here, replacing the line below */
	    return new SearchResult((Disjunction) null);
	}

	/**
	 * SearchThread Class.
	 * Allows a search to be run in a separate thread so you
	 * can limit the resources assigned to it,
	 * in particular the amount of time.
	 */
	static class SearchThread extends Thread {
		
		/** The CNF formula to search. */
		private Conjunction c;
		
		/** Keeps statistics on the search. */
		private SearchStats s;
		
		/** Stores the result for later access. */
		private SearchResult a;
		
		/** Describes how search should proceed. */
		private SearchControl control;
		
		/**
		 * Gets the result.
		 *
		 * @return the result of search
		 */
		public SearchResult getResult() {
			return a;
		}
		
		/**
		 * Gets the stats from the search.
		 *
		 * @return the stats
		 */
		public SearchStats getStats() {
			return s;
		}
				
		/**
		 * Instantiates a new search thread.
		 *
		 * @param c the formula to search
		 * @param control how to do the search
		 */
		public SearchThread(Conjunction c, SearchControl control) {
			super();
			this.c = c;
			this.s = new SearchStats();
			this.control = control;
			this.a = null;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			a = c.search(control, s, 0);
			if (a.getStatus() != SearchStatus.Success) 
				s.fail();
		}
	}
	
	/**
	 * Do search on test CNF file with specified search method and time limit.
	 *
	 * @param testFile the test file (in DIMACS CNF form) giving the SAT problem
	 * @param method search method to use
	 * @param maxMillisecs the maximum number of milliseconds to run the search
	 * @return a text description of the search results and resources
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String doSearchWithTimeLimit(String testFile, SearchControl method, long maxMillisecs) 
	throws IOException {
	    File initialFile = new File(testFile);
	    InputStream targetStream = new FileInputStream(initialFile);
	    BufferedReader br = new BufferedReader( new InputStreamReader( targetStream ) );
	    Conjunction c = parseCNF(br);
	    targetStream.close();
	    
  	    SearchThread t = new SearchThread(c, method);
  	    
  	    long startTime = System.currentTimeMillis();
  	    long endTime = startTime + maxMillisecs;

  	    t.start(); 

  	    while (System.currentTimeMillis() < endTime && t.isAlive()) {
  	    	try {
  	    		Thread.sleep(200L);  // Sleep 1/5 second
  	    	} catch (InterruptedException e) {
 
  	    	}
  	    }

  	    if (t.isAlive()) {
  	    	t.interrupt();
  	    }
  	    try {
  	    	t.join();      
  	    } catch (InterruptedException e) {
  	    	
  	    }
  	    
  	    SearchResult a = t.getResult();
  	    SearchStats s = t.getStats();
  	    String s1;
  	    switch (a.getStatus()) {
  	    case Success:
  	    	s1 = a.getAssignment().toString() + "\n";
  	    	break;
		case Timeout:
			s1 = "Time Limit Exceeded.\n";
			break;
  	    case Failure:
		default:
			s1 = "Unsatisfiable\n";
			break;
		}
	    return (s1 + s.summary());	
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
	    /* TBC: Explore some interesting examples, using patterns such as the following */
	    String o = doSearchWithTimeLimit("simple_v3_c2.cnf", vanillaSearch, 1000L);
		System.err.println(o);
  	}
}
