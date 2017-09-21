package search;

import java.util.*;


public class InsertTest {
	public static void main(String[] args){
		ArrayList<Occurrence> test = new ArrayList<Occurrence>();
		// 12 9 7 6 13
		test.add(new Occurrence("test", 12));
		test.add(new Occurrence("test", 8));
		test.add(new Occurrence("test", 7));
		test.add(new Occurrence("test", 5));
		test.add(new Occurrence("test", 3));
		test.add(new Occurrence("test", 2));
		test.add(new Occurrence("test", 6));
		LittleSearchEngine lse = new LittleSearchEngine();
		ArrayList<Integer> i = lse.insertLastOccurrence(test);
		if(i == null){
			System.out.print("null");
			return ; 
		}
		for (Integer e : i) {
			System.out.println(e);
		}
	}
}
