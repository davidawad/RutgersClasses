import java.awt.Color;

/**
 * Class for representing the way one agent appears to another
 * 
 * @author Matthew Stone
 * @version 1.0
 *
 */
public class Percept {
    
    /**
     * A percept shows an agent as being of a particular category.
     * This need not match an agent's class, for example if an
     * agent is camouflaged, but could.  Add additional values
     * here as you extend the world to include new kinds of things.
     */
    static enum ObjectCategory {
        /** A light source */
        LIGHT,
        /** A peaceful creature */
        BOID,
        /** An agressive creature */
        PREDATOR,
        /** A dead creature */
        CORPSE,
        /** An inert object that must be avoided */
        OBSTACLE;
    }
    
    /** What was seen */
    private ObjectCategory objectCategory;
    /** What color it had */
    private Color color;
    /** How far perceived agent was */
    private double distance;
    /** Where perceived agent is relative to you (0 is forward, positive angle is to the right) */
    private double angle;
    /** What direction is perceived agent facing (0 is the same as you, etc...) */
    private double orientation;
    /** How fast is perceived agent going (negative if it's going in reverse) */
    private double speed;

    /**
     * Constructor for percept object
     * 
     * @param c what was seen
     * @param dis how far perceived agent was
     * @param a where perceived agent is relative to you
     * @param h what direction perceived agent is facing
     * @param s how fast perceived agent is going
     */
    public Percept(ObjectCategory c, Color f, double dis, double a, double h, double s) {
        objectCategory = c;
        color = f;
        distance = dis;
        angle = a;
        orientation = h;
        speed = s;
    }
    
    /**
     * Accessor
     * @return what was seen
     */
    public ObjectCategory getObjectCategory() {
        return objectCategory;
    }

    /**
     * Accessor
     * @return color of was seen
     */
    public Color getColor() {
        return color;
    }

    /**
     * Accessor
     * @return how far perceived agent was
     */
    public double getDistance() {
        return distance;
    }
    
    /**
     * Accessor
     * @return where perceived agent is relative to you
     */
    public double getAngle() {
        return angle;
    }
    
    /**
     * Accessor
     * @return what direction perceived agent is facing
     */
    public double getOrientation() {
        return orientation;
    }
    
    /**
     * Acessor
     * @return how fast perceived agent is going
     */
    public double getSpeed() {
        return speed;
    }
    
    /**
     * A measure is a way of associating a cost with
     * each percept.  That allows for general methods
     * that go through a list of percepts and find
     * the one with the least cost.  The notion of 
     * cost involved can vary with the capabilities
     * and interests of the agents getting the percept.
     */
    static interface Measure {
        public double cost(Percept p);
    }
    
    /**
     * A percept measure object where 
     * close objects have the lowest cost.
     */
    static Measure distanceMeasure = new Measure() {
        public double cost(Percept p) {
            return p.getDistance();
        }
    };
    
    /**
     * A percept measure object where
     * objects with a high red component
     * to their color have the lowest cost.
     */
    static Measure redMeasure = new Measure() {
        public double cost(Percept p) {
            return -p.getColor().getRed();
        }
    };
    
    /**
     * A percept measure object where
     * objects with a high green component
     * to their color have the lowest cost.
     */
    static Measure greenMeasure = new Measure() {
        public double cost(Percept p) {
            return -p.getColor().getGreen();
        }
    };
    
    /**
     * A percept measure object where
     * objects with a high blue component
     * to their color have the lowest cost.
     */
    static Measure blueMeasure = new Measure() {
        public double cost(Percept p) {
            return -p.getColor().getBlue();
        }
    };
}
