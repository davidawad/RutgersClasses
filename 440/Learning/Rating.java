import java.util.Comparator;

/**
 * This class represents a rating. 
 * The same class is used for an actual rating 
 * or for a predicted rating.
 * It is used to represent training data, test data or results,
 * as well as an internal structure in tables and dictionaries.
 * 
 * @author Tarek El-Gaaly and Matthew Stone
 */
public class Rating {

    /** Tag for XML element */
    static final String XML_NAME = "rating";
    
    /** Stores number of ratings created to give each a unique id */
    static int ct = 0;

    /**
     * Forget the state associated with the counter
     */
    static public void recount() {
    	ct = 0;
    }

    /** Name of user that made the rating */
    public final String rater;
    /** Name of thing being judged */
    public final String item;
    /** Judgment (possibly normalized) */
    public double score;
    
    /** Score value from raw data (before normalization) */
    public final double rawScore;
    /** Unique id giving order in which rating was created */
    public final int seq;
    
    /**
     * Constructor 
     * 
     * @param rater user responsible for rating
     * @param item object of rating
     * @param score value of rating
     */
    public Rating(String rater, String item, double score)
    {
        this.rater = rater;
        this.item = item;
        this.rawScore = score;
        this.score = score;
        this.seq = ct++;
    }
    
    /**
     * Output a text description of the rating
     */
    public void print() 
    {
        System.out.println("{ " + rater + " rates " + item + " at " + rawScore + " }");
    }
        
    /**
     * Comparator to sort ratings in increasing order by score.
     * Used only in @see RatingDictionary.getItemRecommendations
     * and @see RatingDictionary.getRaterRecommendations
     * Be careful using this elsewhere because the SCORE field
     * on which this comparator sorts is mutable.
     */
    static public Comparator<Rating> Comp = new Comparator<Rating>() {
        public int compare(Rating r1, Rating r2) {
            if (r1.score > r2.score)
                return -1;
            else if (r2.score > r1.score)
                return 1;
            
            int c = r1.rater.compareTo(r2.rater);
            if (c != 0)
                return c;
            return r1.item.compareTo(r2.item);
        }
    };
    
}
