import java.util.Collection;
import java.util.HashMap;

/**
 * This class represents a set of rating data. 
 * 
 * @author Tarek El-Gaaly and Matthew Stone
 */
public class RatingTable {

    /** Tag for XML element */
    static final String XML_NAME = "critic";

    /** Tag for XML parameter */
    static final String PARAM_NAME = "name";

    /** Distance measure - Euclidean or Pearson correlation coefficient */
    public static enum SimilarityMeasure {
        EUCLIDEAN, PEARSON
    }
    
    /** What kind of data is stored in the table */
    public static enum CommonAttribute {
        ITEM, RATER, NONE
    }

    ////////////////////////////////////////////////////////////
    // Instance Variables
    ////////////////////////////////////////////////////////////
    
    /** Ratings index */
    private HashMap<String,Rating> ratings;

    /** How to deal with indexing */
    private CommonAttribute commonInfo;

    /** Common name for ratings field, if any */
    private String name;
    
    /** Number of ratings */
    private int count;

    /** Total points given in ratings */
    private double points;
    
    /** Total of log ratings */
    private double logSum;

    //////////////////////////////////////////////////////
    // Methods for constructing and accessing tables
    //////////////////////////////////////////////////////
    
    /**
     * Constructor to instantiate the  object and its variables
     * 
     * @param entry common element to ratings (either item or critic if any)
     */
    public RatingTable(String entry, CommonAttribute type) {
        name = entry;
        this.commonInfo = type;
        ratings = new HashMap<String,Rating>();
        points = 0;
        logSum = 0;
        count = 0;
    }

    /**
     * Getter for name variable
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for ratings list variable
     * 
     * @return name
     */
    public Collection<Rating> getRatings() {
        return ratings.values();
    }

    /**
     * Construct a string, using the passed rating as a model,
     * that will retrieve the corresponding rating from the 
     * current rating table.
     * 
     * The case of rating tables that vary both by rater and
     * item is slow, but it is seldom used - just to check
     * predicted values against test data.
     * 
     * @param rating a rating object providing a template
     * @return the string key to find the matching rating 
     *         in this table
     */
    private String keyFor(Rating rating) {
        switch (commonInfo) {
        case ITEM:
            return rating.rater;
            
        case RATER:
            return rating.item;
            
        default:
            return rating.rater + " # " + rating.item;  
        }
    }
    
    /**
     * Add a rating to the critic's list of ratings
     * 
     * @param rating Rating object to add
     */
    public void addRating(Rating rating) {
        if (rating == null)
            return;
        
        ratings.put(keyFor(rating),rating);
        count++;
        points += rating.rawScore;
        logSum += Math.log(rating.rawScore);
    }

    /**
     * Get the keys for which we have ratings
     * 
     * @return list of movie titles watched
     */
    public Collection<String> getRatedKeys() {
        return ratings.keySet();
    }

    /**
     * Get the ratings of a particular entity
     * 
     * @param key index into rating table (@see keyFor)
     * @return rating record or null if entity was not found
     */
    public Rating getRatingFor(String key) {
        return ratings.get(key);
    }
    
    /**
     * Get the rating in the rating table that 
     * corresponds to the passed rating object
     * That is - it has the same key - either
     * the same rater name if this table varies
     * by rater, or the same item name if this
     * table varies by item, or the same complex
     * key if the table varies by both.
     * 
     * @param r Rating object to match
     * @return match for this in table, or null if no match
     */
    public Rating getMatchingRating(Rating r) {
        return getRatingFor(keyFor(r));
    }
    
    /////////////////////////////////////////////////////
    // Distance, similarity and prediction
    /////////////////////////////////////////////////////

    /**
     * @return prediction factor component for geometric 
     *         mean baseline estimated from this set of
     *         rating data
     */
    public double getFactor() 
    {
        if (count == 0)
            return 0;
        return logSum/count;
    }
    
    /**
     * @return average rating score in this set of ratings
     */
    public double getAverage()
    {
        if (count == 0)
            return 0;
        return points/count;
    }

    /**
     * Get the root mean squared distance to between this
     * table and another set of ratings. 
     * 
     *
     * @param table group of ratings against which to compare
     * @return distance measure
     */
    private double getRMSDistance(RatingTable table) {
        double sum_of_squares = 0, diff = 0;
        int ct = 0;

        for (Rating r1 : this.getRatings()) {
            Rating r2 = table.getMatchingRating(r1);
            if (r2 != null) {
                diff = r1.score - r2.score;
                sum_of_squares += diff * diff;
                ct++;
            }
        }
        
        double result = sum_of_squares;
        if (ct > 0)
            result /= ct;
        
        return Math.sqrt(result);
    }

    /**
     * Get the distance between this set of rating data
     * and the corresponding ratings maintained in table
     * This wrapper function could be extended to define
     * different distance measures other than the RMSE
     * distance defined so far.
     * 
     * @param table group of ratings against which to compare
     * @return distance measure
     */
    public double getDistance(RatingTable table) {
        return getRMSDistance(table);
    }

    /**
     * returns a similarity value between 0 and 1 computed
     * from the RMS similarity as described by Segaran,
     * "Programming Collective Intelligence".
     * That is, this corresponds to a Euclidean distance
     * similarity measure.
     * 
     * @param table group of ratings against which to compare
     * @return similarity measure
     */
    private double getRMSSimilarity(RatingTable table) {
        return 1 / (1 + getRMSDistance(table));
    }
    
    /**
     * Get the Pearson correlation coefficient with table. This is a
     * measure of similarity between the two critics or two movies.
     * 
     * @param table group of ratings against which to compare
     * @return similarity measure
     */
    public double getPearsonSimilarity(RatingTable table) {
        double sum1Sq = 0, sum2Sq = 0, pSum = 0, n = 0;
        double sum1 = 0, sum2 = 0, num = 0, den = 0;

        for (Rating r1 : this.getRatings()) {
            Rating r2 = table.getMatchingRating(r1);
            if (r2 != null) {
                sum1 += r1.score;
                sum2 += r2.score;
                sum1Sq += r1.score * r1.score; 
                sum2Sq += r2.score * r2.score;
                pSum += r1.score * r2.score;
                n++;
            }
        }

        if (n == 0) 
            return 0;
            
        num = pSum - (sum1 * sum2 / n);
        den = Math.sqrt((sum1Sq - sum1 * sum1 / n)
                * (sum2Sq - sum2 * sum2 / n));
        if (den == 0)
            return 0;

        double r = num / den;
        return (r > 0) ? r : 0;
    }

    /**
     * 
     * @param table
     * @param simMeasure
     * @return
     */
    public double getSimilarity(RatingTable table, SimilarityMeasure simMeasure) {
        switch (simMeasure) {
        case PEARSON:
            return getPearsonSimilarity(table);
        case EUCLIDEAN:
        default:
            return getRMSSimilarity(table);
        }
    }

    /**
     * Constructs a similarity record describing the relationship
     * between this table and the passed table.
     * The questions that this function answers are:
     * - how would you best predict the ratings of this object
     *   if you knew the ratings in the passed ratings table?
     * - how good of an estimate would the ratings so calculated be?
     * 
     * The prediction is based on linear regression.
     * This is adapted from 
     * http://www.cs.princeton.edu/introcs/97data/LinearRegression.java.html
     *
     * The similarity is measured by the specified similarity measure.
     * 
     * Linear regression as a side-effect computes the Pearson
     * coefficient so this partially duplicates the computation
     * from @getPearsonSimilarity
     * 
     * @param table reference ratings to make prediction from
     * @param sim measure to assess how good those predictions will be
     * @param minOverlap threshold of common ratings required
     *        to establish similarity
     * @return object recording similarity value and prediction rule
     */
    public SimilarityTable.Similarity regressAgainst(
            RatingTable table, SimilarityMeasure sim, int minOverlap)
    {
        // first pass: read in data, compute xbar and ybar
        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
        int n = 0;
        for (Rating r1 : table.getRatings()) {
            Rating r2 = this.getMatchingRating(r1);
            if (r2 != null) {
                sumx += r1.score;
                sumx2 += r1.score * r1.score;   
                sumy += r2.score;
                n++;
            }
        }
        
        if (n < minOverlap)
            return null;
        
        double xbar = sumx / n;
        double ybar = sumy / n;
        
        // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (Rating r1 : table.getRatings()) {
            Rating r2 = this.getMatchingRating(r1);
            if (r2 != null)  {
                xxbar += (r1.score - xbar) * (r1.score - xbar);
                yybar += (r2.score - ybar) * (r2.score - ybar);
                xybar += (r1.score - xbar) * (r2.score - ybar);
            }
        }
            
        
        double beta1 = xybar / xxbar;
        double beta0 = ybar - beta1 * xbar;

        // analyze results
        double ssr = 0.0;      // regression sum of squares
        for (Rating r1 : table.getRatings()) {
            Rating r2 = this.getMatchingRating(r1);
            if (r2 != null) {
                double fit = beta0;
                fit += beta1*r1.score;
                ssr += (fit - ybar) * (fit - ybar);
            }
        }
        
        double R2;
        
        // make a generic prediction when numbers require
        if (yybar == 0 || xxbar == 0) {
            beta1 = 0;
            beta0 = ybar;
            R2 = 0;
        }
        else    
            R2 = ssr / yybar;
        
        double r = (beta1 > 0) ? Math.sqrt(R2) : 0;

        if (sim == SimilarityMeasure.EUCLIDEAN)
            return new SimilarityTable.Similarity(table.getName(), getSimilarity(table, sim), beta1, beta0);

        return (beta1 <= 0) ? null : new SimilarityTable.Similarity(table.getName(), r, beta1, beta0);
    }
    
    /**
     * Reset the ratings and bookkeeping in this rating table
     * to reflect a baseline prediction from the passed rating
     * dictionary.  The idea is that subsequent processing
     * establishes predictions based on deviations from the 
     * baseline.
     * 
     * Note: score is reset from rawScore so this method 
     * can be called on tables whose underlying rating objects
     * overlap (as happens in this implementation).
     * 
     * @param rd Dictionary for prediction
     * @return total corrections made (for updating dictionary)
     */
    public double subtractBaseline(RatingDictionary rd)
    {
        double adjustments = 0;
        
        for (Rating r : getRatings()) {
            double correction = rd.rawGeometricMean(r.rater, r.item);
            adjustments += correction;
            r.score = r.rawScore - correction;
        }
            
        points -= adjustments;
        return adjustments;
    }

    /**
     * Rest the ratings and bookkeeping in this rating table
     * to take into account a prior baseline from the passed
     * rating dictionary.
     * 
     * Note: score is reset from rawScore so this method can
     * be called on tables whose underlying rating objects
     * overlap.  However, in fact this method is only
     * called on data generated by prediction, in order
     * to match predictions against raw test data.
     * 
     * @param rd Dictionary
     * @return total corrections made
     */
    public double addBaseline(RatingDictionary rd)
    {
        double adjustments = 0;
        
        for (Rating r : getRatings()) {
            double correction = rd.rawGeometricMean(r.rater, r.item);
            adjustments += correction;
            r.score = r.rawScore + correction;
        }
        points += adjustments;
        return adjustments;
    }
}