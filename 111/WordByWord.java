// methods to deal with strings of words.
// all the methods assume that their argument is a string of letters 
// and spaces only, with exactly one space between adjacent words and
// no spaces before the first word or after the last, and the same
// should be true of the strings these methods return.
public class WordByWord{
    /**
     * firstWord returns the first word in the String line. If line 
     * is the empty string return the empty string.
     */
    public static String firstWord(String line){
	int lineSpace = line.indexOf(" ") ;
	String empty = "";
	String Word1 = line.substring(0, lineSpace);
	if (Word1.equals(empty)) {
		return empty;
	} 
	else {
		return Word1;  
	}
	}
    /**
     * returns all of line except the first word and the following
     * space.  If line is empty or has only one word, return the empty
    f * string, E.g. restOfWords("now is the") should return "is the".
     */
    public static String restOfWords(String line){
	String afterWord1 = line.substring(lineSpace + 1 , ) ; 
	if (line.equals(empty)) {
		return empty ;
	}
	else{
		return afterWord1 ; 
    }
	}
    /**
     * Assumes word is a single word (no spaces).  Returns the reverse
     * of word, eg revWord("cow") returns "woc"
     */
    public static String revWord(String word){
	String startString = word ;
    String resultString = "";
    int length = startString.length();
    int counter = length;
    for (int i = 0 ; i <length; i++){
            resultString += startString.charAt(counter-1) ;
            counter--;
    }
	return resultString;  
    }
    /**
     * Reverses a line word-by-word, eg kcabSdrow("now is the time")
     * returns "won si eht emit"
     */
    public static String kcabSdrow(String line){
	String startString = line ;
    String resultString = "";
    int length = startString.length();
    int counter = length;
    for (int i = 0 ; i <length; i++){
            resultString += startString.charAt(counter-1) ;
            counter--;
    }
	return resultString;
    }
    /**
     * Reverses the words in a line, eg wordsBack("now is the time")
     * returns "time the is now"
     */
    public static String wordsBack(String line){
	StringBuilder returnValue = new StringBuilder();
        int insertIndex = 0;
        for(int i = 0;i < input.length();i++ ) {
            if(line.charAt(i)!=' ') {
                returnValue.insert(insertIndex, currentChar);
            } else {
                insertIndex = i+1;
                returnValue.append(currentChar);
            }

        }

        return returnValue.toString();
    }
    /**
     * old and new are single words.  Replace all copies of oldWord in line
     * with newWord.  E.g., replaceWords("cat", "dog", "a cat scat cat bye Cat")
     * should return "a dog scat dog bye Cat".  Note that only oldWord as a word
     * by itself is replaced, e.
	 
	 g., not the cat in scat. Note that case matters,
     * e.g., cat does not match Cat.
     */
    public static String replaceWords(String oldWord, String newWord, String line){
	return ""; // replace this line 
    }
    
    /**
     * This is not graded - put any test code here that you want
     */
    public static void main(String [ ] args){
        IO.outputStringAnswer(kcabSdrow("abc def"));
        IO.outputStringAnswer(wordsBack("abc def"));
    }
}
            

