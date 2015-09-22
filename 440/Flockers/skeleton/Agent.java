import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Class description for something that (may) 
 * perceive its environment, 
 * decide what to do,
 * and do it.
 * 
 * @author Matthew Stone
 * @version 1.0
 */
public abstract class Agent {

    /**
     * Class information
     */
    
    /** Convenience variables for reading and writing XML code */
    static final String OPEN = "=\"";
    static final String CLOSE = "\" ";

    /** Attribute name for integer giving identity of agent to change */
    static final String ID_PARAM = "id";

    /** XML element tag name for updates */
    static final String UPDATE = "update";

    /** Conversion constants */
    /** How many radians in a degree */
    static final double DEGREES_TO_RADIANS = Math.PI / 180;
    /** How many degrees in a radian */
    static final double RADIANS_TO_DEGREES = 180 / Math.PI;
    /** Numerical value corresponding to half a circle in defaults */
    static final double HALF_CIRCLE = Math.PI;

    /** 
     * Attributes of the agent that change only slowly, if at all.
     * Organized in a class to facilitate input and output.
     */
    static class FixedAgentAttributes {
        /** Attribute name for how large the agent appears @see size */
        static final String SIZE_PARAM = "size";
        /** Attribute name for largest value for agent speed @see maxSpeedForward */
        static final String MAX_FORWARD_PARAM = "forwardMax";
        /** Attribute name for largest value for agent speed backwards @see maxSpeedBackward */
        static final String MAX_BACKWARD_PARAM = "backwardMax";
        /** Attribute name for maximum acceleration @see maxAccel */
        static final String MAX_ACCEL_PARAM = "accelMax";
        /** Attribute name for maximum braking @see maxDecel */
        static final String MAX_DECEL_PARAM = "decelMax";
        /** Attribute name for maximum turning angle @see maxTurn */
        static final String MAX_TURN_PARAM = "maxTurn";
        /** Attribute name for agent power @see strength */
        static final String STRENGTH_PARAM = "strength";
        /** Attribute name for how much agent can see @see fieldOfViewEachSide */
        static final String FIELD_OF_VIEW_PARAM = "viewAngle";
        /** Attribute name for red component of color */
        static final String R_PARAM = "r";
        /** Attribute name for green component of color */
        static final String G_PARAM = "g";
        /** Attribute name for blue component of color */
        static final String B_PARAM = "b";
        /** Attribute name for debugging information */
        static final String DEBUG_PARAM = "debug";
        /** Attribute name to show that behavior reflects "extensions" */
        static final String EXTENSIONS_PARAM = "with-extensions";
        
        /** How large the agent appears on the screen */
        public int size;
        /** Agent capability: the largest value possible for agent speed (going forward) */
        public double maxSpeedForward;
        /** Agent capability: agent cannot have reverse speed less than -maxSpeedBackward */
        public double maxSpeedBackward;
        /** Agent capability: the largest increase possible to how fast agent is moving */
        public double maxAccel;
        /** Agent capability: the largest decrease possible to how fast agent is moving */
        public double maxDecel;
        /** Agent capability: largest change in angle agent can execute each step */
        public double maxTurn;
        /** How much power an agent has if two agents get in a fight */
        public int strength;
        /** What can the agent see */
        public double fieldOfViewEachSide;
        /** What color should the agent be primarily drawn in */
        public Color color;
        /** Should we visualize debugging information? */
        public boolean debug;
        /** Should we have vanilla or extended behavior? */
        public boolean withExtensions;
        
        /**
         * Constructor for fully specified fixed agent attributes
         * 
         * @param s size of agent
         * @param f maximum speed agent can go forward
         * @param b maximum speed agent can go backward
         * @param a maximum amount agent can accelerate
         * @param d maximum amount agent can slow down
         * @param t maximum angle agent can turn (radians here)
         * @param p strength of agent for deciding conflicts
         * @param v angle to either side that agent can see
         * @param c color in which agent should be drawn
         * @param g should debugging visualization happen
         */
        FixedAgentAttributes(int s, double f, double b, 
                double a, double d, double t,
                int p, double v, Color c, boolean g, boolean e) {
            size = s; maxSpeedForward = f; maxSpeedBackward = b;
            maxAccel = a; maxDecel = d; maxTurn = t;
            strength = p; fieldOfViewEachSide = v;
            color = c; debug = g; withExtensions = e;
        }
        
        /**
         * Copy constructor
         * 
         * @param a attributes to copy
         */
        FixedAgentAttributes(FixedAgentAttributes a) {
            size = a.size;
            maxSpeedForward = a.maxSpeedForward;
            maxSpeedBackward = a.maxSpeedBackward;
            maxAccel = a.maxAccel;
            maxDecel = a.maxDecel;
            maxTurn = a.maxTurn;
            strength = a.strength;
            fieldOfViewEachSide = a.fieldOfViewEachSide;
            color = a.color;
            debug = a.debug;
            withExtensions = a.withExtensions;
        }
        
        /**
         * Initialize or reinitialize agent attributes based on XML data.
         * 
         * Utility function that should be called when each
         * instance of a subclass of agent is created, to
         * make sure that as we add new functionality to the
         * agent simulation, the agents that are created have
         * sensible default behavior.
         * 
         * @param atts SAX attribute structure derived from XML data
         * @param defaults values to use when attributes are unspecified
         * @param locator file information for reporting errors
         * @throws SAXException in case data is formatted wrong
         */
        public void set(Attributes atts, FixedAgentAttributes defaults, Locator locator) 
        throws SAXException {
            size = FlockingReader.getIntParam(atts, SIZE_PARAM, defaults.size, locator);
            maxSpeedForward = FlockingReader.getDoubleParam(atts, MAX_FORWARD_PARAM, defaults.maxSpeedForward, locator);
            maxSpeedBackward = FlockingReader.getDoubleParam(atts, MAX_BACKWARD_PARAM, defaults.maxSpeedBackward, locator);
            maxAccel = FlockingReader.getDoubleParam(atts, MAX_ACCEL_PARAM, defaults.maxAccel, locator);
            maxDecel = FlockingReader.getDoubleParam(atts, MAX_DECEL_PARAM, defaults.maxDecel, locator);
            double degrees = FlockingReader.getDoubleParam(atts, MAX_TURN_PARAM, RADIANS_TO_DEGREES * defaults.maxTurn, locator);
            maxTurn = World.clampToCircle(degrees * DEGREES_TO_RADIANS, 2 * Math.PI);
            strength = FlockingReader.getIntParam(atts, STRENGTH_PARAM, defaults.strength, locator);
            degrees = FlockingReader.getDoubleParam(atts, FIELD_OF_VIEW_PARAM, RADIANS_TO_DEGREES * defaults.fieldOfViewEachSide, locator);
            fieldOfViewEachSide = World.clampToCircle(degrees * DEGREES_TO_RADIANS, 2 * Math.PI);
            int r = FlockingReader.getIntParam(atts, R_PARAM, defaults.color.getRed(), locator);
            int g = FlockingReader.getIntParam(atts, G_PARAM, defaults.color.getGreen(), locator);
            int b = FlockingReader.getIntParam(atts, B_PARAM, defaults.color.getBlue(), locator);
            color = new Color(r,g,b);
            debug = FlockingReader.getBoolParam(atts, DEBUG_PARAM, defaults.debug, locator);
            withExtensions = FlockingReader.getBoolParam(atts, EXTENSIONS_PARAM, defaults.withExtensions, locator);
        }
        
        /**
         * Change agent attributes where they are specified in XML data
         * 
         * @param atts SAX attribute structure derived from XML data
         * @param locator file information for reporting errors
         * @throws SAXException in case data is formatted wrong
         */
        public void update(Attributes atts, Locator locator) 
        throws SAXException {
            set(atts, this, locator);
        }
        
        /**
         * Write XML attributes specifying the agent attribute structure
         * to the file specified by out.
         * 
         * @param out destination file 
         * @throws IOException if writing fails
         */
        public void log(BufferedWriter out) 
        throws IOException {
            double fov = RADIANS_TO_DEGREES * fieldOfViewEachSide;
            double mt = RADIANS_TO_DEGREES * maxTurn;
            
            out.write(
                    SIZE_PARAM + OPEN + Integer.toString(size) + CLOSE +
                    MAX_FORWARD_PARAM + OPEN + Double.toString(maxSpeedForward) + CLOSE +
                    MAX_BACKWARD_PARAM + OPEN + Double.toString(maxSpeedBackward) + CLOSE +
                    MAX_ACCEL_PARAM + OPEN + Double.toString(maxAccel) + CLOSE +
                    MAX_DECEL_PARAM + OPEN + Double.toString(maxDecel) + CLOSE +
                    MAX_TURN_PARAM + OPEN + Double.toString(mt) + CLOSE +
                    STRENGTH_PARAM + OPEN + Integer.toString(strength) + CLOSE +
                    FIELD_OF_VIEW_PARAM + OPEN + Double.toString(fov) + CLOSE +
                    R_PARAM + OPEN + Integer.toString(color.getRed()) + CLOSE +
                    G_PARAM + OPEN + Integer.toString(color.getGreen()) + CLOSE +
                    B_PARAM + OPEN + Integer.toString(color.getBlue()) + CLOSE +
                    DEBUG_PARAM + OPEN + Boolean.toString(debug) + CLOSE +
                    EXTENSIONS_PARAM + OPEN + Boolean.toString(withExtensions) + CLOSE +
                    "\n");
        }
        
        /**
         * Constructor based on XML data
         * 
         * @param atts XML data where attributes are described
         * @param defaults what to use for unspecified attributes
         * @param locator for reporting errors
         * @throws SAXException in case of data format problems
         */
        FixedAgentAttributes(Attributes atts, FixedAgentAttributes defaults, Locator locator)
        throws SAXException {
            set(atts, defaults, locator);
        }
        
    }
    
    /** object that provides customizable defaults for new agent parameters */
    static FixedAgentAttributes defaultFixedAgentAttributes =
        new Agent.FixedAgentAttributes(10, 0, 0, 0, 0, 0, 0, HALF_CIRCLE, Color.BLACK, false, false);
    
    /** Class to hold default values for dynamically changing parameters */
    static class DynamicAgentAttributes {
        /** Attribute name for horizontal coordinate of agent @see locX */
        static final String X_PARAM = "x";
        /** Attribute name for vertical coordinate of agent @see locY */
        static final String Y_PARAM = "y";
        /** Attribute name for direction agent is heading @see heading */
        static final String HEADING_PARAM = "heading";
        /** Attribute name for forward velocity @see forwardV */
        static final String FORWARD_PARAM = "speed";
        
        /** Position of the agent horizontally within the world */
        public double locX;
        /** Position of the agent vertically within the world */
        public double locY;
        /** Angle that the agent is facing, 0 to right, PI/2 down, -PI/2 up */
        public double heading;
        /** How many pixels the agent moves forward each time step (can be negative) */
        public double forwardV;

        /**
         * Constructor for fully specified agent attributes
         * 
         * @param x horizontal coordinate of agent
         * @param y vertical coordinate of agent
         * @param h direction agent is facing
         * @param v rate of motion in the forward direction
         */
        DynamicAgentAttributes(double x, double y, double h, double v) {
            locX = x;
            locY = y;
            heading = h;
            forwardV = v;
        }
        
        /**
         * Copy constructor
         * @param a attributes to mirror
         */
        DynamicAgentAttributes(DynamicAgentAttributes a){
            locX = a.locX;
            locY = a.locY;
            heading = a.heading;
            forwardV = a.forwardV;
        }
        
        /**
         * Initialize or reinitialize agent attributes based on XML data
         * 
         * @param atts SAX attribute structure derived from XML data
         * @param defaults values to use when attributes are unspecified
         * @param locator file information for reporting errors
         * @throws SAXException in case data is formatted wrong
         */
        public void set(Attributes atts, DynamicAgentAttributes defaults, Locator locator) 
        throws SAXException {
            locX = FlockingReader.getDoubleParam(atts, X_PARAM, defaults.locX, locator);
            locY = FlockingReader.getDoubleParam(atts, Y_PARAM, defaults.locY, locator);
            double degrees = FlockingReader.getDoubleParam(atts, HEADING_PARAM, RADIANS_TO_DEGREES * defaults.heading, locator);
            heading = World.clampToCircle(degrees * DEGREES_TO_RADIANS, 2 * Math.PI);
            forwardV = FlockingReader.getDoubleParam(atts, FORWARD_PARAM, defaults.forwardV, locator);
        }
        
        /**
         * Change agent attributes where they are specified in XML data
         * 
         * @param atts SAX attribute structure derived from XML data
         * @param locator file information for reporting errors
         * @throws SAXException in case data is formatted wrong
         */
        public void update(Attributes atts, Locator locator) 
        throws SAXException {
            set(atts, this, locator);
        }

        /**
         * Write XML attributes specifying the agent attribute structure 
         * to the file given by out.
         *  
         * @param out destination file
         * @throws IOException if writing fails
         */
        public void log(BufferedWriter out)
        throws IOException {
            double degrees = heading * RADIANS_TO_DEGREES;
            
            out.write(
                    X_PARAM + OPEN + Double.toString(locX) + CLOSE +
                    Y_PARAM + OPEN + Double.toString(locY) + CLOSE +
                    HEADING_PARAM + OPEN + Double.toString(degrees) + CLOSE +
                    FORWARD_PARAM + OPEN + Double.toString(forwardV) + CLOSE +
                    "\n");
        }
        
        /**
         * Constructor based on XML data
         * 
         * @param atts XML data where attributes are specified
         * @param defaults what to use for unspecified attributes
         * @param locator for reporting errors
         * @throws SAXException in case of data format problems
         */
        DynamicAgentAttributes(Attributes atts, DynamicAgentAttributes defaults, Locator locator)
        throws SAXException {
            set(atts, defaults, locator);
        }
    }
    
    /** object that provides defaults for new agent parameters */
    static DynamicAgentAttributes defaultDynamicAgentAttributes =
        new Agent.DynamicAgentAttributes(250, 250, 0, 0);
    
    /**
     * What happens when two agents interact
     */
    
    static enum InteractiveBehavior {
        /** Do nothing */
        COEXIST,
        /** Block others' motion */
        OBSTRUCT,
        /** Kill or be killed */
        ATTACK;
    }
    
    /**
     * Instance members
     */
    
    /** The world the agent belongs to */
    protected World myWorld = null;
    
    /** A numerical code that identifies this agent within the world */
    protected int id = 0;
    
    /** Is this agent still functioning or has something killed it? */
    protected boolean isAlive = true;

    /** What are the general characteristics of the agent */
    protected FixedAgentAttributes form;
    
    /** What is the agent doing right now */
    protected DynamicAgentAttributes status;
    
    /** Actions computed by deliberation that have yet to be acted on */
    protected List<Intention> todo = null;
    
    /** Preserve last status for debugging visualization */
    protected DynamicAgentAttributes lastStatus = null;
    
    /**
     * Accessor methods
     */
    
    /**
     * @return current horizontal coordinate of agent
     */
    public double getLocX() {
        return status.locX;
    }

    /**
     * @return current vertical coordinate of agent
     */
    public double getLocY() {
        return status.locY;
    }

    /**
     * @return direction agent is currently facing
     */
    public double getHeading() {
        return status.heading;
    }

    /**
     * @return rate at which agent is moving in the direction of heading
     */
    public double getForwardV() {
        return status.forwardV;
    }

    /**
     * @return extent of agent
     */
    public int getSize() {
        return form.size;
    }
    
    /**
     * @return unique integer identifying agent in the world
     */
    public int getId() {
        return id;
    } 

    /**
     * @return how many points an agent gets in a fight
     */
    public int getStrength() {
        return form.strength;
    }

    public Color getColor() {
        return form.color;
    }
    
    /**
     * @return true if the agent is still treated as alive
     *         for the purposes of simulation.
     */
    public boolean isAlive() {
        return isAlive;
    }
    
    public double getFieldOfViewEachSide() {
        return form.fieldOfViewEachSide;
    }
    
    /**
     * Mark an agent as dead.
     */
    public void die() {
        isAlive = false;
        form.color = Color.BLACK;
    }
    
    /**
     * Update the agent's current horizontal coordinate to x
     * @param x in pixels
     */
    public void setLocX(double x) {
        status.locX = x;        
    }
    
    /**
     * Update the agent's current vertical coordinate to y
     * @param y in pixels
     */
    public void setLocY(double y) {
        status.locY = y;
    }
    
    /**
     * Update the agent's current direction of travel to heading
     * @param h in radians, 0 is right, PI/2 is down, -PI/2 is up
     */
    public void setHeading(double h) {
        status.heading = h;
    }
    
    /**
     * Update the rate at which the agent is moving in direction of travel
     * @param s in pixels per step, can be negative
     */
    public void setForwardV(double s) {
        status.forwardV = s;
    }
    
    /**
     * Helper functions for action, decision making and user interface
     * these are linked with
     * the way the simulation works and so tend not to
     * be overridden in subclasses
     */
        
    /**
     * Gives the radius of the smallest circle that this agent
     * could move in, given its maximum turn per step,
     * assuming it stays at its current speed.
     * 
     * @return radius in pixels
     */
    protected double turningRadius() {
        return (Math.abs(status.forwardV) / Math.sin(form.maxTurn));
    }
    
    /**
     * Is the passed point (obtained by mouse click for example)
     * within the area at which this agent might appear.
     * HEADS UP - does not take into account that the world is a torus.
     * 
     * @param x horizontal coordinate of click
     * @param y vertical coordinate of click
     * @return inside or outside
     */
    public boolean isInside(int x, int y) {
        return (x >= status.locX - form.size / 2 && x < status.locX + form.size / 2 &&
                y >= status.locY - form.size / 2 && y < status.locY + form.size / 2);
    }

    /**
     * Attempt to change the rate at which agent is moving 
     * in the direction of travel by an increment of DELTA
     * in the current time step.  Enforces bounds on 
     * minimum and maximum acceleration.
     * 
     * @param delta in pixels per step per step
     */
    private void changeSpeed(double delta) {
        boolean isAccelerating = 
            (status.forwardV > 0 && delta > 0) ||
            (status.forwardV < 0 && delta < 0);     

        double limit = isAccelerating ? form.maxAccel : form.maxDecel;
        double difference = Math.min(Math.abs(delta), limit);
        
        if (delta < 0) {
            status.forwardV -= difference;
            if (status.forwardV < -form.maxSpeedBackward)
                status.forwardV = -form.maxSpeedBackward;
        }
        else {
            status.forwardV += difference;
            if (status.forwardV > form.maxSpeedForward)
                status.forwardV = form.maxSpeedForward;
        }
    }

    /**
     * Change the heading of the agent.  The desired change
     * is delta; however bounds on maximum turning per step
     * are enforced.
     * 
     * @param delta in radians, positive right, negative left
     */
    private void turn(double delta) {
        double difference = Math.min(Math.abs(delta), form.maxTurn);
        if (delta < 0) {
            status.heading = World.clampToCircle(status.heading - difference, 2 * Math.PI);
        }
        else {
            status.heading = World.clampToCircle(status.heading + difference, 2 * Math.PI);
        }
    }

    /**
     * Negotiate with the world to move the agent a step of size given by forwardV
     * in the direction given by the current heading.
     */
    private void go() {
        double newLocX = status.locX + status.forwardV * Math.cos(status.heading);
        double newLocY = status.locY + status.forwardV * Math.sin(status.heading);
        myWorld.tryToMove(this, newLocX, newLocY);  
    }

    /**
     * Carry out the motion actions given by the agent's todo list.
     * That includes one turning action, and one action of changing speed.
     * Then move the agent one step.
     */
    public void act() {
        Set<Intention.ActionType> done = new HashSet<Intention.ActionType>();
        
        lastStatus = new DynamicAgentAttributes(status);
        
        for (Intention a: todo)
        {
            Intention.ActionType t = a.getType();
            if (done.contains(t)) {
                System.err.println("Error: repeated action of " + t.description + " ignored");
            }
            done.add(t);
            switch (t) {
            case TURN:
                turn(a.getParam());
                break;
            case CHANGE_SPEED:
                changeSpeed(a.getParam());
                break;
            }
        }
        
        go();
    }

    /**
     * Important methods that subclasses tend to override
     * The XML details are already handled in skeleton code
     */
    
    /**
     * Write a complete XML description of the agent
     * 
     * @param out destination channel for XML element
     * @throws IOException in case writing fails
     */
    public abstract void log(BufferedWriter out) throws IOException;
    
    /**
     * Write an XML description of the dynamic properties of the agent
     * 
     * @param out destination channel for XML element
     * @throws IOException in case writing fails
     */
    public void changelog(BufferedWriter out) throws IOException {
        out.write("   <" + UPDATE + " " + ID_PARAM + OPEN + Integer.toString(id) + CLOSE +
        "\n    ");
        status.log(out);
        out.write("    />\n");
    }
    
    /**
     * Change the parameters of this agent to reflect the information
     * in the passed XML specification
     * 
     * @param atts SAX attributes derived from XML data
     * @param loc file information for reporting errors
     * @throws SAXException in case of data format problems
     */
    public void update(Attributes atts, Locator loc)
    throws SAXException {
        form.update(atts, loc);
        status.update(atts, loc);
    }
    
    
    /**
     * renders a picture of the agent into the world display
     * @param g graphics information
     */
    public abstract void draw(Graphics g);
    
    /**
     * @return the type information that you can tell about agent
     *         by looking at it
     */
    public Percept.ObjectCategory looksLike() {
        if (isAlive)
            return Percept.ObjectCategory.BOID;
        else
            return Percept.ObjectCategory.CORPSE;
    }

    /** 
     * A simple rule says what kind of behavior one agent has when
     * it interacts with another agent in the same space.
     * By default, nothing happens - the two agents overlap
     * and continue to do their own thing.
     * 
     * @param neighbor A category of agent nearby
     * @return Agent's disposition for what to do with another agent
     * 
     */
    public InteractiveBehavior behaviorOnApproach(Percept.ObjectCategory neighbor) {
        return InteractiveBehavior.COEXIST;
    }

    /**
     * Method each agent uses to update its
     * internal todo list on the basis of a 
     * new round of perceptual information
     * 
     * @param ps Specification of the other agents in the world
     *           from the agent's perspective
     */
    public abstract void deliberate(List<Percept> ps);
}
