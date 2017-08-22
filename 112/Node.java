public class Node {
    public String data;
    public Node next;
    public Node(String data, Node next) {
        this.data = data; this.next = next;
    }
 }
 public class LinkedList {
    private Node rear;  // pointer to last node of CLL
    
 public void delete(String target)throws NoSuchElementException {
	 Node prev = rear.next;
	 Node curr = prev.next ;
	 while(curr != null){
		 if (curr.data == target){
			 prev.next = curr.next ;
		 }
	 
	 }

	 }        
} 
