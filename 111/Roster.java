public class Roster {
    Student [ ] students;
    int numStudents;
    int stopPoint;
    Course course;
	
    /**
     * The constructor for this class.
     * Initialize this roster to empty, i.e., holds no students,
     *   with given stop point and course
     */
    public Roster(int stopPoint, Course course){

	// replace this line with your code

    }
    
    /**
     * toString is a method every class has.  It returns a string 
     * that represents the object for printing
     */
    public String toString( ){
	String res = "";
	for(int j = 0; j < numStudents; j++){
	    res = res + "\n" + students[j].toString();
	}
	return course + " " + numStudents + "/" + stopPoint+res;
    }

    /**
     * isFull returns true if and only if the number of students in it is 
     *   at the stopPoint
     */
    public boolean isFull( ){
	return false;   // replace this line with your code
    }
	
    /**
     * add given student to this roster
     * if student already on roster or numStudents already == stopPoint, 
     *   do not change roster and return false
     * @return true if successful, else false
     */
    public boolean addStudent(Student student){

	return false; // replace this line with your code

    }


    /**
     * returns true if and only if the student is on this roster.
     */
    public boolean findStudent(Student student){

	return false; // replace this line with your code

    }

    /**
     * Remove given student from this roster. 
     * If student is not on this roster do not change roster and return false
     * @return true if successful, else false
     */
    public boolean dropStudent(Student student){

	return false; // replace this line with your code

    }

}