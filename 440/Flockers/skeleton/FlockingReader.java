import java.awt.Frame;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class to read world specifications as XML documents
 * using SAX interface.
 * 
 * Fleshes out a few of the SAX callback methods 
 * to create and update objects and keep the visual
 * display current.
 * 
 * @author Matthew Stone
 * @version 1.0
 */
public class FlockingReader extends DefaultHandler {

    /* Allows us to allocate unique identifiers to new objects */
    static int nextId = 1;
    
    /* Links back to the window where events we read should be displayed */
    private Frame frame;
    
    /* Used to construct error messages based on file position */
    private Locator locator;
    
    /* The overall world being specified in the current file */
    private World world;

    /* Used to tell if we are handling defaults */
    private boolean inDefaults = false;
    
    /* XML element beginning defaults */
    static final String DEFAULT_ELEMENT = "defaults";
    
    /**
     * Returns the world being specified by the file as it is parsed 
     * @return a world
     * @see World
     */
    public World getWorld() {
        return world;
    }

    /**
     *  called when XML parser starts reading and gives us tabs 
     *  on the dynamic Locator object parameter
     *  
     *  @param locator updated by XML parser to hold current file location
     */
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    /**
     * Pretty print the current position in the source file for
     * error messages.
     * 
     * @return String specification of current position in locator
     */
    static public String locationMsg(Locator locator) {
        String location = "";
        if (locator != null) {
            String id = locator.getSystemId();
            if (id != null)
                location = id; // XML-document name;
            location += " line " + locator.getLineNumber();
            location += ", column " + locator.getColumnNumber();
            location += ": ";
        }
        return location;
    }

    /**
     * Helper function for extracting information from the XML attributes
     * of an XML element.
     * 
     * @param atts the XML attributes found on this element by the parser
     * @param param the specific attribute whose value we want
     * @param defaultValue used when element did not specify this value
     * @return value found if present, otherwise default
     * 
     */
    static public String getStringParam(Attributes atts, String param, String defaultValue, Locator locator) 
    {
        String result = defaultValue;
        String value = atts.getValue(World.XMLNS, param);
        if (value == null) {
            value = atts.getValue("", param);
        }
        if (value != null) {
            result = value;
        }
        return result;
    }

    /**
     * Helper function for extracting information from the XML attributes
     * of an XML element
     * 
     * @param atts the XML attributes found on this element by the parser
     * @param param the specific attribute whose value we want
     * @param defaultValue used when element did not specify this value
     * @return value found if present, otherwise default
     * @throws SAXException when value should be but is not an integer
     */
    static public int getIntParam(Attributes atts, String param, int defaultValue, Locator locator) 
    throws SAXException {
        int result = defaultValue;
        String value = atts.getValue(World.XMLNS, param);
        if (value == null) {
            value = atts.getValue("", param);
        }
        if (value != null) {
            try {
                result = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // compose a text with location of error-case:
                throw new SAXException(locationMsg(locator) + "Bad integer format of " + value + " for " + param);
            } 
        }
        return result;
    }

    /**
     * Helper function for extracting information from the XML attributes
     * of an XML element
     * 
     * @param atts the XML attributes found on this element by the parser
     * @param param the specific attribute whose value we want
     * @param defaultValue used when element did not specify this value
     * @return value found if present, otherwise default
     * @throws SAXException when value should be but is not a double
     */
    static public double getDoubleParam(Attributes atts, String param, double defaultValue, Locator locator) 
    throws SAXException {
        double result = defaultValue;
        String value = atts.getValue(World.XMLNS, param);
        if (value == null) {
            value = atts.getValue("", param);
        }
        if (value != null) {
            try {
                result = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                // compose a text with location of error-case:
                throw new SAXException(locationMsg(locator) + "Bad double format of " + value + " for " + param);
            } 
        }
        return result;
    }

    /**
     * Helper function for extracting information from the XML attributes
     * of an XML element
     * 
     * @param atts the XML attributes found on this element by the parser
     * @param param the specific attribute whose value we want
     * @param defaultValue used when element did not specify this value
     * @return value found if present, otherwise default
     */
    static public boolean getBoolParam(Attributes atts, String param, boolean defaultValue, Locator locator) 
    throws SAXException {
        boolean result = defaultValue;
        String value = atts.getValue(World.XMLNS, param);
        if (value == null) {
            value = atts.getValue("", param);
        }
        if (value != null) {
            result = Boolean.parseBoolean(value);
        }
        return result;
    }

    /**
     * Constructor, keeps the passed frame to build UI for world
     * 
     * @param f window where specified world appears
     */
    public FlockingReader(Frame f) {
        super();
        frame = f;
    }


    ////////////////////////////////////////////////////////////////////
    // Event handlers.
    ////////////////////////////////////////////////////////////////////

    
    public void startDocument ()
    {
    }


    public void endDocument ()
    {
    }

    /**
     * SAX callback reached at the start of each element in the document
     * This is the point where we create the objects described in the
     * world specification, and process commands to update the status of
     * existing objects.
     * 
     * This is the place to add code if you have new kinds of agents
     * that you want to create with suitable commands in the XML file.
     * Check for an element corresponding to your new agent, 
     * read in values for the parameters for the agent that are
     * stored in the attributes, then create a new agent and add
     * it to the world (or update the existing one with the specified id).
     * 
     * @param uri local namespace of elements
     * @param name name of this element
     * @param qName qualified name of this element
     * @param atts list of attribute--value data in element declaration
     */
    public void startElement (String uri, String name,
                  String qName, Attributes atts) 
    throws SAXException
    {
        if (!World.XMLNS.equals(uri) && !"".equals (uri)) {
            System.err.println(locationMsg(locator) + "Namespace: {" + uri + "}" + " not recognized");
            return;
        }
        
        if (DEFAULT_ELEMENT.equals(name)) {
            inDefaults = true;
            return;
        }   
        
        if (World.STATE_NAME.equals(name)) {
            int step = getIntParam(atts, World.STEP_NAME, world.getStepCount(), locator);
            world.setStepCount(step);
            return;
        }
        
        if (World.WAIT_NAME.equals(name)) {
            int duration = getIntParam(atts, World.WAIT_INTERVAL, World.DEFAULT_WAIT, locator);
            try { 
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                ;
            }
            return;
        }
        
        if (World.XML_NAME.equals(name)) {
            int width = getIntParam(atts, World.WIDTH_PARAM, World.DEFAULT_WIDTH, locator);
            int height = getIntParam(atts, World.HEIGHT_PARAM, World.DEFAULT_HEIGHT, locator);
            int delay = getIntParam(atts, World.WAIT_INTERVAL, World.DEFAULT_WAIT, locator);
            String logfile = getStringParam(atts, World.LOGFILE_PARAM, null, locator);
            boolean runnable = getBoolParam(atts, World.RUNNABLE_PARAM, true, locator);
            boolean debug = getBoolParam(atts, World.DEBUG_PARAM, false, locator);
            world = new World(width, height, logfile, runnable, delay, debug);
            frame.setSize(width,height);
            frame.add(world);
            frame.pack();
            return;
        }
        
        if (world == null) {
            System.err.println(locationMsg(locator) + name + " element without a world");
            return;
        } 

        int id = getIntParam(atts, Agent.ID_PARAM, nextId++, locator);
        Agent a = world.getAgent(id);
        if (a != null) {
            if (Agent.UPDATE.equals(name))
                a.update(atts, locator);    
            else if (World.DIE_NAME.equals(name))
                world.removeAgent(a);
            return;
        }   
        
        if (LightSource.XML_NAME.equals(name)) {
            if (inDefaults) {
                LightSource.defaultFixedAgentAttributes.update(atts, locator);
                LightSource.defaultDynamicAgentAttributes.update(atts, locator);
            } else {
                LightSource l = new LightSource(world, id, atts, locator);
                world.addAgent(l);
            }
        } else if (Obstacle.XML_NAME.equals(name)) {
            if (inDefaults) {
                Obstacle.defaultFixedAgentAttributes.update(atts, locator);
                Obstacle.defaultDynamicAgentAttributes.update(atts, locator);
            } else {
                Obstacle o = new Obstacle(world, id, atts, locator);
                world.addAgent(o);
            }
        } else if (Runner.XML_NAME.equals(name)) {
            if (inDefaults) {
                Runner.defaultDynamicAgentAttributes.update(atts, locator);
                Runner.defaultFixedAgentAttributes.update(atts, locator);
            } else {
                Runner r = new Runner(world, id, atts, locator);
                world.addAgent(r);
            }
        } else if (Follower.XML_NAME.equals(name)) {
            if (inDefaults) {
                Follower.defaultDynamicAgentAttributes.update(atts, locator);
                Follower.defaultFixedAgentAttributes.update(atts, locator);
            } else {
                Follower f = new Follower(world, id, atts, locator);
                world.addAgent(f);
            }
        } else if (SmartFollower.XML_NAME.equals(name)) {
            if (inDefaults) {
                SmartFollower.defaultDynamicAgentAttributes.update(atts, locator);
                SmartFollower.defaultFixedAgentAttributes.update(atts, locator);
            } else {
                SmartFollower f = new SmartFollower(world, id, atts, locator);
                world.addAgent(f);
            }
        } else if (Flocker.XML_NAME.equals(name)) {
            if (inDefaults) {
                Flocker.defaultDynamicAgentAttributes.update(atts, locator);
                Flocker.defaultFixedAgentAttributes.update(atts,locator);
                Flocker.defaultFlockerAttributes.update(atts, locator);
            } else {
                Flocker f = new Flocker(world, id, atts, locator);
                world.addAgent(f);
            }
        } else if (ReactivePredator.XML_NAME.equals(name)) {
            if (inDefaults) {
                ReactivePredator.defaultDynamicAgentAttributes.update(atts, locator);
                ReactivePredator.defaultFixedAgentAttributes.update(atts, locator);
            } else {
                ReactivePredator p = new ReactivePredator(world, id, atts, locator);
                world.addAgent(p);
            }
        } else if (ModelPredator.XML_NAME.equals(name)) {
            if (inDefaults) {
                ModelPredator.defaultDynamicAgentAttributes.update(atts, locator);
                ModelPredator.defaultFixedAgentAttributes.update(atts, locator);
            } else {
                ModelPredator p = new ModelPredator(world, id, atts, locator);
                world.addAgent(p);
            }
        } else {
            System.err.println(locationMsg(locator) + "Unknown agent type " + name);
        }
    }

    /**
     * This method is called by the XML document parser when 
     * it reaches the end of an element specification.
     * The only time we need this is in the case of state 
     * descriptions.  The end of a state description marks
     * the complete specification of the update of all the
     * agents in the world after another time step of action.
     * At this point we should make sure that the simulation
     * is visible and redraw the world to reflect its updated state.
     * 
     * @param uri local namespace of elements
     * @param name name of this element
     * @param qName qualified name of this element
     */
    public void endElement (String uri, String name, String qName)
    {
        if ((World.XMLNS.equals(uri) || "".equals (uri))) {
            if (World.STATE_NAME.equals(name)) {
                frame.setVisible(true);
                if (world != null) {
                    world.repaint();
                }
            }
            else if (DEFAULT_ELEMENT.equals(name)) {
                inDefaults = false;
            }
        }
    }


    public void characters (char ch[], int start, int length)
    {
    }

}
