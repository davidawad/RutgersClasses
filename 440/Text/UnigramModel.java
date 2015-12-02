import java.lang.Math;
import java.io.*;

/**
 * Search, learning and problem solving: UnigramModel.java
 * Build a simple probability model for words from a text corpus.
 * Adapted by Matthew Stone from Roy Frostig's 
 * text reconstruction assignment (wordsegutil.py)
 * for the Stanford AI class.
 *
 * @author Matthew Stone
 * @version 1.0
 */

public class UnigramModel {

    /** words longer than this get an exponential cost model */
    public static final int LONG_WORD_THRESHOLD = 5;
    
    /** log penalty for each added character in an unknown word */
    public static final double LENGTH_DISCOUNT = 0.15;

    /** unigram model instance (singleton pattern) */
    private static final UnigramModel unigramInstance;

    /* the model instance is constructed from the LangUtil.CORPUS at load time */
    static {
	UnigramModel model = null;
	try {
	    model = new UnigramModel(LangUtil.CORPUS);
	} catch (FileNotFoundException ex) {
	    System.err.println("Problem accessing corpus file " + LangUtil.CORPUS);
	    System.err.println(ex.getMessage());
	    System.exit(1);
	}
	unigramInstance = model;
    }

    /**
     * @return singleton instance of the class
     */
    public static UnigramModel getInstance() {
	return unigramInstance;
    }

    /** Map storing number of counts for each word */
    private Counter<String> counts = null;

    /** Number of tokens in the corpus */
    private int totalCounts = 0;
    
    /**
     * Computes a measure of how surprising the word is,
     * which can be used as a cost function to guide minimum-cost 
     * search.  If the word occurs in the corpus, the
     * cost is the negative log of the probability of the 
     * word (- log(v/totalCounts) = -(log(v) -log(totalCounts))
     * = log(totalCounts) - log(v).
     * Otherwise, it uses a model of unknown words where 
     * longer words become more and more unlikely.
     * 
     * @param word 
     * @return surprisal value of word
     */
    public double cost(String word) {
	int v = counts.getCount(word);
	if (v == 0) {
	    double length = Math.max(LONG_WORD_THRESHOLD, 
				     word.length());
	    return -(length * Math.log(LENGTH_DISCOUNT) + 
		     Math.log(1.0) - Math.log(LangUtil.VOCAB_SIZE));
	}
	return Math.log(totalCounts) - Math.log(v);
    }

    /**
     * Constructor: Build a unigram model from the language data
     * in the file specified by filename
     * 
     * @param filename source of language data
     * @throws FileNotFoundException if the file cannot be found
     */
    private UnigramModel(String filename) 
	throws FileNotFoundException {
	
	counts = new Counter<String>();
	BufferedReader br = new BufferedReader(new FileReader(filename));
	try {
	    String line = null;
	    while ((line = br.readLine()) != null) {
		String[] items = LangUtil.getWords(line);
		for (String s: items) {
		    counts.incrementCount(s);
		    totalCounts++;
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
	double totalCost = 0.0;
	for (String w: words) {
	    double c = cost(w);
	    System.out.println(w + ": surprisal " + Double.toString(c));
	    totalCost += c;
	}
	System.out.println("Total cost is " + Double.toString(totalCost));
    }
}
