/**
 * Search, learning and problem solving: BigramModel.java
 * Interface for models of the probability of pairs of words.
 * Adapted by Matthew Stone from Roy Frostig's 
 * text reconstruction assignment (wordsegutil.py)
 * for the Stanford AI class.
 *
 * @author Matthew Stone
 * @version 1.0
 */

public interface BigramModel {
    /**
     * Get the surprisal value of word w2 
     * occurring immediately after word w1
     *
     * @param w1 first (context) token
     * @param w2 second (test) token
     * @return surprisal of seeing w2 in the context of w1
     */
    public double cost(String w1, String w2);

    public void visualize(String[] words);
}
