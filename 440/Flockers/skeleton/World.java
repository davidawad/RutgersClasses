import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * World calculates and displays the dynamics of a bunch of agents
 * acting in an environment.
 * 
 * Revised to include double buffering drawing,
 * after: http://download.oracle.com/javase/1.3/docs/guide/awt/designspec/lightweights.html
 * 
 * @author Matthew Stone
 * @version 1.1
 */

public class World extends Canvas {

    /**
     * Static class definitions
     */

    /**
     * Java AWT components are required to be serializable,
     * and therefore require a long int id indicating what 
     * version of the code the serialization comes from.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Tolerance for double calculations - can be large
     * since the screen is described by integers
     */
    private static final double EPSILON = 0.01;

    /**
     * Constants specifying the format by which 
     * World constructs are encoded as XML documents.
     */

    /** XML namespace */
    static final String XMLNS = 
        "http://perceptualscience.rutgers.edu/flocking";

    /** Element tag for world objects */
    static final String XML_NAME = "world";

    /** Attribute name for world width */
    static final String WIDTH_PARAM = "width";
    /** Value used when no width specified */
    static final int DEFAULT_WIDTH = 500;

    /** Attribute name for world height */
    static final String HEIGHT_PARAM = "height";
    /** Value used when no height specified */
    static final int DEFAULT_HEIGHT = 500;

    /** Attribute name for XML document recording world history */
    static final String LOGFILE_PARAM = "logfile";

    /** Boolean attribute says whether to do simulation */
    static final String RUNNABLE_PARAM = "runnable";

    /** Boolean attribute for wheter to visualize debugging info */
    static final String DEBUG_PARAM = "debug";

    /** Element tag for delay in replaying log data */
    static final String WAIT_NAME = "wait";

    /** Attribute name for amount of delay, in milliseconds */
    static final String WAIT_INTERVAL = "time";

    /** Delay initiated by waiting when nothing specified, gives 50fps */
    static final int DEFAULT_WAIT = 50;

    /** Element tag for state giving snapshot of world history */
    static final String STATE_NAME = "state";

    /** Attribute for index of state */
    static final String STEP_NAME = "step";

    /** Element tag for death */
    static final String DIE_NAME = "kill";

    /**
     * The world is a torus: the bottom of the window is the same
     * place as the top of the window and the left edge of the
     * window is the same place as the right edge of the window.
     * This makes things easier - we don't have to worry about 
     * objects running up against the edge of the world for example.
     * Mathematically, to see why this defines a torus, imagine
     * "folding up" the window like a piece of rubber until the top
     * and bottom edges of the window are right next to each other
     * and gluing them together.  That gives an open cylinder.
     * Now bend the edges of the cylinder around so the two
     * circles corresponding to the left and right boundaries
     * of the window meet, and glue them together.  On this
     * doughnut shape, what we drew on the screen as the top 
     * and bottom and left and right really are the same place!
     * 
     * We now define some helper functions for reasoning about
     * values in a circle.
     */

    /**
     * Computes the shortest displacement from value FROM to 
     * value TO on a circle of circumference LIMIT.  FROM and
     * TO are assumed to lie in the interval [0, LIMIT).
     * The displacement has absolute value at most LIMIT/2:
     * If it looks like two points are more than LIMIT/2
     * apart, it's shorter to go the other way around!
     * 
     * @param from starting point
     * @param to   ending point
     * @param limit circumference
     * @return how to get from start to end fastest
     */
    public static int displacementOnCircle(int from, int to, int limit) {
        int delta = to - from;
        if (delta > limit / 2) {
            delta -= limit;
        }
        if (delta < -limit / 2) {
            delta += limit;
        }
        return delta;
    }

    /**
     * Computes the shortest displacement from value FROM to 
     * value TO on a circle of circumference LIMIT.  FROM and
     * TO are assumed to lie in the interval [0, LIMIT).
     * The displacement has absolute value at most LIMIT/2:
     * If it looks like two points are more than LIMIT/2
     * apart, it's shorter to go the other way around!
     * 
     * @param from starting point
     * @param to   ending point
     * @param limit circumference
     * @return how to get from start to end fastest
     */
    public static double displacementOnCircle(double from, double to, double limit) {
        double delta = to - from;
        if (delta > limit / 2) {
            delta -= limit;
        }
        if (delta < -limit / 2) {
            delta += limit;
        }
        return delta;       
    }

    /**
     * Normalizes value so it lies within the interval 
     * [0, limit).
     * 
     * @param value coordinate on circular axis
     * @param limit circumference on circular axis
     * @return normalized value
     */
    public static int clampToCircle(int value, int limit) {
        while (value < 0)
            value += limit;
        while (value >= limit) 
            value -= limit;
        return value;
    }

    /**
     * Normalizes value so it lies within the interval 
     * [0, limit).
     * 
     * @param value coordinate on circular axis
     * @param limit circumference on circular axis
     * @return normalized value
     */
    public static double clampToCircle(double value, double limit) {
        while (value < 0)
            value += limit;
        while (value >= limit)
            value -= limit;
        return value;
    }

    /**
     * Collision detection
     * 
     * We use a sloppy and brute-force version of collision detection.
     * We don't take into account the exact shapes of the agents.
     * We just check whether the (x,y) point of one agent intersects
     * the (x,y,x+size,y+size) rectangle of the other agent.
     * We don't worry about the case where both agents are moving;
     * we treat the obstacle agent as though it's standing still.
     * We use brute force to handle the fact that the world is a torus.
     * We separately check the four ways the obstacle could wrap around.
     * It is possible to build highly optimized data structures
     * that minimize the number of pairwise collision checks that need to 
     * be made at each time step.  We don't implement anything like this.
     * We ultimately check every pair of agents for collision at each
     * time step.
     * 
     * The algorithms for collision detection were adapted from GPL 
     * code on the web at two places:
     * http://geosoft.no/software/geometry/Geometry.java.html
     * http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline2d/
     * Custom mods handle the case of the torus world.
     */

    /**
     * Returns true if c lies in the interval between a and b 
     * inclusive.  Does not assume that a is smaller than b.
     * Model at http://geosoft.no/software/geometry/Geometry.java.html
     * 
     * @param a one limit of the interval
     * @param b other limit of the interval
     * @param c point to test whether it lies in the interval
     */
    public static boolean isBetween(double a, double b, double c) {
        if (a < b)
            return (a <= c && c <= b);
        else
            return (b <= c && c <= a);
    }

    /**
     * Check if two points are on the same side of a given line.
     * Algorithm from Sedgewick page 350.
     * Model at http://geosoft.no/software/geometry/Geometry.java.html
     * 
     * @param lx0, ly0, lx1, ly1  The line.
     * @param px0, py0        First point.
     * @param px1, py1        Second point.
     * @return                <0 if points on opposite sides.
     *                        =0 if one of the points is exactly on the line
     *                        >0 if points on same side.
     */
    private static int sameSide (double lx0, double ly0, double lx1, double ly1,
            double px0, double py0, double px1, double py1)
    {
        int  sameSide = 0;

        double dx  = lx1  - lx0;
        double dy  = ly1  - ly0;
        double dx1 = px0 - lx0;
        double dy1 = py0 - ly0;
        double dx2 = px1 - lx1;
        double dy2 = py1 - ly1;

        // Cross product of the vector from the endpoint of the line to the point
        double c1 = dx * dy1 - dy * dx1;
        double c2 = dx * dy2 - dy * dx2;

        if (c1 != 0 && c2 != 0)
            sameSide = c1 < 0 != c2 < 0 ? -1 : 1;
        else if (dx == 0 && dx1 == 0 && dx2 == 0)
            sameSide = !isBetween (ly0, ly1, py0) && !isBetween (ly0, ly1, py1) ? 1 : 0;
        else if (dy == 0 && dy1 == 0 && dy2 == 0)
            sameSide = !isBetween (lx0, lx1, px0) && !isBetween (lx0, lx1, px1) ? 1 : 0;

        return sameSide;
    }

    /**
     * Check if two line segments intersect. 
     * Model at http://geosoft.no/software/geometry/Geometry.java.html
     * @param x0, y0, x1, y1  End points of first line to check.
     * @param x2, y2, x3, y3  End points of second line to check.
     * @return                True if the two lines intersects.
     */
    public static boolean isLineIntersectingLine (double x0, double y0, double x1, double y1,
            double x2, double y2, double x3, double y3)
    {
    	int s1 = World.sameSide (x0, y0, x1, y1, x2, y2, x3, y3);
        int s2 = World.sameSide (x2, y2, x3, y3, x0, y0, x1, y1);

        return s1 <= 0 && s2 <= 0;
    }

    /**
     * Get point where two line segments intersect. 
     * Assumes that the line segments do intersect 
     * Model at http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline2d/
     * @param x0, y0, x1, y1  End points of first line.
     * @param x2, y2, x3, y3  End points of second line.
     * @return coordinate on first line of intersection
     */
    public static double getIntersection(double x0, double y0, double x1, double y1,
            double x2, double y2, double x3, double y3)
    {
        final double dxA = x1 - x0;
        final double dyA = y1 - y0;
        final double dxB = x3 - x2;
        final double dyB = y3 - y2;
        final double cx = x0 - x2;
        final double cy = y0 - y2;

        // lines are parallel -- given intersection this means 
        // they overlap; want to return either (x2,y2) or (x3,y3)
        if (dyB * dxA == dxB * dyA) {
            if (dxA != 0) {
                if (isBetween(x0, x1, x2)) {
                    return -cx / dxA;
                } else {
                    return (x3 - x0) / dxA;
                }
            } else if (isBetween(y0, y1, y2)) {
                return -cy / dyA;
            } else {
                return (y3 - y0) / dyA;
            }
        }

        // general case
        return (dxB * cy - dyB * cx) / (dyB * dxA - dxB * dyA);
    }

    /**
     * Convenience function for Double objects---that might be null
     * @param d1
     * @param d2
     * @return Double object with smallest value from d1 and d2
     *         or null if both d1 and d2 are null
     */
    public static Double min(Double d1, Double d2) {
        if (d1 == null)
            return d2;
        if (d2 == null)
            return d1;
        if (d1.doubleValue() <= d2.doubleValue())
            return d1;
        else
            return d2;
    }

    /**
     * How far do you get on path from (ax0, ay0) to (ax1, ay1)
     * before you intersect the rectangle at (bl, bt, br, bb)?
     */
    public static Double detectOverlap(double ax0, double ay0, double ax1, double ay1,
            double bl, double bt, double br, double bb)
    {
        Double closest = null;

        // left
        if (isLineIntersectingLine(ax0, ay0, ax1, ay1, bl, bt, bl, bb)) {
            closest = min(closest, getIntersection(ax0, ay0, ax1, ay1, bl, bt, bl, bb));            
        }
        // right
        if (isLineIntersectingLine(ax0, ay0, ax1, ay1, br, bt, br, bb)) {
            closest = min(closest, getIntersection(ax0, ay0, ax1, ay1, br, bt, br, bb));
        }

        // bottom
        if (World.isLineIntersectingLine(ax0, ay0, ax1, ay1, bl, bb, br, bb)) {
            closest = min(closest, getIntersection(ax0, ay0, ax1, ay1, bl, bb, br, bb));
        }

        // top
        if (isLineIntersectingLine(ax0, ay0, ax1, ay1, bl, bt, br, bt)) {
            closest = min(closest, getIntersection(ax0, ay0, ax1, ay1, bl, bt, br, bt));
        }

        return closest;
    }

    /**
     * Detect collision
     * 
     * @param a moving agent
     * @param ax1 x coordinate of endpoint of a's path at this time step
     * @param ay1 y coordinate of endpoint of a's path at this time step
     * @param wx  width of the world (for torus computations)
     * @param wy  height of the world (for torus computations)
     * @param b obstacle agent that a potentially may collide with
     * @returns Double whose double value gives the fraction of the path from 
     *          the current location of a to (ax1, ay1) that a can travel
     *          before colliding with b
     *          or null if a will not collide with b on this time step
     */
    public static Double detectCollision(Agent a, double ax1, double ay1, int wx, int wy, Agent b)
    {
        final double ax0 = a.getLocX();
        final double ay0 = a.getLocY();
        final double size = (double) b.getSize() / 2;
        final double bl = b.getLocX() - size;
        final double bt = b.getLocY() - size;
        final double br = b.getLocX() + size;
        final double bb = b.getLocY() + size;

        return min(min(detectOverlap(ax0, ay0, ax1, ay1, bl, bt, br, bb),
                detectOverlap(ax0, ay0, ax1, ay1, bl - wx, bt, br - wx, bb)),
                min(detectOverlap(ax0, ay0, ax1, ay1, bl, bt - wy, br, bb - wy),
                        detectOverlap(ax0, ay0, ax1, ay1, bl - wx, bt - wy, br - wx, bb - wy)));
    }

    /**
     * Instance members
     */

    /** All the active entities that "live" in the world */
    private List<Agent> agents;
    /** The agent that is currently being dragged by the user in the UI */
    private Agent clicked;
    /** Where dynamaics history should be written, null means don't write */
    private String logfile;
    /** If runnable is false this is inert history data */
    private boolean runnable;
    /** Default amount of time to wait between steps of simulation */
    private int delay;
    /** Whether to visualize debugging info */
    private boolean debug;
    /** How many steps of simulation have been run */
    private int stepCount;

    /** image for double buffering */
    private Image offscreen;
    
    /**
     * Instance code
     */

    /**
     * Helper class that organizes the UI processing
     * so you can click on agents and select them.
     */
    class ClickToSelectAgent extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            for (Agent a: agents) {
                if (a.isInside(x, y)) {
                    clicked = a;
                    break;
                } 
            }
        }

        public void mouseReleasedEvent(MouseEvent e) {
            clicked = null;
        }
    }

    /**
     * Helper class that organizes the UI processing
     * so you can drag agents and move them.
     */
    class DragToMoveAgent extends MouseMotionAdapter {
        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            if (clicked != null) {
                clicked.setLocX(x);
                clicked.setLocY(y);
            }
        }
    }

    /**
     * Constructor for new environments
     * 
     * @param width horizontal extent of the environment
     * @param height vertical extent of the environment
     * @param log file name to record dynamics history
     * @param run true to get new dynamics, false to replay old ones
     * @param wait number of milliseconds to delay between simulation steps
     */
    public World(int width, int height, String log, boolean run, int wait, boolean debug) {
        setSize(width, height);
        logfile = log;
        runnable = run;
        delay = wait;
        agents = new LinkedList<Agent>();
        this.debug = debug;
        stepCount = 0;
        addMouseListener(new ClickToSelectAgent());
        addMouseMotionListener(new DragToMoveAgent());
        offscreen = null;
    }

    /**
     * Getters and setters
     */

    /**
     * Get what step the simulation has gotten to
     * @return current step value of simulation
     */
    public int getStepCount() {
        return stepCount;
    }

    /**
     * Set what step the simulation has gotten to
     * @returns current step value of simulation
     */
    public void setStepCount(int s) {
        stepCount = s;
    }

    /**
     * Should this environment display new dynamics
     * @return true if yes, false if replaying old data
     */
    public boolean isRunnable() {
        return runnable;
    }

    /**
     * @return amount of time in milliseconds to wait between simulation steps
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Attach a new agent to the world environment
     * @param a agent object to add
     */
    public void addAgent(Agent a) {
        agents.add(a);
    }

    /**
     * Remove an agent from the world environment.
     * Useful if a has died or been eaten.
     * 
     * @param a agent object to remove.
     */
    public void removeAgent(Agent a) {
        agents.remove(a);
    }

    /**
     * Find the agent by the specified id
     * 
     * @param id creation index for some agent associated with the world
     * @return agent object if found, null otherwise
     */
    public Agent getAgent(int id) {
        for (Agent a: agents) {
            if (id == a.getId()) {
                return a;
            }
        }
        return null;
    }

    /**
     * Producing change logs
     */

    /**
     * Open XML log file - if world is supposed to have one -
     * and write header information giving world parameters.
     * Then describe each of the agents in the world,
     * in complete detail, giving the initial state
     * of the simulation.
     */
    public void startLogging() {
        if (logfile != null) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(logfile, false));
                out.write("<?xml version=\"1.0\"?>\n\n");
                out.write("<" + XML_NAME + 
                        " xmlns=\"" + XMLNS +
                        "\" " + WIDTH_PARAM +
                        "=\"" + Integer.toString(getWidth()) +
                        "\" " + HEIGHT_PARAM +
                        "=\"" + Integer.toString(getHeight()) +
                        "\" " + RUNNABLE_PARAM + 
                        "=\"false\" " + DEBUG_PARAM +
                        "=\"true\" >\n"
                );
                out.write("  <" + STATE_NAME + " " +
                        STEP_NAME + "=\"" + Integer.toString(stepCount) + "\" >\n");
                for (Agent a: agents) {
                    a.log(out);
                }
                out.write("  </" + STATE_NAME + ">\n");
                out.write("  <" + WAIT_NAME + " " + WAIT_INTERVAL + "=\"" +
                        Integer.toString(DEFAULT_WAIT) + "\"/>\n");
                out.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Open XML log file - if world is supposed to have one -
     * and write final close ending main XML element.
     */ 
    public void finishLogging() {
        if (logfile != null) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(logfile, true));
                out.write("</" + XML_NAME + ">\n\n");
                out.close();
            } catch (IOException e) {
            }
            logfile = null;
        }
    }

    /**
     * Open XML log file - if world is supposed to have one -
     * and append state description describing the dynamic parameters
     * of all the agents in the environment at the current
     * time step.
     */
    private void logStep() {
        if (logfile != null) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(logfile, true));
                out.write("  <" + STATE_NAME + " " +
                        STEP_NAME + "=\"" + Integer.toString(stepCount) + "\">\n");
                for (Agent a: agents) {
                    a.changelog(out);
                }
                out.write("  </" + STATE_NAME + ">\n");
                out.write("  <" + WAIT_NAME + " " + WAIT_INTERVAL + "=\"" +
                        Integer.toString(DEFAULT_WAIT) + "\"/>\n");
                out.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Open XML log file - if world is supposed to have one -
     * and append instructions to remove display of agent a
     * for subsequent steps of the simulation.
     * @param a agent that should not be rendered in future steps
     */
    private void logDeath(Agent a) {
        if (logfile != null) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(logfile, true));
                out.write("  <" + DIE_NAME + " " + Agent.ID_PARAM + "=\"" + Integer.toString(a.getId()) + "\" />\n");
                out.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Callback method when window is resized
     * (among other things)
     */
    public void invalidate() {
    	super.invalidate();
    	offscreen = null;
    }
    
    /**
     * override update to *not* erase the background
     */
    public void update(Graphics g) {
    	paint(g);
    }
    
    /**
     * Callback method to redisplay the world
     */
    public void paint(Graphics og) {
    	int width = getSize().width;
    	int height = getSize().height;
    	if (offscreen == null) {
    		offscreen = createImage(width, height);
    	}
    	Graphics g = offscreen.getGraphics();
    	g.setClip(0,0, width, height);
    	g.clearRect(0,0, width, height);
    	if (debug) {
    	    g.setColor(Color.BLACK);
            g.drawString(Integer.toString(stepCount), 3, getHeight() - 3);
        }

        for (Agent a: agents) {
            a.draw(g);
        }
        
        og.drawImage(offscreen, 0, 0, this);
        g.dispose();
    }

    /**
     * Drawing methods that handle the fact that things are on a torus
     * Draws things four times - assumes objects are small
     */

    /**
     * Wrapper for fillPolygon method of Graphics object
     */
    public void fillPolygon(int[] xpoints, int[] ypoints, int numPoints, Graphics g) {
        // get screen bounds
        int xoffset = getWidth();
        int yoffset = getHeight();

        // relocate polygon as best as you can within screen bounds
        int xdiff = clampToCircle(xpoints[0], xoffset) - xpoints[0];
        int ydiff = clampToCircle(ypoints[0], yoffset) - ypoints[0];
        for (int i = 0; i < numPoints; i++) {
            xpoints[i] += xdiff;
            ypoints[i] += ydiff;
        }

        // figure out what quadrant polygon lies in and plan accordingly
        if (xpoints[0] < xoffset / 2)
            xoffset = -xoffset;
        if (ypoints[0] < yoffset / 2)
            yoffset = -yoffset;

        // draw initial polygon
        g.fillPolygon(xpoints, ypoints, numPoints);

        // wrap around horizontally
        for (int i = 0; i < numPoints; i++) {
            xpoints[i] += xoffset;
        }
        g.fillPolygon(xpoints, ypoints, numPoints);

        // wrap around vertically
        for (int i = 0; i < numPoints; i++) {
            ypoints[i] += yoffset;
        }
        g.fillPolygon(xpoints, ypoints, numPoints);

        // unwrap horizontally
        for (int i = 0; i < numPoints; i++) {
            xpoints[i] -= xoffset;
        }
        g.fillPolygon(xpoints, ypoints, numPoints);
    }

    /**
     * Wrapper for fillOval method of graphics object
     */
    public void fillOval(int x, int y, int width, int height, Graphics g) {
        // get screen bounds
        int xoffset = getWidth();
        int yoffset = getHeight();

        // relocate oval within screen bounds
        x = clampToCircle(x, xoffset);
        y = clampToCircle(y, yoffset);

        // figure out what quadrant we're in and plan accordinatly
        if (x < xoffset / 2)
            xoffset = -xoffset;
        if (y < yoffset / 2)
            yoffset = -yoffset;

        // draw oval four times
        g.fillOval(x, y, width, height);
        g.fillOval(x + xoffset, y, width, height);
        g.fillOval(x, y + yoffset, width, height);
        g.fillOval(x + xoffset, y + yoffset, width, height);
    }

    /**
     * Wrapper for fillRect method of graphics object
     */
    public void fillRect(int x, int y, int width, int height, Graphics g) {
        // get screen bounds
        int xoffset = getWidth();
        int yoffset = getHeight();

        // relocate rectangle within screen bounds
        x = clampToCircle(x, xoffset);
        y = clampToCircle(y, yoffset);

        // figure out what quadrant we're in and plan accordinatly
        if (x < xoffset / 2)
            xoffset = -xoffset;
        if (y < yoffset / 2)
            yoffset = -yoffset;

        // draw oval four times
        g.fillRect(x, y, width, height);
        g.fillRect(x + xoffset, y, width, height);
        g.fillRect(x, y + yoffset, width, height);
        g.fillRect(x + xoffset, y + yoffset, width, height);
    }

    /**
     * Wrapper for drawLine method of graphics object
     */
    public void drawLine(int x1, int y1, int x2, int y2, Graphics g) {
        // get screen bounds
        int xoffset = getWidth();
        int yoffset = getHeight();

        // relocate line within screen bounds
        int mx = clampToCircle(x1, xoffset) - x1;
        int my = clampToCircle(y1, yoffset) - y1;
        x1 += mx;
        x2 += mx;
        y1 += my;
        y2 += my;

        // iteratively draw successive segments within screen bounds
        // fraction is the ratio to the next point where you go out of bounds
        double fraction;
        do {
            double xfraction = 2;
            if (x2 != x1) {
                xfraction = (((x2 > x1) ? xoffset : 0) - x1) / (double) (x2 - x1);
            }
            double yfraction = 2;
            if (y2 != y1) {
                yfraction = (((y2 > y1) ? yoffset : 0) - y1) / (double) (y2 - y1);
            }
            fraction = Math.min(xfraction, yfraction);
            if (fraction < 1) {
                // draw up to fraction
                int x3 = x1 + (int)Math.round(fraction * (x2 - x1));
                int y3 = y1 + (int)Math.round(fraction * (y2 - y1));
                g.drawLine(x1, y1, x3, y3);

                // recenter remaining segment in screen bounds
                mx = (int) Math.round(clampToCircle(x3 + ((x2 > x1) ? EPSILON : -EPSILON), xoffset)) - x3;
                my = (int) Math.round(clampToCircle(y3 + ((y2 > y1) ? EPSILON : -EPSILON), yoffset)) - y3;
                x1 = x3 + mx;
                x2 += mx;
                y1 = y3 + my;
                y2 += my;
            }
        } while (fraction < 1);

        g.drawLine(x1, y1, x2, y2);
    }

    /**
     * Constructing percepts
     */

    /**
     * Compute how far away Agent TO is from Agent FROM
     * 
     * @param from Agent doing the sensing
     * @param to   Agent being sensed
     * @return how many pixels TO is from FROM
     */
    protected double distance(Agent from, Agent to) {
        double dx = displacementOnCircle(from.getLocX(), to.getLocX(), getWidth());
        double dy = displacementOnCircle(from.getLocY(), to.getLocY(), getHeight());
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Compute the angle where Agent TO lies in
     * Agent FROM's field of view: 0 is straight ahead,
     * PI/2 is directly to the right;
     * -PI/2 is directly to the left.
     * (Higher values of Y are lower on the screen.)
     * 
     * @param from Agent doing the sensing
     * @param to Agent being sensed
     * @return angle in radians of TO wrt FROM's heading
     */
    protected double direction(Agent from, Agent to) {
        double dx = displacementOnCircle(from.getLocX(), to.getLocX(), getWidth());
        double dy = displacementOnCircle(from.getLocY(), to.getLocY(), getHeight());
        return displacementOnCircle(from.getHeading(), Math.atan2(dy, dx), 2 * Math.PI);
    }

    /**
     * Compute the direction that Agent OF seems to
     * be going, when viewed from Agent WRT.
     * O is straight ahead.
     * PI/2 is directly to the right.
     * -PI/2 is directly to the left.
     * (Higher values of Y are lower on the screen.)
     * 
     * @param wrt Agent doing the sensing
     * @param of Agent being sensed
     * @return angle in radians that OF seems to be going
     */
    protected double relativeHeading(Agent wrt, Agent of) {
        return displacementOnCircle(wrt.getHeading(), of.getHeading(), 2 * Math.PI);
    }

    /**
     * Constructs the percept that Agent SEER gets
     * from Agent SEEN.
     * Override this method to add noise in sensors,
     * and other aspects of simulated visual cognition.
     * 
     * @param seer Agent who will be supplied this percept
     * @param seen Agent that this percept describes
     * @return Element of list giving input to SEER's deliberation
     */
    protected Percept senseAgent(Agent seer, Agent seen) {
        double direction = direction(seer,seen);

        if (Math.abs(direction) <= seer.getFieldOfViewEachSide()) {
            // Seer sees things exactly as they are
            return new Percept(seen.looksLike(),
                    seen.getColor(),
                    distance(seer, seen),
                    direction(seer, seen),
                    relativeHeading(seer, seen),
                    seen.getForwardV());
        }
        else
            return null;

    }

    /**
     * Run a step of deliberation on Agent a.
     * Construct the percept A gets now and
     * feed it to A's deliberation method.
     * Override this method to add visibility checks
     * and other aspects of simulated visual cognition.
     * 
     * @param a One of the agents in the world
     */
    protected void makeAgentThink(Agent a) {

        // A can see everybody else in the world
        List<Percept> ps = new LinkedList<Percept>();
        for (Agent seen : agents) {
            if (seen != a) {
                Percept p = senseAgent(a,seen);
                if (p != null)
                    ps.add(p);
            }
        }

        a.deliberate(ps);
    }

    /**
     * Process the simulated input to agent A's effectors
     * designed to get A to location (newX, newY) in the world.
     * Always succeeds, but normalizes the new position to
     * reflect the fact that the world is a torus.
     * Override this method to create obstacles that
     * an agent can't pass through and other aspects
     * of more realistic simulation of the world.
     * 
     * @param a Agent who wants to move
     * @param newX Desired updated horizontal coordinate
     * @param newY Desired updated vertical coordinate
     */
    public void tryToMove(Agent a, double newX, double newY) {
        Double collision = null;
        Agent bumped = null;
        int width = getWidth();
        int height = getHeight();

        // make sure you're actually moving
        if (a.getLocX() == newX && a.getLocY() == newY)
            return;

        int dxunit, dyunit;

        if (newX > a.getLocX()) 
            dxunit = 1;
        else if (newX == a.getLocX())
            dxunit = 0;
        else 
            dxunit = -1;

        if (newY > a.getLocY())
            dyunit = 1;
        else if (newY == a.getLocY())
            dyunit = 0;
        else
            dyunit = -1;

        // check for collisions
        for (Agent b: agents) {
            if (a != b) {
                if (b.behaviorOnApproach(a.looksLike()) == Agent.InteractiveBehavior.OBSTRUCT ||
                        a.behaviorOnApproach(b.looksLike()) == Agent.InteractiveBehavior.ATTACK) {
                    Double c = detectCollision(a, newX, newY, width, height, b);
                    if (c != null) {
                        if (collision == null || collision.doubleValue() > c.doubleValue()) {
                            collision = c;
                            bumped = b;
                        }
                    }
                }
            }
        }

        // limit amount moved by collision
        if (collision != null) {
            newX = a.getLocX() + collision.doubleValue() * (newX - a.getLocX()) - dxunit;
            newY = a.getLocY() + collision.doubleValue() * (newY - a.getLocY()) - dyunit;
        }

        // wrap motion in torus
        a.setLocX(clampToCircle(newX, getWidth()));
        a.setLocY(clampToCircle(newY, getHeight()));

        // process interaction
        if (bumped != null) {
            if (bumped.behaviorOnApproach(a.looksLike()) == Agent.InteractiveBehavior.OBSTRUCT) {
                if (bumped.getStrength() > a.getStrength()) 
                    a.die();
            }
            if (a.behaviorOnApproach(bumped.looksLike()) == Agent.InteractiveBehavior.ATTACK) {
                if (bumped.getStrength() > a.getStrength())
                    a.die();
                else
                    bumped.die();
            }
        }

    }

    /**
     * Remove all the agents from the world that
     * are no longer alive, and log their deaths.
     */
    private void removeCorpses() {
        LinkedList<Agent> alive = new LinkedList<Agent>();
        for (Agent a: agents) {
            if (a.isAlive())
                alive.add(a);
            else
                logDeath(a);
        }

        agents = alive;
    }

    /**
     * Carry out a step of simulation, 
     * in which all the agents perceive, deliberate, and act,
     * and the environment carries out the effects of
     * this behavior.
     */
    public void stepWorld() {
        stepCount++;

        // For each living agent, figure out what there is to do based on
        // the current state of the world
        for (Agent agent: agents) {
            if (agent.isAlive()) {
                makeAgentThink(agent);
            }
        }

        // For each living agent, update the state of each agent based
        // on their decisions
        for (Agent agent: agents) {
            if (agent.isAlive()) {
                agent.act();
            }
        }

        // Give feedback to the designer of the world
        repaint();  
        logStep();
        removeCorpses();
    }
}
