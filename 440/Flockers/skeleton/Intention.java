/**
 * Class for representing an agent's intentions.
 * So far allows for an agent's intention to
 * change speed or change direction.
 * 
 * @author Matthew Stone
 * @version 1.0
 *
 */
public class Intention {

    /**
     * Class information
     */

    /**
     * Enumerates the different kinds of things
     * that an agent can intend to do.
     * "description" allows intentions to be
     * pretty-printed.
     */
    public static enum ActionType {
        TURN ("turn"),
        CHANGE_SPEED ("change in speed");
        public final String description;
        private ActionType(String d) {
            description = d;
        }
    }

    /**
     * Instance members
     */
    
    /** what kind of thing does this intention describe */
    private ActionType type;
    /** how much: change in speed or change in angle */
    private double param;

    /**
     * Constructor
     * 
     * @param type what to do 
     * @param param how much to do
     */
    public Intention(ActionType type, double param) {
        this.type = type;
        this.param = param;
    }

    /**
     * Accessor
     * @return type of action inteded
     */
    public ActionType getType() {
        return type;
    }

    /**
     * Accessor
     * @return action argument: change in speed or change in angle
     */
    public double getParam() {
        return param;
    }
}
