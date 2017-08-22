public class TwoSmall{
    public static void main(String[] args) {
		int numberSet = IO.readInt;
		int numberPos = 0 ;
		int num = numberSet.charAt(numberPos) ;
		int min = numberSet.charAt(0) ;
		int min2nd = numberSet.CharAt(1);			
		while(numberPos < numberSet.length){
			if( num <= -1){
			break ;
			}
			if ( num < min ){
			min = num ;
			min2nd = min ;
			}
			else if(num < min2nd){
				min2nd = num ;
			}
		numberPos++ = numberPos ;
		}
		IO.outputIntAnswer(min) ;
		IO.outputIntAnswer(min2nd) ; 
    }
}