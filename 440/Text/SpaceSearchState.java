import java.util.ArrayList;
import java.util.Collection;

public class SpaceSearchState implements SearchState {


    final int       index;
    final String    input;

    /**
     * Constructor
     * @param index     Current index at this state
     * @param input     Passing constant input through
     */
    private SpaceSearchState(int index, String input) {
    	this.input = input;
    	this.index = index;
    }

    @Override
    public boolean equals(Object other) {
    	if (other == null) return false;
    	if (other == this) return true;
    	if (!(other instanceof SpaceSearchState)) return false;

    	SpaceSearchState otherState = (SpaceSearchState) other;
    	return (otherState.index == this.index &&
    		otherState.input.equals(this.input));
    }


    @Override
    public int hashCode() {
	   return index;
    }

    public static class Builder extends SearchState.Builder {
	/**
	 * @param targetQuantity specification of the problem instance to solve
	 * @return initial search state corresponding to this problem
	 */
		public SearchState makeInitialState(String targetQuantity)
		    throws IllegalArgumentException {
		    try {
		       SpaceSearchState temp = new SpaceSearchState(0, targetQuantity);
               return temp;
		    }
            catch (NumberFormatException nfe) {
			    throw new IllegalArgumentException("Need unsegmented string for initial test state, got " +
				    targetQuantity);
		    }
		}
    }


    public boolean isGoal() {
	   return index == input.length();
    }


    public Collection<String> getApplicableActions() {
    	ArrayList<String> options = new ArrayList<String>();

        int count = 1;
        while(count + index <= input.length() ){
            options.add(input.substring(index, index+count));
            count ++;
        }
        return options;
    }


    public double getActionCost(String action) {
        UnigramModel temp = UnigramModel.getInstance();
        return temp.cost(action);
    }

    public SpaceSearchState applyAction(String action) {
        SpaceSearchState temp = new SpaceSearchState(index+action.length(), input);
        return temp;
    }

}
