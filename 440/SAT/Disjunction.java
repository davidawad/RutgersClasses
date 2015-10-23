import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Disjunction Class.
 *
 * @author Matthew Stone
 * @version 1.0
 * 
 * Data structure for operations on "clauses", 
 * defined as disjunctions of proposition literals.
 * For search homework for CS 440, Oct 2015.
 */
public class Disjunction {
	
	/** A table of proposition letters mapped to true or false. */
	private HashMap<String, Boolean> literals;

	/** A link to an original disjunction after simplification steps. */
	private Disjunction source;
	
	/**
	 * Instantiates a new disjunction with empty literals.
	 */
	public Disjunction() {
		literals = new HashMap<String, Boolean>();
		source = this;
	}

	/**
	 * Instantiates a new disjunction with literals 
	 * copied from another; internal method used to
	 * make sure we have a functional interface that 
	 * lets us create new search states in new memory,
	 * so they can be modified freely at construction time.
	 *
	 * @param model a model disjunction
	 */
	@SuppressWarnings("unchecked")
	protected Disjunction(Disjunction model) {
		literals = (HashMap<String, Boolean>) model.literals.clone();
		source = model.source;
	}

	
	/**
	 * Gets the source of this disjunction.
	 *
	 * @return the source
	 */
	public Disjunction getSource() {
		return source;
	}

	/**
	 * computes the truth value of this disjunction under the 
	 * passed assignment.  a return value of null means that 
	 * the assignment is partial and the truth value of the 
	 * disjunction is not determined.
	 *
	 * @param assignment truth values for proposition letters
	 * @return truth value of this disjunction
	 */
	public Boolean truthValue(Map<String,Boolean> assignment) {
		boolean miss = false;
		for (Map.Entry<String, Boolean> e : literals.entrySet()) {
			if (!assignment.containsKey(e.getKey())) {
				miss = true;
				continue;
			}
			if (assignment.get(e.getKey()) ^ e.getValue()) {
				continue;
			}
			return new Boolean(true);
		}
		if (miss) {
			return null;
		} else {
			return new Boolean(false);
		}
	}
	
	/**
	 * Join.
	 * An implementation of the string "join" operation
	 * commonly found in civilized programming languges
	 * (sorry Java).
	 *
	 * @param delimiter the delimiter to separate instances in s
	 * @param s the strings to be combined
	 * @return the result of joining together the elements of s
	 *         separated by the delimiter 
	 */
	public static String join(String delimiter, Iterable<? extends CharSequence> s) {
		Iterator<? extends CharSequence> iter = s.iterator();
		if (!iter.hasNext()) return "";
		StringBuilder buffer = new StringBuilder(iter.next());
		while (iter.hasNext()) buffer.append(delimiter).append(iter.next());
		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		List<String> entries = new LinkedList<String>();
		for (Map.Entry<String, Boolean> e : literals.entrySet()) {
			entries.add(e.getValue() ? e.getKey() : "- " + e.getKey());
		}
		return "( " + join(" | ", entries) + " )";
	}

	/**
	 * Puts the variable s in the passed clause with polarity p.
	 * Replace what's there if the variable already occurs.
	 *
	 * @param s the proposition variable to add
	 * @param p the new polarity of corresponding literal
	 */
	public void put(String s, Boolean p) {
		literals.put(s, p);
	}

	/**
	 * Removes the literal corresponding to variable s in this clause.
	 *
	 * @param s the proposition variable to remove
	 */
	public void remove(String s) {
		literals.remove(s);
	}

	/**
	 * Size.
	 *
	 * @return the number of literals in the disjunction
	 */
	public int size() {
		return literals.size();
	}

	/**
	 * Unit var.
	 * Get the variable associated with a unit disjunction
	 * (one with a single literal)
	 * 
	 * @return the name of the only proposition variable in
	 *   the disjunction, if it is a unit clause; 
	 *   null otherwise.
	 */
	public String unitVar() {
		if (literals.size() == 1) {
			for (String e: literals.keySet()) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Unit val.
	 * Get the polarity of the variable associated with a 
	 * unit disjunction (one with a single literal)
	 * 
	 * @return true if the only literal in the disjunction
	 *   is positive; false if it's negative; null if
	 *   the disjunction is not a unit clause.
	 */
	public Boolean unitVal() {
		String s = this.unitVar();
		if (s != null) {
			return literals.get(s);
		}
		return null;
	}

	/**
	 * get the proposition variables mentioned in this disjunction.
	 *
	 * @return the set of proposition variables
	 */
	public Set<String> getVariables() {
		return literals.keySet();
	}

	/**
	 * Say whether this clause already has a literal corresponding
	 * to variable s.
	 *
	 * @param s proposition variable to look for
	 * @return true if literal involving s occurs in the clause
	 */
	public boolean containsLiteral(String s) {
		return literals.containsKey(s);
	}
	
	/**
	 * Accumulate pure variables: update m and s to reflect all
	 * variables in this disjunction with their polarities.
	 *
	 * @param m a map giving the polarity assigned to all potentially 
	 *   pure variables that have been encountered so far
	 * @param s a set giving all the variables that we have
	 *   already encountered with 
	 *   both positive and negative occurrences.
	 */
	void accumulatePureVars(Map<String, Boolean> m, Set<String> s) {
		for (String v : getVariables()) {
			if (!s.contains(v)) {
				if (m.containsKey(v)) {
					if (m.get(v) ^ literals.get(v)) {
						m.remove(v);
						s.add(v);
					}
				}
				else {
					m.put(v, literals.get(v));
				}
			}
		}
	}

	/**
	 * Update the passed map of counts to take into account
	 * the occurrences of variables in this disjunction.
	 *
	 * @param pos a map from variables to counts of positive occurrences
	 * @param neg a map from variables to counts of negative occurrences
	 */
	void updateCounts(Map<String, Integer> pos, Map<String, Integer> neg) {
		for (String v: getVariables()) {
			if (literals.get(v)) {
				if (!pos.containsKey(v)) {
					pos.put(v, 1);
				}	
				else {
					pos.put(v, 1 + pos.get(v));
				}
			} else {
				if (!neg.containsKey(v)) {
					neg.put(v, 1);
				}	
				else {
					neg.put(v, 1 + neg.get(v));
				}
				
			}
				
		}
	}

	/**
	 * The Class DisjunctionComparator.
	 * Comparator for ordering Disjunctions in increasing order by size.
	 */
	static class DisjunctionComparator implements Comparator<Disjunction> {
		
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Disjunction d1, Disjunction d2) {
			return d1.size() - d2.size();
		}
	}

	/** The disjunction comparator instance. */
	static DisjunctionComparator disjunctionComparator = new DisjunctionComparator();

	/**
	 * Simplify disjunction by giving the specified variable the
	 * specified truth value.
	 *
	 * @param s the target variable to assign
	 * @param p the truth value to give it
	 * @return a disjunction, possibly simplified to reflect
	 *   the associated constraint; null if the assignment
	 *   makes the disjunction always true.
	 */
	public Disjunction simplifyWith(String s, Boolean p) {
		if (!literals.containsKey(s)) {
			// Disjunction is unchanged
			return this;
		}
		Boolean b = literals.get(s);
		// Disjunction must be true because of another literal
		if (b ^ p) {
			Disjunction result = new Disjunction(this);
			result.remove(s);
			return result;
		}
		// Disjunction is always true
		return null;
	}

	/**
	 * If clause d1 and clause d2 resolve against one another to
	 * yield another, non-tautological clause, return the result.
	 * formally to resolve
	 *   (a1 ... ai-1 | c | ai+1 ... am) 
	 * with
	 *   (b1 ... bj-1 | -c | bj+1 ... bn)
	 * to get
	 *   (a1 ... ai-1 | ai+1 ... am | b1 ... bj-1 | bj+1 ... bn)
	 * see 
	 *   https://en.wikipedia.org/wiki/Resolution_(logic)
	 *
	 * @param d1 a first disjunction
	 * @param d2 a section disjunction
	 * @return the result of resolving d1 against d2
	 */
	public static Disjunction resolve(Disjunction d1, Disjunction d2) {
		if (d1 == null || d2 == null) {
			return null;
		}
		Disjunction result = new Disjunction(d1);
		result.source = result;
		String resolvent = null;
		for (Map.Entry<String, Boolean> e: d2.literals.entrySet()) {
			if (result.containsLiteral(e.getKey())) {
				if (result.literals.get(e.getKey()) ^ e.getValue()) {
					if (resolvent == null) {
						// resolve on this variable
						resolvent = e.getKey();
						result.literals.remove(e.getKey());
					}
					else {
						// result is tautological
						return null;
					}
				}
			} else {
				result.put(e.getKey(), e.getValue());
			}
		}
		return result;
	}
	
	/** A template for matching one literal in a clauses. */
	protected static final Pattern literalPattern = 
			Pattern.compile("[\\s]*(-[\\s]*)?([^\\s|]+)[\\s|]*");

	/**
	 * Puts the next literal that can be read from the input string
	 * into this disjunction.  Throws IOException in case there is
	 * no correctly formatted literal next.  Returns the number of 
	 * characters consumed by in matching the literal extraction pattern
	 * against the current input.
	 *
	 * @param input string to scan from
	 * @return the number of characters read
	 * @throws IOException Signals that no literal can be read from input
	 */
	public int putReadLiteral(String input) throws IOException {
		Matcher m = literalPattern.matcher(input);
		Boolean b = null;
		if (!m.find()) {
			throw new IOException("Bad clause format: " + input);
		}
		if (0 != m.start()) {
			throw new IOException("Bad clause format: " + input.substring(0, m.start()));
		}
		if (m.group(1) != null) {
			b = new Boolean(false);
		}
		else {
			b = new Boolean(true);
		}
		String v = m.group(2);
		if (containsLiteral(v) && b ^ literals.get(v)) {
			throw new IOException("Conflicting literal: " + v);
		} 
		put(v, b);
		return m.end();
	}
	
	/**
	 * Read in a disjunction from the passed string.
	 *
	 * @param s the s
	 * @return the disjunction
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Disjunction fromString(String s) throws IOException {
		Disjunction result = new Disjunction();
		int position = 0;
		do {
			position += result.putReadLiteral(s.substring(position));
		} while (position < s.length());
		return result;
	}

	/**
	 * The main method.
	 * A collection of tests to verify the 
	 * implementation of disjunctions.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		Disjunction d = fromString(" x133 | - x150 | x112 ");
		System.out.println(d.toString());
		Disjunction d2 = d.simplifyWith("x150", new Boolean(false));
		if (d2 != null) {
			System.out.println(d2.toString());
		}
		else {
			System.out.println("disjunction is always true");
		}
		Disjunction d3 = d.simplifyWith("x150", new Boolean(true));
		if (d3 != null) {
			System.out.println(d3.toString());
		}
		else {
			System.out.println("disjunction is always true");
		}
	}
}
