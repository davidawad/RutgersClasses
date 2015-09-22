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
 * A basic agent that proceeds forward in an initial direction.
 * Inspired by Braitenberg's vehicle 1.
 * 
 * @author Matthew Stone
 * @version 1.0
 */

public class Runner extends Agent {

    /**
     * Class information
     */

    /** Tag for XML element */
    static final String XML_NAME = "runner";

    /** Size in display */
    static final int RUNNER_SIZE = 15;
    /** Can go forward up to 4 pixels per step */
    static final double RUNNER_MAX_SPEED = 4;
    /** Cannot go backward */
    static final double RUNNER_MIN_SPEED = 0;
    /** No constraint on acceleration */
    static final double RUNNER_MAX_ACCEL = 0;
    /** No constraint on braking */
    static final double RUNNER_MAX_DECEL = 0;
    /** Relatively unable to make sharp turns */
    static final double RUNNER_MAX_TURN = 0;

    /* Record that allows XML files to set runner defaults */
    static FixedAgentAttributes defaultFixedAgentAttributes =
        new Agent.FixedAgentAttributes(
                RUNNER_SIZE,
                RUNNER_MAX_SPEED,
                RUNNER_MIN_SPEED,
                RUNNER_MAX_ACCEL,
                RUNNER_MAX_DECEL,
                RUNNER_MAX_TURN,
                0,
                HALF_CIRCLE, 
                Color.BLUE,
                false, false);

    /* Record that allows XML files to set runner defaults */
    static DynamicAgentAttributes defaultDynamicAgentAttributes =
        new Agent.DynamicAgentAttributes(250, 250, 0, RUNNER_MAX_SPEED);

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
    public Runner(World w, int id, Attributes atts, Locator loc)
    throws SAXException {
        myWorld = w;
        this.id = id;
        form = new FixedAgentAttributes(atts, defaultFixedAgentAttributes, loc);
        status = new DynamicAgentAttributes(atts, defaultDynamicAgentAttributes, loc);
    }

    /**
     * Output an XML element describing the current state of
     * this runner.
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
     * Draw a runner as a triangle pointing in the direction
     * of the agent's heading.
     * @param g object to control drawing mechanism 
     * @see Agent#draw(java.awt.Graphics)
     */
    @Override
    public void draw(Graphics g) {
        int[] xpoints = new int[4];
        int[] ypoints = new int[4];

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
        xpoints[3] = x0 + (int) Math.round(s * Math.cos(status.heading));

        ypoints[0] = y0;
        ypoints[1] = y0 + baseOffsetY;
        ypoints[2] = y0 + baseOffsetY / 2 + (int) Math.round(s * Math.sin(status.heading));
        ypoints[3] = y0 + (int) Math.round(s * Math.sin(status.heading));

        g.setColor(form.color);
        myWorld.fillPolygon(xpoints, ypoints, 3, g);
    }


    /**
     * Do nothing
     * 
     * @param ps A description of everything the agent can see
     */
    @Override
    public void deliberate(List<Percept> ps) {      
        todo = new LinkedList<Intention>();
    }

    /**
     * A runner is one of many agents that 
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

}
