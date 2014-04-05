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

public class ExamHallManager implements Serializable{
	
	
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
		String url = "jdbc:mysql://"+Protocol.serverAddr+":3306/";
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
	        	examHall = new ExamHall(examSlot, examHallID);
	        }
	        else{
	        	JOptionPane.showMessageDialog(null, "You cannot enter the exam hall because it is 20 minutes before the start of the exam");
	        }
	        
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
		
		return examHall;
	}
	
	public Socket connectExamHall(ExamHall examHall, User user){
		String serverAddr = Protocol.serverAddr; 	// server host name
		int portNo=Protocol.serverPortNo;
		try {
	  			// S1 - create a socket to connect to server      
			
			
			Socket con = new Socket(serverAddr, portNo);
			in = new DataInputStream(con.getInputStream());
			out = new DataOutputStream(con.getOutputStream());
			
			//Send examhallID and user ID
			out.writeUTF(examHall.getExamHallID());
			out.writeInt(user.getUserID());
			out.writeBoolean(user.isStudent());
			
			boolean allow=false;
			allow = in.readBoolean();
			if(allow){
				out.writeInt(Protocol.CONNECT);
			}
			else{
				con = null;
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

	public void receiveQuestion(Socket client, ExamHall examHall){
		byte[] byteArray; 
	    BufferedInputStream bis;   
	    BufferedOutputStream bos; 
	    
		try{
			Socket socket = new Socket(Protocol.questionTransferAddr, Protocol.questionTransferPort);

			FileOutputStream fos = new FileOutputStream("Local/ExamQuestion/ExamHall="+examHall.getExamHallID()+".pdf");
			bos = new BufferedOutputStream(fos);
			byte[] buffer = new byte[2022386];
			int count;
			InputStream in = socket.getInputStream();
			while((count=in.read(buffer)) >=0){
				fos.write(buffer, 0, count);
			}
			fos.close();
			
			socket.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public int checkJoinNo(Socket client){
		try{
			out = new DataOutputStream(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());
			out.writeInt(Protocol.CHECKJOINNO);
			int joinNo = in.readInt();
			return joinNo;
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		
		
		return 0;
	}
	
	//Start Exam
	public boolean startExam(ExamHall examHall){
		
		//check if exam has reached timing
		java.util.Date currentDate = new java.util.Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		String time1 = dateFormat.format(currentDate);
        String time2 = dateFormat.format(examHall.getExamSlot().getStartTime());
        
        if(time1.compareTo(time2) > 0){
        	return true;
        }
        
		
		return false;
	}

	//Methods for end of exam
	//End student exam
	public void studentFinishExam(Socket c, ExamHall examHall, User user){
		try{
			out = new DataOutputStream(c.getOutputStream());
			out.writeInt(Protocol.STUDENTSENDANSWER);
			out.writeInt(Protocol.FINISH);
			
		}
		catch(IOException ex){
			System.out.println("Error : Unable to get I/O for the connection");
		}
	}
	
	public void finishExam(Socket c, ExamHall examHall){
		try{
			out = new DataOutputStream(c.getOutputStream());
			out.writeInt(Protocol.ALLSENDVIDEO);
			out.writeInt(Protocol.ALLSENDANSWER);
			out.writeInt(Protocol.FINISHALL);
			
		}
		catch(IOException ex){
			System.out.println("Error : Unable to get I/O for the connection");
		}
	}

	public void sendVideo(Socket c, String examHallID){  
		byte[] byteArray; 
	    BufferedInputStream bis;   
	    BufferedOutputStream bos; 
	    

	    String fileToSend = "Local/ExamRecording/ExamHall="+examHallID+".mov";
	    
		try{

			Socket socket = new Socket(Protocol.videoTransferAddr, Protocol.videoTransferPort);
			
	    	File myFile = new File(fileToSend);
			int count;
	    	byte[] buffer = new byte[2022386];

	    	OutputStream out = socket.getOutputStream();
	    	BufferedInputStream in = new BufferedInputStream(new FileInputStream(myFile));
	    	while ((count = in.read(buffer)) >= 0) {
	    	     out.write(buffer, 0, count);
	    	     out.flush();
	    	}
			socket.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void sendAnswer(Socket c, String fileToSend){  
		byte[] byteArray; 
	    BufferedInputStream bis;   
	    BufferedOutputStream bos; 
	    
		try{
			Socket socket = new Socket(Protocol.answerTransferAddr, Protocol.answerTransferPort);
			
	    	File myFile = new File(fileToSend);
			int count;
	    	byte[] buffer = new byte[2022386];

	    	OutputStream out = socket.getOutputStream();
	    	BufferedInputStream in = new BufferedInputStream(new FileInputStream(myFile));
	    	while ((count = in.read(buffer)) >= 0) {
	    	     out.write(buffer, 0, count);
	    	     out.flush();
	    	}
			socket.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void endStudentTakable(int userID, String examHallID){
		String url = "jdbc:mysql://"+Protocol.serverAddr+":3306/";
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

	public void terminateStudent(Socket c, int userID, ExamHall examHall, String reason){
		try{
			out = new DataOutputStream(c.getOutputStream());
			out.writeInt(Protocol.STUDENTREMOVAL);
			out.writeUTF(examHall.getExamHallID());
			out.writeInt(userID);
			out.writeUTF(reason);
		}
		catch(IOException ex){
			System.out.println("Error : Unable to get I/O for the connection");
		}
	}
}
