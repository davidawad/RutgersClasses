package search;


import java.io.FileNotFoundException;
import java.util.ArrayList;


public class LittleSearchEngineDriver {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		LittleSearchEngine lse = new LittleSearchEngine();
		lse.makeIndex("docs.txt", "noisewords.txt");

		// getkeyWord
		System.out.println("getkeyWord");
		System.out.println("================================");
		String s[] = {"distance.", "equi-distant", "Rabbit", "Between", "we're",
				"World...", "World?!", "What,ever"};

		for (int i = 0; i < s.length; i++) {
			System.out.println(lse.getKeyWord(s[i]));
		}
		System.out.println("================================");
		System.out.println("insertLastOccurrence");
		System.out.println("================================");
		// insertLastOccurrence
		int data[] = {12, 8, 7, 5, 3, 2, 6};
		ArrayList<Occurrence> occs = new ArrayList<Occurrence>();
		for (int i = 0; i < data.length; i++) {
			occs.add(new Occurrence("Doc", data[i]));
		}
		ArrayList<Integer> a = lse.insertLastOccurrence(occs);
		System.out.print("Sequence: ");
		if(a == null){
			System.out.println("deddle doo");
		}
		for (int i = 0; i < a.size(); i ++) {
			System.out.print(a.get(i) + " ");
		}
		System.out.println();
		System.out.print("Result: ");
		for (int i = 0; i < occs.size(); i++) {
			System.out.print(occs.get(i).frequency + " ");
		}
		System.out.println();
		System.out.println("================================");
		System.out.println("top5search");
		System.out.println("================================");
		ArrayList<String> top = lse.top5search("deep", "world");
		System.out.println(top);
	}

}



