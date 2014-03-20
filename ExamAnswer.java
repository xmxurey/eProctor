package eProctor;
import java.util.*;

public class ExamAnswer {
	private int answerID;
	private String content;
	private ModuleAttendance moduleAttendance;
	
	public ExamAnswer(int a, String c, ModuleAttendance ma){
		answerID = a;
		content = c;
		moduleAttendance = ma;
	}

	//get and set methods
	public void setAnswerID(int a){
		this.answerID = a;
	}
	public int getAnswerID(){
		return this.answerID;
	}
	public void setContent(String c){
		this.content = c;
	}
	public String getContent(){
		return this.content;
	}
	public void setModuleAttendance(ModuleAttendance ma){
		this.moduleAttendance = ma;
	}
	public ModuleAttendance getModuleAttendance(){
		return this.moduleAttendance;
	}
}
