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
 * A simple "agent" that doesn't move or behave.
 * The "light source" name reflects the fact that
 * this agent shows up in a percept as being of
 * category "light".
 * 
 * @author Matthew Stone
 * @version 1.0
 */

public class LightSource extends Agent {

    /**
     * Class information
     */

    /** XML element tag */
    static final String XML_NAME = "light";

    /** Record that allows XML files to set light defaults */
    static FixedAgentAttributes defaultFixedAgentAttributes =
        new Agent.FixedAgentAttributes(10, 0, 0, 0, 0, 0, 0, HALF_CIRCLE, Color.YELLOW, false, false);

    /** Record that allows XML files to set light defaults */
    static DynamicAgentAttributes defaultDynamicAgentAttributes =
        new Agent.DynamicAgentAttributes(250, 250, 0, 0);

    /**
     * Constructor: initialize general agent fields to describe
     * agent that does not move.
     * 
     * @param w world to which agent belongs
     * @param id number to identify agent in its world
     * @param x horizontal coordinate where agent appears
     * @param y vertical coordinate where agent appears
     */
    public LightSource(World w, int id, Attributes atts, Locator loc)
    throws SAXException {
        myWorld = w;
        this.id = id;
        form = new FixedAgentAttributes(atts, defaultFixedAgentAttributes, loc);
        status = new DynamicAgentAttributes(atts, defaultDynamicAgentAttributes, loc);
    }

    /**
     * Output an XML element describing the current state of
     * this light source.
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
     * Draw a light source as a filled circle.
     * 
     * @param g object to control drawing mechanism
     */
    @Override
    public void draw(Graphics g) {
        g.setColor(form.color);
        g.fillOval((int) Math.round(status.locX - form.size/2),
                (int) Math.round(status.locY - form.size/2), 
                form.size, form.size);
    }

    /**
     * describe the light source as other agents see it
     * 
     * @return how the light source appears: as a light
     */
    @Override
    public Percept.ObjectCategory looksLike() {
        return Percept.ObjectCategory.LIGHT;
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
