import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.Random;

/**
 * This file creates a data set that lets you reason 
 * carefully about the behavior of similarity models
 * for ratings prediction.
 * 
 * The model here assumes that raters fall into one
 * of a set number of classes corresponding to their
 * taste.  Their taste determines the rating for each
 * item in an arbitrary way.  Other factors are due to noise.
 * 
 * The program defines a main method that takes a number
 * of command-line arguments:
 * - the number of raters in the population (an integer)
 * - the number of items in the collection (an integer)
 * - the number of "taste classes" raters fall in (an integer)
 * - the standard deviation of Gaussian noise on each rating (double)
 * - the number of ratings that will constitute the data set
 * - the file where the data should be stored
 * 
 * The program works by assigning an average rating for
 * each item in each class, and assigning each rater to
 * a class at random.  To calculate a rating, the program
 * chooses a rater and an item at random, gets the
 * rating for the item and the rater's class, adds
 * specified Gaussian noise, and rounds to an integer
 * between 1 and 5 (inclusive).
 * 
 * The resulting data sets should allow collaborative filtering
 * techniques to get very accurate performance (up to the noise).
 * However, rater averages are not useful in predicting scores
 * on new items, and item averages are only roughly useful
 * (they give some evidence about the rating of each class
 * on that item, as ratings are averaged together, but they
 * miss a lot of variance because ratings across classes are
 * not related).  The mixed baseline retains some of the 
 * information from the item baseline but it is weighted 
 * against noise from the rater baseline.  The program 
 * calculates the error that should be expected on each 
 * of these methods.  This is a coarse estimate that ignores:
 * - The effects of random sampling on small data sets
 * - Roundoff errors in converting from double to integer ratings
 * 
 * @author Tarek El-Gaaly and Matthew Stone
 *
 */
public class RecoTestData {

    static Random random = new Random(new Date().getTime());
        
    /**
     * Main method
     * 
     * @param args command-line specification
     */
    public static void main(String[] args) {
        if (args.length != 6) {
            System.out.println("Error: Usage <Num raters> <Num items> <Num classes> <Noise> <Num ratings> <File>");
            System.exit(0);
        }
        try {
            
            // Get parameters from command line
            int arg = 0;
            int raters = Integer.parseInt(args[arg++]);
            int items = Integer.parseInt(args[arg++]);
            int classes = Integer.parseInt(args[arg++]);
            double noise = Double.parseDouble(args[arg++]);
            int ratings = Integer.parseInt(args[arg++]);
            System.out.println("Creating " + ratings + " ratings for file " + args[arg]);
            
            // Set the model parameters
            double rating_means[][] = new double[items][classes];
            double item_means[] = new double[items];
            
            for (int i = 0; i < items; i++) {
                item_means[i] = 0;
                for (int j = 0; j < classes; j++) {
                    rating_means[i][j] = 4 * random.nextDouble() + 1;
                    item_means[i] += rating_means[i][j];
                }
                item_means[i] /= classes;
            }
            
            double class_means[] = new double[classes];
            for (int j = 0; j < classes; j++) {
                class_means[j] = 0;
                for (int i = 0; i < items; i++)
                    class_means[j] += rating_means[i][j];
                class_means[j] /= items;
            }

            int category[] = new int[raters];
            for (int i = 0; i < raters; i++)
                category[i] = random.nextInt(classes);
            
            
            // Simulate and report performance on balanced data
            double t = 0;
            for (int i = 0; i < items; i++) 
                for (int j = 0; j < classes; j++)
                    t +=  (item_means[i] - rating_means[i][j]) * (item_means[i] - rating_means[i][j]);
            
            t /= items * classes;
            t += noise * noise;
            System.out.println("Estimated item baseline RMSE: " + Math.sqrt(t));
            
            t = 0;
            for (int i = 0; i < items; i++) 
                for (int j = 0; j < classes; j++)
                    t +=  (class_means[j] - rating_means[i][j]) * (class_means[j] - rating_means[i][j]);
            
            t /= items * classes;
            t += noise * noise;
            System.out.println("Estimated rater baseline RMSE: " + Math.sqrt(t));

            t = 0;
            for (int i = 0; i < items; i++) 
                for (int j = 0; j < classes; j++) {
                    double p = Math.sqrt(item_means[i] * class_means[j]); 
                    t +=  (p - rating_means[i][j]) * (p - rating_means[i][j]);
                }
            
            t /= items * classes;
            t += noise * noise;
            System.out.println("Estimated mixed baseline RMSE: " + Math.sqrt(t));
            
            // Generate actual ratings randomly from the model
            try {
                BufferedWriter w = new BufferedWriter(new FileWriter(args[arg++]));
                for (int i = 0; i < ratings; i++) {
                    int rater = random.nextInt(raters);
                    int item = random.nextInt(items);
                    double rating = rating_means[item][category[rater]];
                    int round = (int) Math.round(Math.min(5, Math.max(1, rating + noise * random.nextGaussian())));
                    w.write(rater + "\t" + item + "\t" + round + "\n");
                }
                w.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
