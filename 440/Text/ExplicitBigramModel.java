import java.lang.Math;
import java.io.*;

/**
 * Search, learning and problem solving: ExplicitBigramModel.java
 * Simple model of the probability of pairs of words, 
 * computed directly from frequency data in a corpus.
 * Adapted by Matthew Stone from Roy Frostig's 
 * text reconstruction assignment (wordsegutil.py)
 * for the Stanford AI class.
 *
 * @author Matthew Stone
 * @version 1.0
 */

public class ExplicitBigramModel implements BigramModel {

    /** bigram model instance (singleton pattern) */
    private static final ExplicitBigramModel bigramInstance;

    /* the model instance is constructed from the LangUtil.CORPUS at load time */
    static {
	ExplicitBigramModel model = null;
	try {
	    model = new ExplicitBigramModel(LangUtil.CORPUS);
	} catch (FileNotFoundException ex) {
	    System.err.println("Problem accessing corpus file " + LangUtil.CORPUS);
	    System.err.println(ex.getMessage());
	    System.exit(1);
	}
	bigramInstance = model;
    }

    /**
     * @return singleton instance of the class
     */
    public static ExplicitBigramModel getInstance() {
	return bigramInstance;
    }

    /** mapping from pairs of tokens to number of occurrences */
    private Counter<String> bigramCounts = null;

    /** mapping from tokens to number of occurrences */
    private Counter<String> wordCounts = null;

    /**
     * Get the surprisal value of word w2 
     * occurring immediately after word w1
     * Slightly smoothed to handle zero counts.
     * Estimate of the conditional probability is
     * (number of occurrences of (w1, w2) + 1) 
     * divided by
     * (number of occurrences of w1 + size of vocabulary)
     * as always, surprisal is negative log probability.
     *
     * @param w1 first (context) token
     * @param w2 second (test) token
     * @return surprisal of seeing w2 in the context of w1
     */
    public double cost(String w1, String w2) {
	int bict = bigramCounts.getCount(LangUtil.pair(w1,w2));
	int wct = wordCounts.getCount(w1);
	return Math.log(wct + LangUtil.VOCAB_SIZE) - Math.log(bict + 1);
    }

    /**
     * Constructor: Build a bigram model from the language data
     * in the file specified by filename
     * 
     * @param filename source of language data
     * @throws FileNotFoundException if the file cannot be found
     */
    private ExplicitBigramModel(String filename) 
	throws FileNotFoundException {

	bigramCounts = new Counter<String>();
	wordCounts = new Counter<String>();
	BufferedReader br = new BufferedReader(new FileReader(filename));
	try {
	    String line = null;
	    while (( line = br.readLine()) != null) {
		String[] items = LangUtil.getWords(line);
		String s1 = LangUtil.SENTENCE_BEGIN;
		for (String s2: items) {
		    bigramCounts.incrementCount(LangUtil.pair(s1,s2));
		    wordCounts.incrementCount(s1);
		    s1 = s2;
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
	String w1 = LangUtil.SENTENCE_BEGIN;
	for (String w2: words) {
	    double c = cost(w1, w2);
	    System.out.println(w1 + ", " + w2 + ": surprisal " + Double.toString(c));
	    totalCost += c;
	    w1 = w2;
	}
	System.out.println("Total cost is " + Double.toString(totalCost));
    }
}
