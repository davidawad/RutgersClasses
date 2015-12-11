import java.util.ArrayList;
import java.util.Collection;

public class CombinedSearchState implements SearchState {

    final int index;
    final String input;
    final String prev;

    static final char[] VOWELS = {'a', 'e', 'i', 'o', 'u'};

    private CombinedSearchState(int index, String prev, String input) {
    	this.input = input;
    	this.index = index;
        this.prev = prev;
    }

    @Override
    public boolean equals(Object other) {
      if (other == null) {return false;}
      if (other == this) {return true;}
      if (!(other instanceof CombinedSearchState)) {return false;}
    	CombinedSearchState temp = (CombinedSearchState) other;
    	return (temp.index == this.index && temp.prev.equals(this.prev));
      }


    @Override
    public int hashCode(){
	   return index ^ prev.hashCode();
    }

    public static class Builder extends SearchState.Builder {
    		public SearchState makeInitialState(String targetQuantity)
    		    throws IllegalArgumentException {
        		    try {
                        return new CombinedSearchState(0, " ", targetQuantity);
        		    } catch (NumberFormatException ex) {
              			throw new IllegalArgumentException("got incorrect string" + targetQuantity);
        		    }
    		    }
    }

    public boolean isGoal() {
	     return index == input.length();
    }

    public Collection<String> getApplicableActions() {
        ArrayList<String> options = new ArrayList<String>();

        for(int count = 1; (count + index) <= input.length(); count++) {
            for(String sub : ExpansionDictionary.getInstance().lookup( input.substring(index, index + count) )) {
                options.add(sub);
            }
        }
        return options;
    }


    public double getActionCost(String action) {
        SmoothedBigramModel temp = SmoothedBigramModel.getInstance();
        if(index == 0) {
            return temp.cost(LangUtil.SENTENCE_BEGIN, action);
        }
            return temp.cost(prev, action);
    }

    public CombinedSearchState applyAction(String action) {
        int numvows = 0;
        for(int count = 0; count < action.length(); count++){
            char tempChar = action.charAt(count);
            // check if the tempChar is a vowel
            for(char vowel : CombinedSearchState.VOWELS) {
                if(tempChar == vowel) {
                    numvows++;
                    break;
                }
            }
        }
        return new CombinedSearchState(index + action.length() - numvows, action, input);
    }
}
