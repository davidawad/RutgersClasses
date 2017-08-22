public class ReverseWords {
	public static void main(String[] args){
    String startString = IO.readStr ;
    String resultString = "";
    int length = startString.length();
    int counter = length;

    for (int i = 0 ; i <length; i++){
            resultString += startString.charAt(counter-1) ;
            counter--;
    }
	IO.outputStringAnswer(result);
 }
   
}
