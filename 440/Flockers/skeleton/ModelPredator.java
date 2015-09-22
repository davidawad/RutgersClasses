import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import java.util.LinkedList;

/**
 * A chasing agent that targets other boids.
 * Skeleton code
 * 
 * @version 1.0
 */

public class ModelPredator extends Follower {
    /**
     * Class information
     */

    /** Tag for XML element */
    static final String XML_NAME = "model-predator";

    /** Can go forward up to 6 pixels per step */
    static final double PREDATOR_MAX_SPEED = 6;

    /** Record that allows XML files to set ModelPredator defaults */
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

    /** Record that allows XML files to set ModelPredator defaults */
    static DynamicAgentAttributes defaultDynamicAgentAttributes =
        new Agent.DynamicAgentAttributes(250, 250, 0, 0);

    /** Store the target here for visualization purposes */
    Percept lastTarget;

    /**
     * Constructor - initialize general agent fields to describe
     * agent that will follow other peaceful agents and eat them.
     * 
     * @param w world to which agent belongs
     * @param id number to identify agent in its world
     * @param atts SAX attributes corresponding to XML agent spec
     * @param loc file information for error messages
     * @throws SAXException if data is formatted incorrectly
     */
    public ModelPredator(World w, int id, Attributes atts, Locator loc) 
    throws SAXException {
        super(w, id, atts, loc);
        form.set(atts, defaultFixedAgentAttributes, loc);
        status.set(atts, defaultDynamicAgentAttributes, loc);
        lastTarget = null;
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

    /**
     * YOUR CODE HERE
     */
    protected boolean canIntercept(Percept p) {
        double divergence = Math.sin(p.getOrientation() - p.getAngle());
        double approach = p.getSpeed() / form.maxSpeedForward;
        return (approach * divergence <= 1 && approach * divergence >= -1);
    }

    protected double interceptTurn(Percept p) {
        double divergence = Math.sin(p.getOrientation() - p.getAngle());
        double approach = p.getSpeed() / form.maxSpeedForward;
        return Math.asin(approach * divergence);
    }

    protected double interceptTime(Percept p) {
        double divergence = Math.cos(p.getOrientation() - p.getAngle());
        double approach = Math.cos(interceptTurn(p));
        double time = p.getDistance() / 
        (form.maxSpeedForward * approach - p.getSpeed() * divergence);
        return time;
    }

    @Override
    public void draw(Graphics g) {
        // visualize intersection
        if (lastTarget != null && form.debug) {
            double t = interceptTime(lastTarget);

            double heading = lastTarget.getOrientation() + lastStatus.heading;
            double abs = lastTarget.getAngle() + lastStatus.heading;
            int x = (int) Math.round(lastStatus.locX + lastTarget.getDistance() * Math.cos(abs));
            int y = (int) Math.round(lastStatus.locY + lastTarget.getDistance() * Math.sin(abs));
            int itx = (int) Math.round(x + t * lastTarget.getSpeed() * Math.cos(heading));
            int ity = (int) Math.round(y + t * lastTarget.getSpeed() * Math.sin(heading));
            g.setColor(lastTarget.getColor());
            myWorld.drawLine(x, y, itx, ity, g);

            double plannedAngle = (lastStatus.heading + 
                    interceptTurn(lastTarget) + lastTarget.getAngle());
            int myx = (int) Math.round(lastStatus.locX + 
                    t * form.maxSpeedForward * Math.cos(plannedAngle));
            int myy = (int) Math.round(lastStatus.locY + 
                    t * form.maxSpeedForward * Math.sin(plannedAngle));
            g.setColor(form.color);
            myWorld.fillOval(myx - form.size/2, myy - form.size/2, form.size, form.size, g);
        }

        super.draw(g);
    }

    @Override
    protected boolean isTarget(Percept p) {
        boolean result = 
            (p.getObjectCategory() == Percept.ObjectCategory.BOID &&
                    canIntercept(p));
        return result;
    }

    @Override
    protected double targetCost(Percept p) {
        return interceptTime(p);
    }

    @Override
    public void deliberate(List<Percept> ps) {
        Percept bestTarget = bestTarget(ps);
        todo = new LinkedList<Intention>();

        if (bestTarget != null) {
            lastTarget = bestTarget;

            if (bestTarget.getDistance() > form.size / 2) {
            	double desiredSpeed = form.maxSpeedForward;
            	double desiredAngle = interceptTurn(bestTarget) + bestTarget.getAngle();

            	todo.add(new Intention(Intention.ActionType.TURN, desiredAngle));
            	todo.add(new Intention(Intention.ActionType.CHANGE_SPEED, desiredSpeed - status.forwardV));
            } else {
            	todo.add(new Intention(Intention.ActionType.CHANGE_SPEED, -status.forwardV));
            }
        }
        else {
            lastTarget = null;
        }
    }
}
