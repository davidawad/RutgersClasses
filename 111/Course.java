public class Course {
	int deptNum;
	int courseNum;
	
	public Course(int deptNum, int courseNum){
		this.deptNum = deptNum;
		this.courseNum = courseNum;
	}
	
	public String toString( ){
		return deptNum + ":" + courseNum;
	}
	
	
}
