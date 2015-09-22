import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * A basic agent that attempts to orient itself towards
 * the closest light source and to approach it as
 * quickly as possible.  Inspired by Braitenberg's
 * (1984) "aggressive" vehicle 2.
 * 
 * @author Matthew Stone
 * @version 1.0
 */

public class Follower extends Agent {

    /**
     * Class information
     */

    /** Tag for XML element */
    static final String XML_NAME = "follower";

    /** Size in display */
    static final int FOLLOWER_SIZE = 15;
    /** Can go forward up to 4 pixels per step */
    static final double FOLLOWER_MAX_SPEED = 4;
    /** Cannot go backward */
    static final double FOLLOWER_MIN_SPEED = 0;
    /** No constraint on acceleration */
    static final double FOLLOWER_MAX_ACCEL = 10;
    /** No constraint on braking */
    static final double FOLLOWER_MAX_DECEL = 10;
    /** Relatively unable to make sharp turns */
    static final double FOLLOWER_MAX_TURN = 6 * Math.PI / 180;

    /** Record that allows XML files to set follower defaults */
    static FixedAgentAttributes defaultFixedAgentAttributes =
        new Agent.FixedAgentAttributes(
                FOLLOWER_SIZE,
                FOLLOWER_MAX_SPEED,
                FOLLOWER_MIN_SPEED,
                FOLLOWER_MAX_ACCEL,
                FOLLOWER_MAX_DECEL,
                FOLLOWER_MAX_TURN,
                0, HALF_CIRCLE, Color.BLUE,
                false, false);

    /** Record that allows XML files to set follower defaults */
    static DynamicAgentAttributes defaultDynamicAgentAttributes =
        new Agent.DynamicAgentAttributes(250, 250, 0, 0);

    /**
     * Instance members
     */

    /**
     * Constructor: initialize general agent fields to describe
     * agent that will follow a light source.
     * 
     * @param w world to which agent belongs
     * @param id number to identify agent in its world
     * @param atts SAX attributes corresponding to XML agent spec
     * @param loc file information for error messages
     * @throws SAXException if data is formatted incorrectly
     */
    public Follower(World w, int id, Attributes atts, Locator loc)
    throws SAXException {
        myWorld = w;
        this.id = id;
        form = new FixedAgentAttributes(atts, defaultFixedAgentAttributes, loc);
        status = new DynamicAgentAttributes(atts, defaultDynamicAgentAttributes, loc);
    }

    /**
     * Output an XML element describing the current state of
     * this follower.
     * 
     * @param out an open file to write to, wrapped in BufferedWriter 
     *            convenience class
     */
    public void log(BufferedWriter out) 
    throws IOException {
        out.write("   <" + XML_NAME + " " + ID_PARAM + OPEN + Integer.toString(id) + CLOSE +
        "\n     ");
        form.log(out);
        out.write("    ");
        status.log(out);
        out.write("    />\n");
    }

    /*
     * Draw a light follower as a solid triangle pointing in the direction
     * of the agent's heading.
     * @param g object to control drawing mechanism 
     * @see Agent#draw(java.awt.Graphics)
     */
    @Override
    public void draw(Graphics g) {
        int[] xpoints = new int[3];
        int[] ypoints = new int[3];

        double baseAngle = status.heading + Math.PI / 2;
        double s = form.size;
        int baseOffsetX = (int) Math.round(2 * s * Math.cos(baseAngle) / 3);
        int baseOffsetY = (int) Math.round(2 * s * Math.sin(baseAngle) / 3);

        int x0 = ((int) Math.round(status.locX - baseOffsetX / 2
                - s * Math.cos(status.heading) / 3));
        int y0 = ((int) Math.round(status.locY - baseOffsetY / 2
                - s * Math.sin(status.heading) / 3));

        xpoints[0] = x0;
        xpoints[1] = x0 + baseOffsetX;
        xpoints[2] = x0 + baseOffsetX / 2 + (int) Math.round(s * Math.cos(status.heading));

        ypoints[0] = y0;
        ypoints[1] = y0 + baseOffsetY;
        ypoints[2] = y0 + baseOffsetY / 2 + (int) Math.round(s * Math.sin(status.heading));

        g.setColor(form.color);
        myWorld.fillPolygon(xpoints, ypoints, 3, g);

        if (form.debug) {
            double length = 
                Math.sqrt(myWorld.getWidth() * myWorld.getWidth() +
                        myWorld.getHeight() * myWorld.getHeight()) / 2;
            int x1 = (int) Math.round(status.locX + length * Math.cos(status.heading));
            int y1 = (int) Math.round(status.locY + length * Math.sin(status.heading));
            g.setColor(form.color);
            myWorld.drawLine((int)Math.round(status.locX),(int)Math.round(status.locY),x1,y1,g);
        }

    }

    /**
     * A follower is one of many agents that 
     * appears like an active agent with peaceful
     * behavior 
     * 
     * @return the generic appearance BOID while
     *         creature is alive, otherwise CORPSE.
     */
    @Override
    public Percept.ObjectCategory looksLike() {
        if (isAlive)
            return Percept.ObjectCategory.BOID;
        else 
            return Percept.ObjectCategory.CORPSE;
    }

    /**
     * The behavior of followers and their subclasses is parameterized
     * by this method: it takes a percept specifying an agent the follower can see,
     * and returns true if the follower might be interested in chasing
     * that agent.  The default follower is interested in any light source.
     * 
     * @param p percept describing potential target
     * @return true if perceived agent is interesting
     */
    protected boolean isTarget(Percept p) {
        return (p.getObjectCategory() == Percept.ObjectCategory.LIGHT);
    }

    /**
     * The behavior of followers and their subclasses is parameterized
     * by this method: it takes a percept and gives a measure of
     * the tradeoff in effort versus goodness would be involved in
     * chasing the agent described here.  The default follower 
     * prefers the closest light source.
     * 
     * @param p percept describing target
     * @return effort involved in chasing this target
     */
    protected double targetCost(Percept p) {
        return (p.getDistance());
    }

    /**
     * Go through a list of percepts and find the 
     * target percept for the follower that has the
     * lowest cost according to the percept measure m.
     * 
     * @param ps list describing each of the things agent can see
     * @param m way to measure the cost of chasing a target
     * @return specific percept for best target
     */
    protected Percept bestTarget(List<Percept> ps, Percept.Measure m) {
        Percept bestTarget = null;
        double bestValue = 0;
        for (Percept p: ps) {
            if (isTarget(p)) {
                if (bestTarget != null) {
                    double quality = m.cost(p);
                    if (quality < bestValue) {
                        bestTarget = p;
                        bestValue = quality;
                    }
                } else {
                    bestTarget = p;
                    bestValue = m.cost(p);
                }
            }
        }
        return bestTarget;
    }

    /**
     * Go through a list of percepts and find the target percept
     * for the follower that has least cost according to the 
     * built-in default targetCost measure of this follower.
     * 
     * @param ps list describing each of the things agent can see
     * @return specific percept - defaults to closest light
     */
    protected Percept bestTarget(List<Percept> ps) {
        Percept.Measure m = new Percept.Measure() { 
            public double cost(Percept p) { 
                return targetCost(p); 
            }
        };

        return bestTarget(ps, m);
    }

    /**
     * Add to the follower's todo list intentions to 
     * adjust speed and angle to get towards the thing
     * described by percept p as quickly as possible
     * 
     * @param p target to aim for
     */
    protected void steerTo(Percept p) {
        // haven't overlapped target 
    	if (p.getDistance() > form.size / 2) {
    		// full speed ahead 
    		double desiredSpeed = form.maxSpeedForward;
    		// turn as much as you can without overshooting
    		double desiredAngle = p.getAngle();

    		todo.add(new Intention(Intention.ActionType.TURN, desiredAngle));
    		todo.add(new Intention(Intention.ActionType.CHANGE_SPEED, desiredSpeed - status.forwardV));
    	} else {
    		// stop
    		todo.add(new Intention(Intention.ActionType.CHANGE_SPEED, -status.forwardV));
    	}
    }

    /**
     * Update agent's todo list.  Here: find the closest light source,
     * turn to face it, and go as fast as you can without overshooting it.
     * 
     * @param ps A description of everything the agent can see
     */
    @Override
    public void deliberate(List<Percept> ps) {
        Percept closestSeen = bestTarget(ps);

        todo = new LinkedList<Intention>();

        if (closestSeen != null) {
            steerTo(closestSeen);
        }
    }
}
