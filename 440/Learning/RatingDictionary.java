import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class represents a rating dictionary.
 * It organizes the aggregation of rating data,
 * the calculation of similarities across raters and items,
 * prediction of ratings based on current data,
 * and the calculation of new recommendations.
 *
 * @author Tarek El-Gaaly and Matthew Stone
 */
public class RatingDictionary {

    /** XML namespace */
    static final String XMLNS =
        "http://perceptualscience.rutgers.edu/colabFiltering";

    /** Element tag for ratingdictionary objects */
    static final String XML_NAME = "ratingdictionary";

    /** Strategies for predicting rating from rater and item */
    static enum Method {
        ITEM_BASELINE, RATER_BASELINE, MIXED_BASELINE,
        ITEM_SIMILARITY, RATER_SIMILARITY, CUSTOM
    }

    ////////////////////////////////////////////////////////////
    // INSTANCE MEMBERS
    ////////////////////////////////////////////////////////////

    /** Assocates each rater with a table of their ratings */
    private Hashtable<String,RatingTable> raterData;
    /**
     *  Associates each item with a table of its ratings
     *  Note that the SAME rating objects are stored twice,
     *  once under raters and once under items, so
     *  changes in one are reflected in the other.
     */
    private Hashtable<String,RatingTable> itemData;
    /** Associates brief names of items with longer names */
    private Hashtable<String,String> itemIndex;
    /** Associates each rater with a list of similar ones */
    private Hashtable<String,SimilarityTable> raterNeighbors;
    /** Associates each item with a list of similar ones */
    private Hashtable<String,SimilarityTable> itemNeighbors;

    /** Gives the total raw score of all ratings in the dictionary */
    private double scoreTotals;
    /** Gives the sum of logs of raw scores of ratings (for baseline) */
    private double logTotals;
    /** Stores the number of ratings in the table */
    private int numRatings;
    /** Indicates whether rating scores reflect baseline or not */
    private boolean scoresAreFromBaseline;

    //////////////////////////////////////////////////////////
    // Methods for creating dictionaries and dealing with data
    //////////////////////////////////////////////////////////

    /**
     * Constructor: initializes the indexes of this rating dictionary
     */
    public RatingDictionary()
    {
        raterData = new Hashtable<String,RatingTable>();
        itemData = new Hashtable<String,RatingTable>();
        itemIndex = new Hashtable<String, String>();

        raterNeighbors = null;
        itemNeighbors = null;

        scoreTotals = 0;
        logTotals = 0;
        numRatings = 0;
        scoresAreFromBaseline = false;
    }

    /**
     * Factory method for loading rating dictionary from XML
     * @param fileName location of XML data
     * @return RatingDictionary encapsulating the information in file
     */
    static public RatingDictionary fromXML(String fileName)
    {
        try {
            // Set up SAX reader, which processes XML objects as file is read.
            XMLReader xr = XMLReaderFactory.createXMLReader();
            TableReader handler = new TableReader();
            xr.setContentHandler(handler);
            xr.setErrorHandler(handler);

            System.out.println(">> loading critic rating dictionary..");

            // Parse the XML
            FileReader f = new FileReader(fileName);
            xr.parse(new InputSource(f));
            RatingTable data = handler.getRatingData();
            RatingDictionary result = new RatingDictionary();
            result.addTrainingData(data, 0, 1);
            return result;

        } catch (SAXException e) {
            System.err.println(e.getMessage());
            return null;
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Factory method for initializing rating dictionary from
     * MovieLens item descriptions
     *
     * @param itemfile file indexing movies by title
     * @return empty dictionary with item index from item file
     */
    static public RatingDictionary fromMovieLensItems(String itemfile)
    {
        File itemData = new File(itemfile);

        BufferedReader input = null;

        RatingDictionary rd = new RatingDictionary();

        try {
            input = new BufferedReader(new FileReader(itemData));
            try {
                String line = null; // not declared within while loop
                while ((line = input.readLine()) != null) {
                    String words[] = line.split("\\|");
                    rd.addItemTitle(words[0], words[1]);
                }
            }

            finally {
                input.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rd;
    }

    /**
     * Read in the MovieLens (format) rating descriptions and convert them
     * into a rating table by looking up the movie Ids to the
     * corresponding names and creating rating objects for them.
     *
     * @param datafile file where MovieLens (format) ratings sit
     * @return rating table object for stored data
     */
    public RatingTable tabulateMovieLensData(String datafile)
    {
        File ratings = new File(datafile);
        int numRatings = 0;

        RatingTable result = new RatingTable(null, RatingTable.CommonAttribute.NONE);
        BufferedReader input = null;

        try {
            input = new BufferedReader(new FileReader(ratings));
            try {
                String line = null; // not declared within while loop
                while ((line = input.readLine()) != null) {
                    String words[] = line.split("\t");
                    String critic = words[0];
                    String movie = getItemName(words[1]);
                    if (movie == null) movie = words[1];
                    double score = Double.parseDouble(words[2]);
                    result.addRating(new Rating(critic, movie, score));
                    numRatings++;
                }
            } finally {
                input.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(">> " + numRatings + " ratings loaded...");

        return result;

    }

    //////////////////////////////////////////////////////////////
    // Utility and access functions
    //////////////////////////////////////////////////////////////

    /**
     * Get all raters in the rating dictionary
     * @return List of tables describing raters
     */
    public Collection<RatingTable> getRaters()
    {
        return raterData.values();
    }

    /**
     * Get all the items in the rating dictionary
     * @return List of tables describing items
     */
    public Collection<RatingTable> getItems()
    {
        return itemData.values();
    }

    /**
     * Get the table of ratings for a rater by name
     * @return Table describing rater
     */
    public RatingTable getRater(String raterName)
    {
        return raterData.get(raterName);
    }

    /**
     * Get the table of ratings for an item by name
     * @return Table describing item
     */
    public RatingTable getItem(String itemName)
    {
        return itemData.get(itemName);
    }

    /**
     * Get a rater's rating of a particular item. -1 is returned if rating
     * does not exist in dictionary or if an erroneous parameter was passed
     * @param raterName
     * @param itemName
     * @return rating OR -1.0 if error or rating does not exist
     */
    public double getRating(String raterName, String itemName)
    {
        if (raterName==null || itemName==null) return -1.0;
        RatingTable c = raterData.get(raterName);
        if (c==null) return -1.0;
        Rating r1 = c.getRatingFor(itemName);
        if (r1==null) return -1.0; else return r1.score;
    }

    /**
     * Get all rater's ratings in the form of a list.
     * Returns null if there is no information on this rater
     * @param raterName string name for rater to look up
     * @return list of ratings for specified critic OR
     *         null if ratings do not exist
     */
    public Collection<Rating> getRaterRatings(String raterName)
    {
        if (raterName==null) return null;
        RatingTable c = raterData.get(raterName);
        if (c==null) return null;
        return c.getRatings();
    }

    /**
     * Add a rater to the rating dictionary
     * @param c - table describing rater's ratings
     */
    public void addRater(RatingTable c)
    {
        if(c != null && !raterData.containsKey(c.getName()))
            raterData.put(c.getName(), c);
    }

    /**
     * Add an item to the rating dictionary
     * @param m - table describing item ratings
     */
    public void addItem(RatingTable m)
    {
        if (m != null && !itemData.containsKey(m.getName()))
            itemData.put(m.getName(), m);
    }

    /**
     * Add item title to the index of item titles in this rating dictionary
     * @param id - String number to identify the item
     * @param itemTitle - human readable name of the item
     */
    public void addItemTitle(String id, String itemTitle)
    {
        if(id!=null && itemTitle!=null && !itemIndex.contains(id)) {
            itemIndex.put(id, itemTitle);
            RatingTable m = new RatingTable(itemTitle, RatingTable.CommonAttribute.ITEM);
            itemData.put(itemTitle, m);
        }
    }

    /**
     * Get the human-readable name of an item by ID
     * @param id database index of item
     * @return Name of item
     */
    public String getItemName(String id)
    {
        return itemIndex.get(id);
    }

    /**
     * Update the rating dictionary to include the passed rating
     * @param r Rating object to add
     */
    public void addRating(Rating r)
    {
        String raterName = r.rater;
        String itemName = r.item;

        RatingTable c = raterData.get(raterName);
        if (c == null) {
            c = new RatingTable(raterName, RatingTable.CommonAttribute.RATER);
            addRater(c);
        }
        c.addRating(r);

        RatingTable m = itemData.get(itemName);
        if (m == null) {
            m = new RatingTable(itemName, RatingTable.CommonAttribute.ITEM);
            addItem(m);
        }
        m.addRating(r);

        numRatings++;
        scoreTotals += r.score;
        logTotals += Math.log(r.score);
    }

    //////////////////////////////////////////////////////////////
    // Utility functions for similarity
    //////////////////////////////////////////////////////////////

    /**
     * Compute similarity tables for all the items
     * in the dictionary, given the current inventory of
     * training data.
     *
     * @param sim how similarity should be calculated
     * @param minOverlap number of shared ratings required for similarity
     * @param maxNeighbors number of similar items to store for each entity
     */
    public void computeItemSimilarities(RatingTable.SimilarityMeasure sim, int minOverlap, int maxNeighbors)
    {
        itemNeighbors = new Hashtable<String,SimilarityTable>();

        Collection<RatingTable> alts = itemData.values();
        for (RatingTable m : alts) {
            itemNeighbors.put(m.getName(), new SimilarityTable(m, alts, sim, minOverlap, maxNeighbors));
        }
    }

    /**
     * Compute similarity tables for all the raters
     * in the dictionary, given the current inventory of
     * training data.
     *
     * @param sim how similarity should be calculated
     * @param minOverlap number of shared ratings required for similarity
     * @param maxNeighbors number of similar items to store for each entity
     */
    public void computeRaterSimilarities(RatingTable.SimilarityMeasure sim, int minOverlap, int maxNeighbors)
    {
        raterNeighbors = new Hashtable<String,SimilarityTable>();

        Collection<RatingTable> alts = raterData.values();
        for (RatingTable c : alts) {
            raterNeighbors.put(c.getName(), new SimilarityTable(c, alts, sim, minOverlap, maxNeighbors));
        }

    }

    /////////////////////////////////////////////////////////////////
    // Functions for making predictions
    /////////////////////////////////////////////////////////////////

    /**
     * Return the best estimate for the value of a rating
     * given no other information.
     *
     * @return average score of ratings in the dictionary
     */
    public double defaultScore() {
        return scoreTotals / numRatings;
    }

    /**
     * Return an estimate of the score for
     * specified rater and item.
     * Current algorithm takes the geometric
     * mean of factors for the rater and item
     * calculated to agree with the training
     * data on average.
     *
     * @param rater string name of rater
     * @param item string name of item
     * @return baseline estimated score
     */
    public double rawGeometricMean(String rater, String item)
    {
        RatingTable r = raterData.get(rater);
        if (r == null) return defaultScore();
        RatingTable i = itemData.get(item);
        if (i == null) return defaultScore();
        double f1 = r.getFactor();
        double f2 = i.getFactor();
        double al = logTotals / numRatings;
        return Math.exp(f1 + f2 - al);
    }

    /**
     * Return an estimate of the score for
     * specified rater and item.
     */
    public double geometricMeanBaseline(String rater, String item)
    {
	if (scoresAreFromBaseline)
	    return 0.0;
	else
	    return rawGeometricMean(rater, item);
    }

    /**
     * Predict: Key function - estimate a user's rating for an item
     *
     * @param rater String name of user
     * @param item String name of item
     * @param method One of the RatingDictionary.Method values
     *               describing how the prediction is to be made
     * @param neighbors How many items to take into account
     *               in nearest neighbor methods
     * @return estimated prediction
     */
    public Rating predict(String rater, String item, Method method,
			  int numItemNeighbors, int numRaterNeighbors) {

        double score = 0;
        double norm = 0;
        int ct = 0;

        if (method == Method.ITEM_BASELINE) {
            // TODO: Your code here.
        }

        if (method == Method.RATER_BASELINE) {
            // TODO: Your code here.
        }

        if (method == Method.MIXED_BASELINE) {
            // TODO: Your code here.
        }

        if (method == Method.ITEM_SIMILARITY) {
            // TODO: Your code here.
        }

        if (method == Method.RATER_SIMILARITY) {
            // TODO: Your code here.
        }

        if (method == Method.CUSTOM) {
            // TODO: Your code here.
        }

        return new Rating(rater, item, this.defaultScore());
    }

    ////////////////////////////////////////////////////////////////
    // User level functions for interacting with dictionaries
    ////////////////////////////////////////////////////////////////

    /**
     * Update the rating dictionary to include the passed information
     * Assumes crossvalidation - omits the training data corresponding
     * to the passed fold (which can then be used as test data)
     *
     * @param t Overall data
     * @param fold Integer between 0 and numFolds - 1 for data to leave out
     * @param numFolds Number of crossvalidation runs anticipated
     */
    public void addTrainingData(RatingTable t, int fold, int numFolds)
    {
        for (Rating r : t.getRatings()) {
            if (numFolds <= 1 || r.seq % numFolds != fold)
                addRating(r);
        }
    }

    /**
     * Adjust all the rating data (and associated bookkeeping)
     * so that scores are given relative to a baseline that
     * factors item and rater averages.
     */
    public void subtractBaseline()
    {
        for (RatingTable c : raterData.values())
            scoreTotals -= c.subtractBaseline(this);
        for (RatingTable m : itemData.values())
            m.subtractBaseline(this);
        scoresAreFromBaseline = true;
    }

    /**
     * Create ratings predictions.
     * Draw the (rater, item) pairs from a specific fold
     * of the training data (t) assuming crossvalidation.
     * Use the specified prediction method with the
     * specified number of nearest neighbors.
     *
     * @param t RatingTable with specified data
     * @param method type of prediction method to use
     * @param neighbors number of neighbors in NN prediction
     * @param fold which run in crossvalidation
     * @param numFolds number of crossvalidation runs anticipated
     * @param printPredictions whether to display results
     * @return RatingTable giving overall results
     */
    public RatingTable predictTestData(RatingTable t, Method method,
				       int numItemNeighbors, int numRaterNeighbors,
				       int fold, int numFolds, boolean printPredictions)
    {
        RatingTable result = new RatingTable(null, RatingTable.CommonAttribute.NONE);

        for (Rating r : t.getRatings()) {
            if (r.seq % numFolds == fold) {
                if (raterData.containsKey(r.rater) && itemData.containsKey(r.item)) {
                    Rating prediction = predict(r.rater, r.item, method,
						numItemNeighbors, numRaterNeighbors);
                    result.addRating(prediction);
                    if (printPredictions)
                        System.out.println("Prediction for rating " + r.seq + " of " + prediction.score + " against " + r.score);
                }

            }
        }

        return result;
    }

    /**
     * Get most similar raters to a specific rater
     * @param rater name of rater to retrieve
     * @return precomputed list of similar raters
     */
    public SimilarityTable getTopRaterMatches(String rater)
    {
        return raterNeighbors.get(rater);
    }

    /**
     * Get most similar items to specified item
     * @param item name of item to retrieve
     * @return precomputed list of similar items
     */
    public SimilarityTable getTopItemMatches(String item)
    {
        return itemNeighbors.get(item);
    }

    /**
     * Rank items that haven't been rated by rater by predicted rating
     *
     * @param rater name of rater to consider
     * @param method how to make predictions
     * @param neighbors how many neighbors to use in NN
     * @return Predictions ranking items by rater's predicted recommendations
     */
    public Collection<Rating> getItemRecommendations(String rater, Method method, int numItemNeighbors, int numRaterNeighbors) {
        ArrayList<Rating> result = new ArrayList<Rating>();
        Enumeration<String> e = itemData.keys();
        while (e.hasMoreElements()) {
            String item = e.nextElement();
            if (raterData.get(rater).getRatingFor(item) != null)
                continue;
            result.add(predict(rater, item, method, numItemNeighbors, numRaterNeighbors));
        }
        Collections.sort(result, Rating.Comp);
        return result;
    }

    /**
     * Rank raters that haven't rated item by predicted rating
     *
     * @param item name of item to rate
     * @param method how to make predictions
     * @param neighbors how many neighbors to use in NN
     * @return Predictions ranking raters by predicted recommendation for item
     */
    public Collection<Rating> getRaterRecommendations(String item, Method method, int numItemNeighbors, int numRaterNeighbors)
    {
        ArrayList<Rating> result = new ArrayList<Rating>();
        Enumeration<String> e = raterData.keys();
        while (e.hasMoreElements()) {
            String rater = e.nextElement();
            if (raterData.get(rater).getRatingFor(item) != null)
                continue;
            result.add(predict(rater, item, method, numItemNeighbors, numRaterNeighbors));
        }
        Collections.sort(result, Rating.Comp);
        return result;
    }

    /**
     * Display raters and their ratings stored in the rating dictionary
     */
    public void print() {
        System.out.println();
        int numRatings = 0;
        for (RatingTable c : raterData.values()) {
            System.out.print("critic name: " + c.getName() + " ");
            Collection<Rating> ratings = c.getRatings();
            for (Rating r : ratings) {
                System.out.print("- '" + r.item + "': " + r.rawScore + " ");
                numRatings++;
            }
            System.out.println();
        }
        System.out.println();
        System.out.println(">> " + raterData.size() + " critics and "
                + numRatings + " ratings");
    }

    /**
     * Display a certain number of nearest neighbors
     * to each of the items in this rating dictionary
     *
     * @param detail number of items to show
     */
    public void summarizeItemSimilarities(int detail)
    {
        for (SimilarityTable m : itemNeighbors.values()) {
            m.summarize(detail);
        }
    }

}
