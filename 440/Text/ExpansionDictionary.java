import java.io.*;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

/**
 * Search, learning and problem solving: ExpansionDictionary.java
 * Reverse dictionary to look up a word without vowels
 * and list all the corresponding words with vowels.
 * Adapted by Matthew Stone from Roy Frostig's 
 * text reconstruction assignment (wordsegutil.py)
 * for the Stanford AI class.
 *
 * @author Matthew Stone
 * @version 1.0
 */

public class ExpansionDictionary {

    /** instance for singleton pattern */
    private static final ExpansionDictionary dictionaryInstance;

    /* the dictionary instance is constructed from the LangUtil.CORPUS at load time */
    static {
	ExpansionDictionary dictionary = null;
	try {
	    dictionary = new ExpansionDictionary(LangUtil.CORPUS);
	} catch (FileNotFoundException ex) {
	    System.err.println("Problem accessing corpus file " + LangUtil.CORPUS);
	    System.err.println(ex.getMessage());
	    System.exit(1);
	}
	dictionaryInstance = dictionary;
    }

    /**
     * @return singleton instance of the class
     */
    public static ExpansionDictionary getInstance() {
	return dictionaryInstance;
    }

    /** default return value for strings that are not in the dictionary */
    static private final HashSet<String>empty = new HashSet<String>();

    /** main data structure storing dictionary data */
    private HashMap<String, HashSet<String>> expansions = 
	new HashMap<String, HashSet<String>>();

    /**
     * get all the words with vowels that could correspond to
     * the passed string, given the tokens we've seen in our corpus data
     *
     * @param s word without vowels
     * @return set of words with vowels that we've seen that agree with s
     */
    public Set<String> lookup(String s) {
	Set<String> result = expansions.get(s);
	if (result == null) {
	    result = empty;
	}
	return result;
    }

    /**
     * Constructor: Build a dictionary linking words without vowels
     * to words with vowels, describing all the tokens found 
     * in the file specified by filename
     * 
     * @param filename source of language data
     * @throws FileNotFoundException if the file cannot be found
     */
    private ExpansionDictionary(String filename) 
	throws FileNotFoundException {
	BufferedReader br = new BufferedReader(new FileReader(filename));
	try {
	    String line = null;
	    int ct = 0;
	    while (( line = br.readLine()) != null) {
		String[] items = LangUtil.getWords(line);
		for (String s: items) {
		    String nv = LangUtil.removeVowels(s);
		    HashSet<String> exps = expansions.get(nv);
		    if (exps == null) {
			exps = new HashSet<String>();
			expansions.put(nv, exps);
		    }
		    exps.add(s);
		}
	    }
	} catch (IOException ex) {
	    System.err.println("Error reading corpus file " + filename);
	    System.err.println(ex.getMessage());
	} finally {
	    try {
		br.close();
	    } catch (IOException ex) {
		System.err.println("Error processing corpus file " + filename);
		System.err.println(ex.getMessage());
	    }
	}
    }

    public void visualize(String[] words) {
	for (String w: words) {
	    String alt = LangUtil.removeVowels(w);
	    System.out.print(alt + " could be: ");
	    for (String k: lookup(alt)) {
		System.out.print(k + " ");
	    }
	    System.out.println();
	}
    }
}
