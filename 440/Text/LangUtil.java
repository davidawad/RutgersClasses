import java.util.Map;
import java.util.Collection;

/**
 * Search, learning and problem solving: LangUtil.java
 * Useful infrastructure (static methods and constants)
 * for dealing with text in Java.
 * Adapted by Matthew Stone from Roy Frostig's 
 * text reconstruction assignment (wordsegutil.py)
 * for the Stanford AI class.
 *
 * @author Matthew Stone
 * @version 1.0
 */

public class LangUtil {

    /** language data file for extracting word lists and ngrams */
    public static final String CORPUS = "corpus.txt";
    
    /** estimate of maximum vocabulary (helps for cost of unknown words) */
    public static final int VOCAB_SIZE = 600000;
    
    /** initial dummy token to give bigram for first word */
    public static final String SENTENCE_BEGIN =  "-BEGIN-";

    /** empty array used as generic return value */
    public static final String[] NO_STRINGS = {};

    /**
     * Create an index for maps and other data structures
     * based on two strings.  Workaround for the fact that
     * Java doesn't have a built-in pair type.  Concatenates
     * s1 and s2, separated by ":", which shouldn't occur
     * in s1 or s2, assuming that these are strings of letters.
     *
     * @param s1 first index string of letters
     * @param s2 second index string of letters
     * @return unique string encodning info in s1 and s2
     */
    public static String pair(String s1, String s2) {
	return s1 + ":" + s2;
    }

    /**
     * Return a new version of the passed String with
     * occurrences of characters 'a', 'e', 'i', 'o', and 'u' removed.
     *
     * @param s String to process
     * @return String like s but wihout vowels
     */
    public static String removeVowels(String s) {
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < s.length(); i++) {
	    char c = s.charAt(i);
	    if (c != 'a' && c != 'e' && c != 'i' && c != 'o' && c != 'u') {
		sb.append(c);
	    }
	}
	return sb.toString();
    }

    /**
     * Preprocessing operation for tokenizing language data.
     * Removes punctuation and other special characters.
     * Normalizes words to all lower case.
     * Returns its result as an array of Strings
     * containing the tokens found in the passed String in order.
     *
     * @param line String to process
     * @return string array giving words in line
     */
    public static String[] getWords(String line) {
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < line.length(); i++) {
	    char c = line.charAt(i);
	    if (Character.isLetter(c)) {
		sb.append(Character.toLowerCase(c));
	    }
	    else if (Character.isWhitespace(c) || '-' == c) {
		sb.append(' ');
	    }
	}
	String[] result = sb.toString().trim().split("\\s+");
	if (result.length == 1 && result[0].equals("")) {
	    return NO_STRINGS;
	}
	return result;
    }

    /**
     * Implementation of python-style join operation.  Takes
     * an array of strings to combine and a delimiter string.
     * Returns a new string, concatenating all the strings
     * in the array together in order, with each successive pair
     * separated by a copy of the delimiter string.
     *
     * @param input
     * @param delimiter
     * @return String joining strings in input, separated by delimiter
     */
    public static String join(Collection<String> input, String delimiter)
    {
	StringBuilder sb = new StringBuilder();
	for (String value : input) {
		sb.append(value);
		sb.append(delimiter);
	}
	int length = sb.length();
	if (length > 0)  {
	    // Remove the extra delimiter
	    sb.setLength(length - delimiter.length());
	}
	return sb.toString();
    }
}
