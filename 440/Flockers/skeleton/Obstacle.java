import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class Obstacle extends Agent {

    /**
     * Class information
     */

    /** XML element tag */
    static final String XML_NAME = "rock";

    /** Record that allows XML files to set obstacle defaults */
    static FixedAgentAttributes defaultFixedAgentAttributes =
        new Agent.FixedAgentAttributes(10, 0, 0, 0, 0, 0, 0, HALF_CIRCLE, Color.BLACK, false, false);

    /** Record that allows XML files to set obstacle defaults */
    static DynamicAgentAttributes defaultDynamicAgentAttributes =
        new Agent.DynamicAgentAttributes(250, 250, 0, 0);

    /**
     * Constructor: initialize general agent fields to describe
     * agent that does not move.
     * 
     * @param w world to which agent belongs
     * @param id number to identify agent in its world
     * @param atts SAX attributes corresponding to XML agent spec
     * @param loc file information for error messages
     * @throws SAXException if data is formatted incorrectly
     */
    public Obstacle(World w, int id, Attributes atts, Locator loc)
    throws SAXException {
        myWorld = w;
        this.id = id;
        form = new FixedAgentAttributes(atts, defaultFixedAgentAttributes, loc);
        status = new DynamicAgentAttributes(atts, defaultDynamicAgentAttributes, loc);
    }

    /**
     * Output an XML element describing the current state of
     * this obstacle.
     * 
     * @param out an open file to write to, wrapped in BufferedWriter 
     *            convenience class
     */
    @Override
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
     * Draw the obstacle as a filled square
     * 
     * @param g object to control drawing mechanism
     */
    @Override
    public void draw(Graphics g) {
        g.setColor(form.color);
        myWorld.fillRect((int) Math.round(status.locX - form.size / 2), 
                (int) Math.round(status.locY - form.size / 2), 
                form.size, form.size, g);
    }

    /**
     * describe the obstacle as other agents see it
     * 
     * @return how the obstacle appears: as an obstacle
     */
    @Override
    public Percept.ObjectCategory looksLike() {
        return Percept.ObjectCategory.OBSTACLE;
    }

    /**
     * obstacles get in other agents' way
     * 
     * @param appearance of agent that runs into obstacle
     * @return constant OBSTRUCT indicating collision stops others
     */
    @Override
    public InteractiveBehavior behaviorOnApproach(Percept.ObjectCategory neighbor) {
        return InteractiveBehavior.OBSTRUCT;
    }

    /**
     * Never do anything
     * 
     * @param ps what the light source would "see"
     */
    @Override
    public void deliberate(List<Percept> ps) {
        todo = new LinkedList<Intention>();
    }
}
