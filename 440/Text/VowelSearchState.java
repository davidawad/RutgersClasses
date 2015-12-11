import java.util.Set;
import java.util.ArrayList;
import java.util.Collection;


public class VowelSearchState implements SearchState {

    final int       index;
    final String    input;
    final String     prev;

    private VowelSearchState(int index, String input, String prev) {
    	this.input = input;
    	this.index = index;
        this.prev = prev;
    }

    @Override
    public boolean equals(Object other) {
    	if (other == null) return false;
    	if (other == this) return true;
    	if (!(other instanceof VowelSearchState)) return false;

    	VowelSearchState temp = (VowelSearchState) other;
    	return (temp.index == this.index &&
    		temp.input.equals(this.input) &&
            temp.prev.equals(this.prev));
    }


    @Override
    public int hashCode() {
	   return prev.hashCode() ^ index ;
    }

    public static class Builder extends SearchState.Builder {

		public SearchState makeInitialState(String targetQuantity)
		    throws IllegalArgumentException {
    		    try {
                   // create new vowel search state with previous added in
    		       VowelSearchState temp = new VowelSearchState(0, targetQuantity, " ");
                   return temp;
    		    }
                catch (NumberFormatException nfe) {
    			    throw new IllegalArgumentException("Got incorrect string " +
    				    targetQuantity);
    		    }
		    }
    }


    public boolean isGoal() {
	   return index == input.length();
    }


    public Collection<String> getApplicableActions() {
        ArrayList<String> options = new ArrayList<String>();

        // iterate through the dictionary of possibilities
        for (String element : ExpansionDictionary.getInstance().lookup(input)){
            options.add(element);
        }
        return options;
    }


    public double getActionCost(String action) {
        SmoothedBigramModel temp = SmoothedBigramModel.getInstance();
        if(index == 0){

            return temp.cost(LangUtil.SENTENCE_BEGIN, action);
        }
        return temp.cost(action, prev);
    }

    public VowelSearchState applyAction(String action) {
        return new VowelSearchState(index + 1, prev, action);

    }

}
