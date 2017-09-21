public class Student {
	String personalName;
	String familyName;
	
	public Student(String pName, String fName){
		personalName = pName;
		familyName = fName;
	}
	
	public String toString( ){
		return "Student: " + familyName + ", "+ personalName;
	}
}
