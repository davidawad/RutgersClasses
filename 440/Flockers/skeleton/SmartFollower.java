import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * A more refined version of the light follower agent
 * that is capable of backing up and reorienting when
 * it finds itself facing the wrong way too close to
 * the light it is chasing.  
 * 
 * @author Matthew Stone
 * @version 1.0
 *
 */
public class SmartFollower extends Follower {
    /**
     * Class information
     */

    /** XML tag for reading and writing elements describing this agent */
    static final String XML_NAME = "shadower";

    /** Can go forward up to four pixels per step */
    static final double SMART_FOLLOWER_MAX_SPEED = 4;
    /** Can also go backward up to four pixels per step */
    static final double SMART_FOLLOWER_MIN_SPEED = 4;
    /** Relatively unable to make sharp turns */
    static final double SMART_FOLLOWER_MAX_TURN = 6 * DEGREES_TO_RADIANS;

    /** Record that allows XML files to set smart follower defaults */
    static FixedAgentAttributes defaultFixedAgentAttributes =
        new Agent.FixedAgentAttributes(
                FOLLOWER_SIZE,
                SMART_FOLLOWER_MAX_SPEED,
                SMART_FOLLOWER_MIN_SPEED,
                FOLLOWER_MAX_ACCEL,
                FOLLOWER_MAX_DECEL,
                SMART_FOLLOWER_MAX_TURN,
                0, HALF_CIRCLE, new Color(0, 40, 80),
                false, false);
    
    /* Record that allows XML files to set smart follower defaults */
    static DynamicAgentAttributes defaultDynamicAgentAttributes =
        new Agent.DynamicAgentAttributes(250, 250, 0, 0);

    /**
     * Constructor is basically the same as a light follower except for
     * the ability to back up
     * 
     * @param w world to which agent belongs
     * @param id number to identify agent in its world
     * @param atts SAX attributes corresponding to XML agent spec
     * @param loc file information for error messages
     * @throws SAXException if data is formatted incorrectly
     */
    
    public SmartFollower(World w, int id, Attributes atts, Locator loc)
    throws SAXException {
        super(w, id, atts, loc);
        form.set(atts, defaultFixedAgentAttributes, loc);
        status.set(atts, defaultDynamicAgentAttributes, loc);
    }

    /**
     * Output an XML element describing the current state of
     * the smart light follower.
     * 
     * @param out an open file to write to, wrapped in BufferedWriter 
     *            convenience class
     */
    public void log(BufferedWriter out) 
    throws IOException {
        out.write("   <" + XML_NAME + " " + ID_PARAM + "=\"" + Integer.toString(id) +
           "\n     ");
        form.log(out);
        out.write("    ");
        status.log(out);
        out.write("    />\n");
    }
    
    /**
     * Tests whether the object represented by percept p
     * is far enough away or directly enough ahead that
     * the agent can reach it by forward-directed smooth
     * pursuit.  Inspired by theory of affordances.
     * 
     * @param p percept representing target the agent is following
     * @return true if object affords pursuit.
     */
    private boolean canPursue(Percept p) {
        // get coordinates of target in local frame
        double dx = p.getDistance() * Math.cos(p.getAngle());
        double dy = p.getDistance() * Math.sin(p.getAngle());

        // relate coordinates to circle centered at (0,r) or (0,-r)
        // which describes path agent will take when it pursues target
        double r = turningRadius();
        if (dy < 0) {
            dy += r;
        } else {
            dy -= r;            
        }

        // if distance is bigger than r, then agent will reach target
        // otherwise it will not: target will remain inside circle
        // that describes agent's path
        // the 0.5 is so we don't worry about sub-pixel error
        boolean result = Math.sqrt(dx * dx + dy * dy) >= r - 0.5;
        return result;
    }

    /**
     * Update agent's todo list.  Here: find the closest light source,
     * and react to it in one of three ways:
     *  - if agent is close to target, just finalize the approach
     *  - if agent can reach the target by smooth pursuit,
     *    turn towards the target and proceed at full speed
     *  - if the agent cannot reach the target by smooth pursuit,
     *    back up and turn so heading gets closer towards target
     * 
     * @param ps A description of everything the agent can see
     */
    @Override
    public void deliberate(List<Percept> ps) {
        Percept closestSeen = bestTarget(ps);
        
        todo = new LinkedList<Intention>();

        if (closestSeen != null) {
            
            double desiredForwardV;
            double desiredAngle;
            
            if (closestSeen.getDistance() < form.maxSpeedForward) {
                //snuggle
                desiredForwardV = closestSeen.getDistance();    
                desiredAngle = 0;
            } 
            else if (canPursue(closestSeen)) {
                // go as fast as you can without overshooting
                desiredForwardV = form.maxSpeedForward;
                
                // turn as much as you can without overshooting
                desiredAngle = closestSeen.getAngle();
            }
            else {
                // back up and swing around
                desiredForwardV = -form.maxSpeedBackward;
                if (closestSeen.getAngle() > 0) {
                    desiredAngle = form.maxTurn;
                } else {
                    desiredAngle = -form.maxTurn;
                }
                    
            }
            todo.add(new Intention(Intention.ActionType.TURN, desiredAngle));
            todo.add(new Intention(Intention.ActionType.CHANGE_SPEED, desiredForwardV - status.forwardV));
        }
    }
}
