import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class Solution {

    public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
	Scanner sc = new Scanner(System.in) ;
    int n = sc.nextInt();
	float ans = 0 ;
    for(int i=0;i<n;i++){
    double temp= Math.pow(-1,i) ;
    double temp2= 2*i +1; 
	ans+= (float)(temp/ temp2);
 	System.out.println(ans) ;
    }
    
    }
}
