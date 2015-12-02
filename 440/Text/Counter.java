import java.util.HashMap;

/**
 * Search, learning and problem solving: Counter.java
 * Reconstruction of python's built in counter class;
 * Uses a hash map to associate keys with integers.
 * Adapted by Matthew Stone from Roy Frostig's 
 * text reconstruction assignment (wordsegutil.py)
 * for the Stanford AI class.
 *
 * @author Matthew Stone
 * @version 1.0
 */

public class Counter<Key> extends HashMap<Key,Integer> {

    /**
     * Update the count associated with the passed key
     * to be one larger, handling the case where key
     * is not found, so implicitly has a zero count.
     *
     * @param key
     */
    public void incrementCount(Key key) {
	Integer v = this.get(key);
	if (v == null) {
	    this.put(key, 1);
	}
	else {
	    this.put(key, new Integer(v.intValue() + 1));
	}
    }

    /**
     * Return the count associated with the passed key
     * handling the case where key
     * is not found, so implicitly has a zero count.
     *
     * @param key
     * @return number of times key has been incremented
     */
    public int getCount(Key key) {
	Integer v = this.get(key);
	if (v == null) {
	    return 0;
	}
	else {
	    return v.intValue();
	}
    }
}
