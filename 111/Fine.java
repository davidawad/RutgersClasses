public class Fine{
    public static void main(String[] args) {
	int daysLate = IO.readInt;
	int bookCost = IO.readInt; 
	if(daysLate <= 7) {
	int fine = daysLate* 10; 
	}
	else if( 90 >= daysLate > 7 ){
	daysLate = daysLate - 7;
	int fine = 70 + daysLate * 20;
	IO.outputIntAnswer(fine);
	}
	else{ // book is lost
	system.out.println("Book is Lost"); 
	int fine = 100 + bookCost;
	IO.outputIntAnswer(fine);
	}
}

