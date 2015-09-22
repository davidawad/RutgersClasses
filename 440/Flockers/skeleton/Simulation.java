import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Simulation manages a window, file I/O, and simulation stepping
 * for a test scenario describing agents acting in an environment.
 * 
 * @author Matthew Stone
 * @version 1.0
 */
public class Simulation extends Frame {

    /**
     * Java AWT components are required to be serializable,
     * and therefore require a long int id indicating what 
     * version of the code the serialization comes from.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Holds the world that this object will be simulating.
     * @see World
     */
    private World w = null;
    
    /**
     * Constructor: creates the frame and then adds
     * cleanup code hiding the window, terminating
     * the simulation, releasing graphics resources,
     * and quitting.
     */
    public  Simulation() {
        super("Agents acting in an environment"); 

        // exit cleanly 
        addWindowListener(new WindowAdapter() { 
            public void windowClosing(WindowEvent e) { 
                setVisible(false); 
                if (w != null && w.isRunnable()) {
                    w.finishLogging();
                }
                w = null;
                dispose(); 
                System.exit(0); 
            } 
        }); 
    }
    
    /**
     * We create a Cleanup object as a handler for
     * shutdown events (triggered for example by
     * hitting Control-C at the command line).
     * This is called a shutdown hook.
     * The code here makes sure that any log file
     * being created remains valid XML.
     */
    class Cleanup extends Thread {
        public void run() {
            if (w != null && w.isRunnable()) {
                w.finishLogging();
            }
        }       
    }
    
    /**
     * Command-line interface to simulation class.
     * 
     * @param args array of strings specified on the 
     *             command line; should specify a
     *             single XML specification of a world
     */
    public static void main(String[] args) {
        Simulation s = new Simulation();

        // Clean up if control-C is pressed
        Runtime.getRuntime().addShutdownHook(s.new Cleanup());
        
        if (args.length != 1) {
            System.err.println("Usage error: run as <program> <specfile> for a single XML world spec.");
        }
        try {
            // Set up SAX reader, which processes XML objects as file is read.
            XMLReader xr = XMLReaderFactory.createXMLReader();      
            FlockingReader handler = new FlockingReader(s);
            xr.setContentHandler(handler);
            xr.setErrorHandler(handler);
            
            // Parse the XML
            FileReader r = new FileReader(args[0]);
            xr.parse(new InputSource(r));

            // Show the simulation on screen, if you haven't already
            s.w = handler.getWorld();
            if (s.w == null)
            	s.pack();
            s.setVisible(true);

            // Get ready to record activities
            if (s.w != null) 
                s.w.startLogging();

            // Run any simulation indefinitely
            while (s.w != null && s.w.isRunnable()) {
                s.w.stepWorld();
                try {
                    Thread.sleep(s.w.getDelay());
                }
                catch (InterruptedException e) {
                }
            }
            
            // Clean up if the world spec did not want a simulation
            s.processWindowEvent(new WindowEvent(s, WindowEvent.WINDOW_CLOSING));
            
        } catch (SAXException e) {
            System.err.println(e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

}
