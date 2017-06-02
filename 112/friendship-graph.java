//Leo Yu and Jeremy Priestner

package app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import structures.Neighbor;
import structures.FriendGraph;
import structures.User;


public class Friends {

        static Scanner kb;
        static FriendGraph friendGraph = null;

        static void build(String fileName)
        throws IOException {

                HashMap<String, Integer> nameTable = new HashMap<String,Integer>(1000,2.0f);
                int index1, index2;
                User user1, user2;

                Scanner sc = new Scanner(new File(fileName));

                //first line is number of vertices
                int n = sc.nextInt();
                ArrayList<User> userList = new ArrayList<User>(n);
                sc.nextLine();//skip blank line

                //read student info from file and add to arraylist
                //and to the indexing hashmap "nameTable" with key-value pair <Name,Index in ArrayList al>
                for (int i = 0 ; i < n ; i++) {

                        //go through each line and tokenize it using "|" as a delimiter
                        String str = sc.nextLine();
                        StringTokenizer st = new StringTokenizer(str,"|");

                        String name = st.nextToken().toLowerCase();
                        String yesNo = st.nextToken().toLowerCase();

                        User user = new User(name);
                        if (yesNo.equals("y")) {
                                String school = st.nextToken().toLowerCase();
                                user.school = school;
                        }

                        userList.add(user);

                        //debug
                        System.out.println("\tUser: " + user.toString());

                        nameTable.put(name,i);
                }

                //build the neighbor linked lists from the edge information contained in the input file
                while(sc.hasNext()) {
                        StringTokenizer st = new StringTokenizer(sc.next(),"|");
                        //hashmap access
                        index1 = nameTable.get(st.nextToken());
                        index2 = nameTable.get(st.nextToken());
                        //arraylist access
                        user1 = userList.get(index1);
                        user2 = userList.get(index2);

                        if (user1.firstNeighbor == null)
                                user1.firstNeighbor = new Neighbor(index2, null);
                        else {
                                Neighbor tmp = user1.firstNeighbor;
                                user1.firstNeighbor = new Neighbor(index2, tmp);
                        }

                        if (user2.firstNeighbor == null)
                                user2.firstNeighbor = new Neighbor(index1, null);
                        else {
                                Neighbor tmp = user2.firstNeighbor;
                                user2.firstNeighbor = new Neighbor(index1, tmp);
                        }

                        //debug
                        System.out.println("\tedge: " + user1.name + "|" + userList.get(user1.firstNeighbor.neighborIndex).name);
                }

                friendGraph = new FriendGraph(userList, nameTable);
    }

        static void subgraph(){
                System.out.println("Enter the name of the school you want a subgraph for: ");
                kb.nextLine();
                String str = kb.nextLine();

                //original code
                //friendGraph.subgraph(str).printGraph();

                //modified code w/ option to keep/replace original graph
                FriendGraph outputGraph = friendGraph.subgraph(str);
                if(outputGraph == null){
                        System.out.println("Subgraph of " + str + " students is empty");
                }else{
                        outputGraph.printGraph();
                }

                System.out.println("Replace current graph with subgraph of " + str + " students? (y/n)");
                while (true) {
                        str = kb.next();
                        if (str.equalsIgnoreCase("y")) {
                                friendGraph = outputGraph;
                                break;
                        }
                        else if (str.equalsIgnoreCase("n")) {
                                break;
                        }
                        else
                                System.out.println("Not valid input. Try again.");
                }
        }

        static void shortestPath(){
                System.out.println("Enter the name of the start user: ");
                String str1 = kb.next();
                System.out.println("Enter the name of the end user: ");
                String str2 = kb.next();

                //throws exception if there is know path between start and end user
                try{
                        friendGraph.shortestPath(str1, str2);
                }catch(Exception e){
                        System.out.println(e.getMessage());
                }
        }

        static void islands(){
                kb.nextLine();
                System.out.println("Enter the name of the school you want the cliques for: ");
                String str = kb.nextLine();
                friendGraph.islands(str);
                System.out.println("");
        }

        static void connectors() {
                friendGraph.connectors();
        }

        static void printGraph() {
                if(friendGraph == null){
                        System.out.println("Graph is empty");
                }else{
                        friendGraph.printGraph();
                }
        }

        static void printHash() {
                friendGraph.printHash();
        }

        public static void main(String[] args) throws IOException {

     	           kb = new Scanner(System.in);

     	           while (true) {
     	                   System.out.println("Type the name of the input graph file: ");
     	                   String line = kb.next();
     	                   if (line.length() == 0) { continue; }
     	                   build(line);
     	                   break;
     	                   }

     	           while (true) {
     	                   System.out.println("");
     	                   System.out.println("Do you want to run:" +
     	                                   "\n(1) subgraph" +
     	                                   "\n(2) shortest path" +
     	                                   "\n(3) connected islands" +
     	                                   "\n(4) connectors" +
     	                                   "\n(5) print graph" +
     	                                   "\n(6) print hash table" +
     	                                   "\n(7) quit");
     	                   String line = kb.next();
     	                   if (line.equals("")){
     	                           continue;
     	                   }

     	                   switch(line.charAt(0)) {
     	                   case '1': subgraph(); break;
     	                   case '2': shortestPath(); break;
     	                   case '3': islands(); break;
     	                   case '4': connectors(); break;
     	                   case '5': printGraph(); break;
     	                   case '6': printHash(); break;
     	                   case '7': System.exit(0);
     	                   }
     	           }
     	   }
}


