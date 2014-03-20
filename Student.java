package eProctor;
import java.util.*;

public class Student extends User{
	private String matricNo;
	private String course;
	
	public Student(int u, String p, String e, String n, String m, String c){
		super(u, p, e, n);
		matricNo = m;
		course = c;
	}

	//get and set methods
	public void setMatricNo(String m){
		this.matricNo = m;
	}
	public String getMatricNo(){
		return this.matricNo;
	}
	public void setCourse(String c){
		this.course = c;
	}
	public String getCourse(){
		return this.course;
	}
}
