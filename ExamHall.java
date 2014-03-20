package eProctor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.text.*;
import java.io.*;
import java.net.*;


public class ExamHall {
	private ExamSlot examSlot;
	private String examHallID;
	private EventLog eventLog;
	private Recording recording;
	private ArrayList inExam;
			
	public ExamHall(ExamSlot es, String ehID, EventLog el){
		examSlot = es;
		examHallID = ehID;
		eventLog = el;
		recording = null;
		inExam = new ArrayList();
	}
	
	public ExamHall(){
		
	}
	
	//get and set methods
	public void setExamSlot(ExamSlot es){
		this.examSlot = es;
	}
	public ExamSlot getExamSlot(){
		return this.examSlot;
	}	
	public void setExamHallID(String ehID){
		this.examHallID = ehID;
	}
	public String getExamHallID(){
		return this.examHallID;
	}	
	public void setEventLog(EventLog el){
		this.eventLog = el;
	}
	public EventLog getEventLog(){
		return this.eventLog;
	}	
	public void setRecording(Recording r){
		this.recording = r;
	}
	public Recording getRecording(){
		return this.recording;
	}	
	public void setAttendanceList(ArrayList al){
		this.inExam = al;
	}
	public ArrayList getAttendanceList(){
		return this.inExam;
	}	

}
