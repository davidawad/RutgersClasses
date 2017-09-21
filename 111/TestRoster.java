public class TestRoster {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Student s1 = new Student("John","Doe");
		Course c1 = new Course(198, 111);
		Roster r1 = new Roster(4, c1);
		System.out.println(r1);
		testAdd(r1, s1);
	}

    private static void testAdd(Roster r, Student s){
	System.out.println(s.familyName+" "+r.addStudent(s));
	System.out.println(r);
    }	
}
