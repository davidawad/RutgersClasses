import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * The SimilarityTable stores a list of keys ranked by similarity.
 * Each element in the list also includes a model for predicting
 * a value based on a rating for the key.  Prediction assumes
 * a linear model with parameters slope and intercept.
 * 
 * @author Tarek El-Gaaly and Matthew Stone
 *
 */
public class SimilarityTable {

    /**
     * Class for elements in the similarity table
     * @author Tarek El-Gaaly and Matthew Stone
     */
    static public class Similarity {
        /** Name of similar entity (rater or item) */
        public String key;
        /** Degree of similarity */
        public double value;
        /** Covariance for linear model */
        public double slope;
        /** Base for linear model */
        public double intercept;
        
        /**
         * Constructor for similarity object.
         * Just initializes instance members.
         * 
         * @param item name of similar entity
         * @param value degree of similarity
         * @param slope covariance for linear model
         * @param icpt base for linear model
         */
        public Similarity(String item, double value, double slope, double icpt) {
            this.key = item;
            this.value = value;
            this.slope = slope;
            this.intercept = icpt;
        }
        
        /**
         * Assuming a set rating for the similar entity
         * predict the rating of the target entity.
         * 
         * @param from rating for similar entity
         * @return predicted rating for target
         */
        public double predict(double from) 
        {
            double to = from * slope + intercept;
            return to;
        }
        
        /**
         * Assuming a set rating for the target entity,
         * figure out the rating for the similar entity
         * that would lead to a correct prediction.
         * 
         * @param to observed rating for target
         * @return rating that predicts TO
         */
        public double explain(double to)
        {
            double from = (to - intercept) / slope;
            return from;
        }
    }
    
    /**
     * Comparator object for sorting similarity records.
     * Records with a higher similarity value come first.
     */
    static public Comparator<Similarity> Comp = 
        new Comparator<Similarity>() {
        public int compare(Similarity s1, Similarity s2) {
            if (s1.value > s2.value)
                return -1;
            else if (s2.value > s1.value)
                return 1;
            else return s1.key.compareTo(s2.key);
        }
    };
    
    ////////////////////////////////////////////////////
    // INSTANCE SPECS
    ////////////////////////////////////////////////////
    
    /** Name of target entity */
    public String name;
    /** List of similarity records for related entities */
    public ArrayList<Similarity> similarities;
    
    /**
     * Constructs a new similarity table,
     * describing up to maxNeighbors additional keys
     * taken from entities in alternatives
     * that have at least minOverlap ratings in common
     * and are maximally similar to entity
     * according to similarity measure sim
     * 
     * @param entity rating table describing target for prediction
     * @param alternatives rating tables from which predictions can be made
     * @param sim rule for calculating similarity
     * @param minOverlap threshold for number of common ratings
     * @param maxNeighbors limit on table size
     */
    public SimilarityTable(RatingTable entity, 
            Collection<RatingTable> alternatives,
            RatingTable.SimilarityMeasure sim,
            int minOverlap,
            int maxNeighbors) {
        // Initialize
        this.name = entity.getName();
        similarities = new ArrayList<Similarity>();
        
        // Build models for all entities and order by similarity
        for (RatingTable alt: alternatives) {
            if (name.equals(alt.getName())) 
                continue;
            Similarity elt = entity.regressAgainst(alt, sim, minOverlap);
            if (elt != null)
                similarities.add(elt);
        }
        Collections.sort(similarities, Comp);
        
        // Discard similarities that aren't needed and free memory
        int length = similarities.size();
        if (length > maxNeighbors)
            similarities.subList(maxNeighbors, length).clear();
        similarities.trimToSize();
    }
    
    /**
     * Output to standard output a description of the passed
     * similarity table.
     * 
     * @param detail number of records to include in description
     */
    public void summarize(int detail)
    {
        int ct = 0;
        System.out.println("top matches for " + name + ":");
        for (Similarity s : similarities) {
            System.out.println("{ " + s.key + "@ " + s.value + " by y = " +
                    s.slope + " x + " + s.intercept + " }");
            ct++;
            if (ct >= detail) break;
        }
    }
}
