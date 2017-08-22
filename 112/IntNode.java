public class IntNode {
   public int data;
   public IntNode next;
  
   public IntNode(int data, IntNode next) {
   	this.data = data; this.next = next;
   }
   public String toString() {
   	return data + "";
   }

// Implement a method that will add a new integer before a target integer in the list.
//The method should return a pointer/reference to the front node of the resulting list:

 
  public static IntNode addBefore(IntNode front, int target, int newItem) {
   	
      while(front != null){  
   	   if(front.next.data == target){
   		   front.next = new IntNode(newItem , front.next) ;
   	   }
   	   front = front.next ;
   		 
      }
      return front ;
  }

  public static void deleteEveryOther(IntNode front) {   //done
 	 
      while(front.next.next != null ){
   	   front.next = front.next.next ;
   	   front = front.next.next ;  
      }
  }
//creates a new linked list consisting of the items common to the input lists
 // returns the front of this new linked list, null if there are no common items
  public IntNode commonElements(IntNode frontL1, IntNode frontL2) {
 
  IntNode cheese = new IntNode( 0 , null);
  result = cheese ;
  while(frontL1 != null){	
	  int num1 = frontL1.data ;
	  while(frontL2 != null){
		if(frontL2.data == num1 ){
		   IntNode common = new IntNode (frontL2.data , cheese) ;   
		  cheese = common ; 
		  cheese = cheese.next ;
		  }
		frontL2 = frontL2.next ;
		}
	  frontL1 = frontL1.next ;
	  }
  if(result == null){
	  return null ;
  }
	else {return result ;} 
	}

// in class example, how to delete an item from a linked list... JSON!

public static IntNode delete(IntNode front , int target){
if(front == null){
return front ;
}
if(front.data == target){
front = front.next ;
return front;
}
IntNode p = front ;
IntNode s = front.next ;
while(s != null){
if(s.data == target){
p.next = s.next ;
}
p = p.next ;
s = s.next ;
}
return front; 
}

}
