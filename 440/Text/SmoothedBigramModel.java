import java.lang.Math;
import java.io.*;

/**
 * Search, learning and problem solving: SmoothedBigramModel.java
 * More sophisticated model of the probability of pairs of words, 
 * weighting a bigram model with a unigram model.
 * Adapted by Matthew Stone from Roy Frostig's 
 * text reconstruction assignment (wordsegutil.py)
 * for the Stanford AI class.
 *
 * @author Matthew Stone
 * @version 1.0
 */

public class SmoothedBigramModel implements BigramModel {

    /** bigram model instance (singleton pattern) */
    private static final SmoothedBigramModel smoothedBigramInstance = new SmoothedBigramModel();

    /**
     * @return singleton instance of the class
     */
    public static SmoothedBigramModel getInstance() {
	return smoothedBigramInstance;
    }

    /** Bernouilli weight favoring the unigram model */
    static final double bernoulliAlpha = 0.1;

    /**
     * utility for working with log probabilities 
     * avoiding loss of precision associated with
     * arithmetic on very different large numbers.
     *
     * log(exp(x) + exp(y)) =
     *     log(exp(x) * (exp(x) + exp(y)) / exp(x))
     *   = log((exp(x) + exp(y)) / exp(x)) + x
     *   = log(1 + exp(y) / exp(x)) + x
     *   = log(1 + exp(y - x)) + x
     *
     * @param x
     * @param y
     * @return log(exp(x) + exp(y))
     */
    private static double logSumExp(double x, double y) {
	double lo = Math.min(x,y);
	double hi = Math.max(x,y);
	return Math.log(1.0 + Math.exp(lo - hi)) + hi;
    }

    /**
     * Get the surprisal value of word w2 
     * occurring immediately after word w1
     * a: bernouilli alpha
     * u: unigram surprisal of w2
     * b: bigram surprisal of w2 after w1
     *
     * Want: -log( a * exp(-u) + (1-a) * exp(-b) )
     *     = -log( exp(log(a) - u) + exp(log(1-a) - b) )
     *     = -logSumExp( log(a) - u, log(1-a) - b )
     *
     * @param w1 first (context) token
     * @param w2 second (test) token
     * @return surprisal of seeing w2 in the context of w1
     */
    public double cost(String w1, String w2) {
	double u = UnigramModel.getInstance().cost(w2);
	double b = ExplicitBigramModel.getInstance().cost(w1, w2);
	return -logSumExp(Math.log(bernoulliAlpha) - u, 
			  Math.log(1 - bernoulliAlpha) - b);
    }

    /**
     * Constructor - nothing to do.
     */
    private SmoothedBigramModel() {
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
