import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Search, learning and problem solving: SearchConsole.java
 * Top-level code for playing with search problems.
 *
 * Adapted by Matthew Stone from Roy Frostig's 
 * text reconstruction assignment (util.py)
 * for the Stanford AI class.
 *
 * @author Matthew Stone
 * @version 1.0
 */

public class SearchConsole {

    /**
     * Class for commands that apply fixed models to input data.
     */
    public static class VisualizationCmd {
	/** how to call the command */
	public String name;
	/** what to print on the help line and before results appear */
	public String description;
	
	/**
	 * Constructor
	 * @param name what to call the command
	 * @param description what to print on the help line 
	 */
	public VisualizationCmd(String name, String description) {
	    this.name = name;
	    this.description = description;
	}
	/**
	 * Method for visualizing input
	 * @param args input line as an array of tokens
	 */
	public void go(String[] args) {
	}
    }

    /** Commands for showing models */
    public static ArrayList<VisualizationCmd> vcs = new ArrayList<VisualizationCmd>();
    static {
	vcs.add(new VisualizationCmd("ug", "Unigram model report.\n") {
		public void go(String[] args) {
		    UnigramModel.getInstance().visualize(args);
		}
	    });
	vcs.add(new VisualizationCmd("bg", "Smoothed bigram model report.\n") {
		public void go(String[] args) {
		    SmoothedBigramModel.getInstance().visualize(args);
		}
	    });
	vcs.add(new VisualizationCmd("ed", "Expansion dictionary model report.\n") {
		public void go(String[] args) {
		    ExpansionDictionary.getInstance().visualize(args);
		}
	    });
	vcs.add(new VisualizationCmd("ebg", "Explicit bigram model report.\n") {
		public void go(String[] args) {
		    ExplicitBigramModel.getInstance().visualize(args);
		}
	    });
    }

    /**
     * Class for commands that carry out search operations
     */
    public static class SearchCmd {
	/** how to call the command */
	public String name;
	/** what to print on the help line and before results appear */
	public String description;
	/** builder object to create initial states for search */
	public SearchState.Builder builder;

	/**
	 * Constructor
	 * @param name how to call the command
	 * @param description what this does: printf expression with %s for argument
	 * @param builder object to create initial states for search
	 */
	public SearchCmd(String name, String description, SearchState.Builder builder) {
	    this.name = name;
	    this.description = description;
	    this.builder = builder;
	}
	/**
	 * Preprocessing operation on input tokens, 
	 * to set up an appropriate problem in the case of
	 * reconstruction problems where it's easiest to
	 * give the input after reconstruction 
	 * (e.g., create a vowel search problem with ordinary typing)
	 *
	 * @param args input tokens including command name
	 */
	public String simplify(String[] args) {
	    return LangUtil.join(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)), " ");
	}
    }

    /** Commands for driving search */
    static public ArrayList<SearchCmd> cmds = new ArrayList<SearchCmd>();
    static {
	cmds.add(new SearchCmd("cc", "Finding recipe for %s\n", 
			       new TestSearchState.Builder()));

	/*
	 * Add back in the code below when you're ready to test
	 * your implementation of SpaceSearchState:
	cmds.add(new SearchCmd("sp", "Adding spaces to %s\n", 
			       new SpaceSearchState.Builder()) {
		@Override
		public String simplify(String[] args) {
		    return LangUtil.join(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)), "");
		}
	    });
	*/

	/*
	 * Add back in the code below when you're ready to test
	 * your implementation of VowelSearchState:
	cmds.add(new SearchCmd("vw", "Adding vowels to %s\n", new VowelSearchState.Builder()) {
		@Override
		public String simplify(String[] args) {
		    return LangUtil.removeVowels(LangUtil.join(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)), " "));
		}
	    });
	*/

	/*
	 * Add back in the code below when you're ready to test
	 * your implementation of CombinedSearchState:
	cmds.add(new SearchCmd("sv", "Adding vowels and spaces to %s\n", new CombinedSearchState.Builder()) {
		@Override
		public String simplify(String[] args) {
		    return LangUtil.removeVowels(LangUtil.join(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)), ""));
		}
	    });
	*/
    }

    /** Manage interactive IO */
    private BufferedReader inputReader = 
	new BufferedReader(new InputStreamReader(System.in));
    
    /**
     * Display a prompt and read a line of input
     * @return user input
     */
    public String promptedInput() {
	System.out.printf(">>> ");
	System.out.flush();
	String result = null;
	try {
	    result = inputReader.readLine();
	} catch (IOException ex) {
	    System.err.println(ex.getMessage());
	    System.exit(1);
	}
	return result;
    }

    /**
     * Display help information about possible commands
     */
    private void printHelp() {
	System.out.println("Possible commands:");
	System.out.println("help : This message.");
	System.out.println("quit : Quit.");
	for (SearchCmd c : cmds) {
	    System.out.print(c.name + " : ");
	    System.out.printf(c.description, "argument");
	}
	for (VisualizationCmd c : vcs) {
	    System.out.printf(c.name + " : " + c.description);
	}
    }

    /**
     * See if the input tokens involve a search command,
     * and if so, set it up and return the initial state
     * of the corresponding search problem, or null if
     * no command applies.
     *
     * @param problems user input tokens
     * @return initial state for search
     */
    private SearchState interpretSearchCmd(String[] problems) {
	for (SearchCmd c : cmds) {
	    if (c.name.equals(problems[0])) {
		String arg = c.simplify(problems);
	        System.out.printf(c.description, arg);
		return c.builder.makeInitialState(arg);
	    }
	}
	return null;
    }

    /**
     * See if the input tokens involve calculating with a model
     * and if so, carry out the visualization task for the user.
     *
     * @param problems user input tokens
     */
    private void interpretVisualizationCmd(String[] problems) {
	for (VisualizationCmd c : vcs) {
	    if (c.name.equals(problems[0])) {
		System.out.printf(c.description);
		c.go(Arrays.copyOfRange(problems, 1, problems.length));
	    }
	}
    }

    /**
     * Run a search for the user and print out the results.
     *
     * @param start initial state of the search problem
     */
    private void runSearch(SearchState start) {
	Search search = new Search(start);
	try {
	    Node n = search.solve();
	    if (n != null) {
		System.out.println("\nSolution found (" + 
				   Double.toString(n.getPriority()) +
				   "): " + n.history());
	    } else {
		System.out.println("\nNo solution found!");
	    }
	} catch (Search.SearchLimitReached s) {
	    System.out.println("\nSearch Limit Reached!");
	}
    }

    /**
     * main loop: process commands
     *
     * @param args command line input (ignored)
     */
    public static void main(String[] args) {
	SearchConsole sc = new SearchConsole();
	do {
	    try {
		String input = sc.promptedInput().trim();
		if (input == null || input.equals("quit")) {
		    System.exit(0);
		} else if (input.equals("help")) {
		    sc.printHelp();
		    continue;
		}
		String[] problems = input.split("\\s");
		SearchState start = sc.interpretSearchCmd(problems);
		if (start != null) {
		    sc.runSearch(start);
		} else {
		    sc.interpretVisualizationCmd(problems);
		}
	    } catch (IllegalArgumentException e) {
		System.out.println(e);
	    }
	} while (true);
    }
}
