import java.util.ArrayList;

/**
 * This program counst the number of prime integers between 3000001 and 6000000.
 * The work is divided among one to five threads.  The number of threads is
 * chosen by the user.  This version of the program uses the producer/consumer
 * pattern to get the results from the threads back to the main thread where
 * they are added up to get the overall total.
 */
public class ThreadTest3 {
   
   /**
    * The starting point for the range of integers that are tested for primality.
    * The range is from (start+1) to (2*start).  Note the value of start is chosen
    * to be divisible by 2, 3, 4, and 5 to make it easy to divide up the range
    * among the threads.
    */
   private static final int start = 3000000;
   
   /**
    * An object that is used to transfer restuls from the threads that do
    * the counting to the main thread that adds up the results.  An integer
    * x is added to the results by calling results.produce(x).  A result
    * is retrieved by calling results.consume().  If results.consume() is
    * called when no results are available, it will wait for a result to
    * become available 
    */
   private static ProducerConsumer results = new ProducerConsumer();

   
   /**
    * An object of type ProducerConsumer represents a list of results
    * that are available for processing.  Results are added to the list
    * by calling the produce method and are remove by calling consume.
    * If no result is available when consume is called, the method will
    * not return until a result becomes available.  This is meant to
    * be used in a situation where results are produced by one thread
    * and are consumed by another.
    */
   private static class ProducerConsumer {
      private ArrayList<Integer> items = new ArrayList<Integer>();
      public void produce(int n) {
         synchronized(items) {
            items.add(n);    // Add n to the list of results.
            items.notify();  // Notify any thread waiting in consume() method.
         }
      }
      public int consume() {
         int n;
         synchronized(items) {
            // If no results are available, wait for notification from produce().
            while (items.size() == 0) {
               try {
                  items.wait();
               }
               catch (InterruptedException e) {
               }
            }
            // At this point, we know that at least one result is available.
            n = items.remove(0);
         }
         return n;
      }
   }
   
   /**
    * A Thread beloning to this class will count the nubmer of primes in a specified
    * range of integers.  The range is from min to max, inclusive, where min and max
    * are given as parameters to the constructor.  After counting, the thread
    * outputs a message about the number of primes that it has found, and it
    * adds its count to the results by calling results.produce().
    */
   private static class CountPrimesThread extends Thread {
      int count = 0;
      int min, max;
      public CountPrimesThread(int min, int max) {
         this.min = min;
         this.max = max;
      }
      public void run() {
         count = countPrimes(min,max);
         System.out.println("There are " + count + " primes between " + min + " and " + max);
         results.produce(count);
      }
   }
   
   /**
    * Counts the primes in the range from (start+1) to (2*start), using a specified number
    * of threads.  The total elapsed time is printed.
    */
   private static void countPrimesWithThreads(int numberOfThreads) {
      int increment = start/numberOfThreads;
      System.out.println("\nCounting primes between " + (start+1) + " and " 
            + (2*start) + " using " + numberOfThreads + " threads...\n");
      long startTime = System.currentTimeMillis();
      CountPrimesThread[] worker = new CountPrimesThread[numberOfThreads];
      for (int i = 0; i < numberOfThreads; i++)
         worker[i] = new CountPrimesThread(  start+i*increment+1, start+(i+1)*increment );
      for (int i = 0; i < numberOfThreads; i++)
         worker[i].start();
      int total = 0;
      for (int i = 0; i < numberOfThreads; i++) {
            // Add the counts from all the threads.  We know how many threads there
            // are, so we know how many results to expect.  The program will end
            // after we receive that many results.
         total = total + results.consume();
      }
      long elapsedTime = System.currentTimeMillis() - startTime;
      System.out.println("\nFound " + total + " primes.");
      System.out.println("\nTotal elapsed time:  " + (elapsedTime/1000.0) + " seconds.\n");
   }
   
   /**
    * Gets the number of threads from the user and counts primes using that many threads.
    */
   public static void main(String[] args) {
      int numberOfThreads = 0;
      while (numberOfThreads < 1 || numberOfThreads > 5) {
         System.out.print("How many threads do you want to use  (from 1 to 5) ?  ");
         numberOfThreads = TextIO.getlnInt();
         if (numberOfThreads < 1 || numberOfThreads > 5)
            System.out.println("Please enter 1, 2, 3, 4, or 5 !");
      }
      countPrimesWithThreads(numberOfThreads);
   }
   
   /**
    * Count the primes between min and max, inclusive.
    */
   private static int countPrimes(int min, int max) {
      int count = 0;
      for (int i = min; i <= max; i++)
         if (isPrime(i))
            count++;
      return count;
   }
   
   /**
    * Test whether x is a prime number.
    * x is assumed to be greater than 1.
    */
   private static boolean isPrime(int x) {
      int top = (int)Math.sqrt(x);
      for (int i = 2; i <= top; i++)
         if ( x % i == 0 )
            return false;
      return true;
   }
   
}
