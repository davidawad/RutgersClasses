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
 * A chasing agent that targets other boids.
 * Skeleton code
 * 
 * @version 1.0
 */

public class ReactivePredator extends Follower {
    /**
     * Class information
     */

    /** Tag for XML element */
    static final String XML_NAME = "reactive-predator";

    /** Can go forward up to 6 pixels per step */
    static final double PREDATOR_MAX_SPEED = 6;

    /** Record that allows XML files to set reactive predator defaults */
    static FixedAgentAttributes defaultFixedAgentAttributes =
        new Agent.FixedAgentAttributes(
                FOLLOWER_SIZE,
                PREDATOR_MAX_SPEED,
                FOLLOWER_MIN_SPEED,
                FOLLOWER_MAX_ACCEL,
                FOLLOWER_MAX_DECEL,
                HALF_CIRCLE,
                0, HALF_CIRCLE, Color.RED,
                false, false);

    /** Record that allows XML files to set reactive predator defaults */
    static DynamicAgentAttributes defaultDynamicAgentAttributes =
        new Agent.DynamicAgentAttributes(250, 250, 0, 0);

    /**
     * Constructor - initialize general agent fields to describe
     * agent that will follow other peaceful agents and eat them.
     * 
     * @param w world to which agent belongs
     * @param id number to identify agent in its world
     * @param x horizontal coordinate where agent appears
     * @param y vertical coordinate where agent appears
     * @param heading initial orientation of agent
     */
    public ReactivePredator(World w, int id, Attributes atts, Locator loc) 
    throws SAXException {
        super(w, id, atts, loc);
        form.set(atts, defaultFixedAgentAttributes, loc);
        status.set(atts, defaultDynamicAgentAttributes, loc);
    }

    /**
     * Output an XML element describing the current state of
     * this reactive predator.
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

    /** 
     * Predators attack their prey!
     * 
     * @param neighbor A category of agent nearby
     * @return Agent's disposition for what to do with another agent
     */
    @Override
    public InteractiveBehavior behaviorOnApproach(Percept.ObjectCategory neighbor) {
        if (neighbor == Percept.ObjectCategory.BOID)
            return InteractiveBehavior.ATTACK;
        else
            return InteractiveBehavior.COEXIST;
    }

    /**
     * Predators look scary!  While alive,
     * they look like predators.
     * 
     * @return appearance PREDATOR as Percept.ObjectCategory
     *         (or CORPSE if dead)
     */
    @Override
    public Percept.ObjectCategory looksLike() {
        if (isAlive)
            return Percept.ObjectCategory.PREDATOR;
        else
            return Percept.ObjectCategory.CORPSE;
    }

    /********************************************************************
     * 
     * Code block for solution to behavior generation.
     * 
     */

    /**
     * Specialized drawing method in case you want debugging help
     */
    @Override
    public void draw(Graphics g) {
        // TBC: Debug visualization here

        super.draw(g);
    }

    /**
     * Specialized target method: helps define chasing behavior
     */
    @Override
    public boolean isTarget(Percept p) {
        return p.getObjectCategory() == Percept.ObjectCategory.BOID;
    }

    /**
     * Specialized deliberate method: helps define chasing behavior.
     * Default version copied directly from superclass Follower.
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
