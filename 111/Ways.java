public class Ways{
  public static int ways(int members, int committee){
    int m = members ;
	int c = committee ;
	if(m == 0 || c == 0 || c > m ) { 
	IO.reportBadInput() ;
	return -1 ;
	}
	else if ( c < 0 || m < 0){ 
	IO.reportBadInput() ;
	return -1 ;
	}
	else if (c==m) {
	return 1 ; 
	}
	else if (c==1) {
	return m ;
	}
	else { 
	int w = ways(m-1,c-1) + ways(m-1,c ); 
	return w ;
	}
}
  public static void main(String[ ] args) {
      System.out.println("members? ");
      int members = IO.readInt( );
      System.out.println("committee? ");
      int committee = IO.readInt( );
      IO.outputIntAnswer(ways(members, committee));   
 
  }
}
