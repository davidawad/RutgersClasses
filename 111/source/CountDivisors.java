/**
 * This program reads a positive integer from the user.
 * It counts how many divisors that number has, and
 * then it prints the result.
 */
   
public class CountDivisors {
   
   public static void main(String[] args) {
      
      int N;  // A positive integer entered by the user.
              // Divisors of this number will be counted.
              
      int testDivisor;  // A number between 1 and N that is a
                        // possible divisor of N.
      
      int divisorCount;  // Number of divisors of N that have been found.
      
      int numberTested;  // Used to count how many possible divisors
                         // of N have been tested.  When the number
                         // reaches 1000000, a period is output and
                         // the value of numberTested is reset to zero.
                         
      /* Get a positive integer from the user. */
   
      while (true) {
         TextIO.put("Enter a positive integer: ");
         N = TextIO.getlnInt();
         if (N > 0)
            break;
         TextIO.putln("That number is not positive.  Please try again.");
      }
      
      /* Count the divisors, printing a "." after every 1000000 tests. */
    
      divisorCount = 0;
      numberTested = 0;
      
      for (testDivisor = 1; testDivisor <= N; testDivisor++) {
         if ( N % testDivisor == 0 )
            divisorCount++;
         numberTested++;
         if (numberTested == 1000000) {
            TextIO.put('.');
            numberTested = 0;
         }
      }
      
      /* Display the result. */
      
      TextIO.putln();
      TextIO.putln("The number of divisors of " + N
                          + " is " + divisorCount);
      
   } // end main()
   
} // end class CountDivisors
