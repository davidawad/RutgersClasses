public class Elevator {
    public static void bounce(int n){
	    
    	    int f = n ;
    	    if(n >0 ) {
    	    
    	    System.out.println("floor " + n);
    	    bounce(n-1) ;

    	    else if (n <= 0 ){
	    System.out.println("bounce");
	    System.out.println("floor" + n);
	    bounce(n+1);
	    
	    }
    	    

    }
  
  public static void main(String[ ] args) {
      bounce(IO.readInt( )); 

  }
}

