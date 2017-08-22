public class Elevator {
    public static void bounce(int n){
	    
    	if (n < 0 ) {
		 IO.reportBadInput() ;
		 
		}   
    	else if( n > 0 ) { 
		 IO.outputStringAnswer( "floor " + n );
		 bounce(n-1); 
		 IO.outputStringAnswer( "floor " + n );
		}
		
		else if(n==0 ){ 
		IO.outputStringAnswer( "bounce" );
		}
		else{ 
		 
		 
		}
    	    

    }  
  public static void main(String[ ] args) {
      bounce(IO.readInt( )); 

  }
}