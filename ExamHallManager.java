package eProctor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.text.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class ExamHallManager {
	
	//Communication Protocol
	private final int CONNECT = 1;
	private final int MSG = 2;
	private final int START = 3;
	private final int FINISH = 4;
	
	//Connection
	private DataInputStream in;
	private DataOutputStream out;
	public ExamHallManager(){
		
	}
	
	//enter exam Hall
	public ExamHall enterExamHall(User user, String examHallID){
		
		ExamSlot examSlot=null;
		ExamHall examHall = null;
		//check if user can enter examhall
		String url = "jdbc:mysql://localhost:3306/";
        String dbName = "cz2006?";
        String driver = "com.mysql.jdbc.Driver";
        String username = "user=root&";
        String password = "password=pass";
        
        try {
        	Class.forName(driver);
	        Connection conn = DriverManager.getConnection(url+dbName+username+password);
	        Statement st = conn.createStatement();
	        
	        //check if user is authorised.
	        ResultSet res = st.executeQuery("SELECT ModuleAttendance.userID, examHall.invigilatorID " +
	    	        "FROM ModuleAttendance inner join ExamHall on ModuleAttendance.examHallID = ExamHall.examHallID " +
	    	        "WHERE ModuleAttendance.ExamHallID = '"+examHallID+"' and (ModuleAttendance.userID = '"+user.getUserID()+"' or ExamHall.invigilatorID = '"+user.getUserID()+"')");
        	
	        boolean allowedUser;
	        while(res.next()){
	        	allowedUser = true;
	        }
	        
	        //check if its time to enter
	        res = st.executeQuery("SELECT Examslot.startTime, Examslot.endTime, examslot.examslotID, examslot.noOfExamhalls " +
	    	        "FROM ExamSlot inner join ExamHall on examSlot.examSlotID = Examhall.examslotID " +
	    	        "WHERE ExamHall.ExamHallID = '"+examHallID+"'");
	        
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        

	        java.util.Date currentTime = Calendar.getInstance().getTime();
	        java.util.Date currentDateTwenty = new java.util.Date(System.currentTimeMillis()+20*60*1000);
	        
	        java.util.Date startDate = null;
	        java.util.Date startTime = null;
	        java.util.Date endTime = null;
	        
	        String examSlotID="";
	        int noOfExamHall =0;
	        while (res.next()){
	        	examSlotID = res.getString("examslotID");
	        	noOfExamHall = res.getInt("noOfExamhalls");
	        	startDate = res.getTimestamp("startTime");
	        	startTime = res.getTime("startTime");
	        	endTime = res.getTime("endTime");
	        }

	        if(currentDateTwenty.compareTo(startDate) > 0){
	        	System.out.println("Access Allowed.");
	        	examSlot = new ExamSlot(examSlotID, noOfExamHall, startTime, endTime, startDate);
	        	//create examHall
	        	examHall = new ExamHall(examSlot, examHallID, null);
	        }
	        else{
	        	System.out.println("Access Denied. Time has not reached");
	        }
	        
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
		
		return examHall;
	}
	
	public Socket connectExamHall(ExamHall examHall, User user){
		String serverAddr = "172.22.127.64"; 	// server host name
		int portNo=2001;
		//String serverAddr = "172.22.105.24"; 	// server host name
		//int portNo = 2000;	     		// server port number
		try {
	  			// S1 - create a socket to connect to server          
			Socket con = new Socket(serverAddr, portNo);
			in = new DataInputStream(con.getInputStream());
			out = new DataOutputStream(con.getOutputStream());
			
			out.writeUTF(examHall.getExamHallID());
			out.writeInt(CONNECT);
			//Send examhallID and user ID
			out.writeUTF(examHall.getExamHallID());
			out.writeInt(user.getUserID());
			if(user instanceof Student){
				out.writeInt(1);				
			}
			else{
				out.writeInt(0);
			}
			
			
			return con; 
	   	  } 
		catch (UnknownHostException e) {
			System.out.println("Error : Unable to find host");
		} 
		catch (IOException e) {
			System.out.println("Error : Unable to get I/O for the connection");
		}

		return null;
	}

	//Start Exam
	public boolean startExam(ExamHall examHall){
		
		//check if exam has reached timing
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date currentTime = Calendar.getInstance().getTime();
        
        java.util.Date startTime = examHall.getExamSlot().getStartTime();
        
        if(currentTime.compareTo(startTime) > 0){
        	return true;
        }
        
		
		return false;
	}

	//Methods for end of exam
	//End student exam
	public void getParticipantList(Socket c, ExamHall examHall){
		try{
			out = new DataOutputStream(c.getOutputStream());
			out.writeInt(FINISH);
			
		}
		catch(IOException ex){
			System.out.println("Error : Unable to get I/O for the connection");
		}
	}
	public void endStudentTakable(int userID, String examHallID){
		String url = "jdbc:mysql://localhost:3306/";
        String dbName = "cz2006?";
        String driver = "com.mysql.jdbc.Driver";
        String username = "user=root&";
        String password = "password=pass";
        
        try {
        	Class.forName(driver);
	        Connection conn = DriverManager.getConnection(url+dbName+username+password);
	        Statement st = conn.createStatement();
	        
	        //check if user is authorised.
	        st.execute("UPDATE ModuleAttendance " + 
			        "SET takable='0' " + 
			        "WHERE UserID='"+userID+"' and examHallID='"+examHallID+"' ");
        }
        catch(Exception ex){
        	ex.printStackTrace();
        }
	}

	//update eventlog
	public void endEventLog(Socket c, ExamHall examHall){
		try{
			PrintWriter writer= new PrintWriter(new BufferedWriter(new FileWriter("eProctorServer/EventLog/ExamHall=" + examHall.getExamHallID() + ".txt", true)));
			
			//update eventlog for students whole takable = 1
			//check if user can enter examhall
			String url = "jdbc:mysql://localhost:3306/";
			String dbName = "cz2006?";
			String driver = "com.mysql.jdbc.Driver";
			String username = "user=root&";
			String password = "password=pass";
			
			
			
			//writer.println(name+ " has joined the examHall");
			writer.close();
			
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
	}
}
