package eProctor;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.text.*;
import java.nio.file.Files;





import javax.swing.*;

public class Server extends Thread{
	int port = 2001;  
	int connectors = 50;
	Socket client;
	DataInputStream in;
	DataOutputStream out;
	
	String examHallID;
	int userID;
	boolean isStudent;
	ArrayList<Session> ExamHallParticipantList;
	
	//Communication Protocol
	private final int CONNECT = 1;
	private final int MSG = 2;
	private final int START = 3;
	private final int FINISHALL = 4;
	private final int FINISHTIMER = 5;
	private final int TERMINATE = 6;
	private final int SENDVIDEO = 7;
	private final int SENDANSWER = 8;
	
	
	public Server(){
		ExamHallParticipantList = new ArrayList();
	}
	
	public void run() {
		try{
			ServerSocket ss = new ServerSocket(port, connectors);
    		System.out.println("Waiting for client connection .. ");

    		boolean allow=false;
			while(true){
				client = ss.accept();
				in = new DataInputStream(client.getInputStream()); 
				out = new DataOutputStream(client.getOutputStream()); 		
	    		examHallID = in.readUTF();	
	    		userID = in.readInt();	
	    		isStudent = in.readBoolean();
	    		Session s = new Session(client, examHallID, userID, isStudent);	
	    		allow = checkLogin(s, isStudent);
	    		out.writeBoolean(allow);
	    		if(allow){
	    			ExamHallParticipantList.add(s);
		    		s.start();	
		    		System.out.println("Client successfully connected.");
	    		}
	    		else{
	    			client.close();
	    		}
	    		
	    		
			}
		}
		catch(Exception e){
			System.out.println("Could not listen on port: "+port+".");

		}
    }

	/*
	 connect exam
	 */
	private synchronized boolean checkLogin(Session session, boolean isStudent) {
		boolean allow = true;
		
		//check if user is inside examhall already
		for (Session s : ExamHallParticipantList) {
			if(s.getExamHallID().equals(session.examHallID)){
				if(s.userID == session.userID)
					allow=false;
			}
		}
		if(allow){
			if(isStudent){
				allow = false;
				for (Session s : ExamHallParticipantList) {
					if(s.getExamHallID().equals(session.examHallID)){
						if(s.isStudent == false)
							allow=true;
					}
				}
			}
		}
		return allow;
	}
	
	private synchronized void connectInvExam(String examHallID, int userID) {
		for (Session s : ExamHallParticipantList) {
			if(s.getExamHallID().equals(examHallID)){
				if(s.isStudent == false){
					//isInvigilator
					s.writeInt(CONNECT);
					s.writeInt(userID);
				}
			}
		}
	}
	
	/*
	  Send Msg
	 */
	private synchronized void broadcast(String examHallID) {
        for (Session s : ExamHallParticipantList) {
            
        	if(s.getExamHallID().equals(examHallID)){
        		s.writeInt(MSG);
        		try {
	        		BufferedReader bIn = new BufferedReader(new FileReader("eProctorServer/EventLog/ExamHall=" + examHallID + ".txt"));
	        		String msg="";
	        		String str="";
	
	        		while((str=bIn.readLine()) !=null){
	        			msg+= str+" \n";
	        		}
	        		s.writeUTF(msg);
        		}
        		catch (IOException ex) {
                    System.out.println(" Exception reading Streams: " + ex);
                    break;
                }
        		//send msg here
        	}
        }
    }
	
	/*
	  Start Exam
	 */
	//method to invoke all students to start exam
	private synchronized void startExam(String e) {
    	String examHallID = e;
        for (Session s : ExamHallParticipantList) {
            
        	if(s.getExamHallID().equals(examHallID)){
        		s.writeInt(START);
        	}
        }
    }
	
	/*
	  Finish Exam
	 */
	private synchronized void studentSendAnswer(String e){
		 for (Session s : ExamHallParticipantList) {

		      	if(s.getExamHallID().equals(examHallID)){
		      		if(s.isStudent){
			      		s.writeInt(SENDANSWER);
			      		receiveAnswer();
		      		}
		      	}
		      }
	}
	//receive Answer File
	private void receiveAnswer(){
		BufferedInputStream bis;
		BufferedOutputStream bos;
		byte[] data;
		try{
			Socket socket = new Socket("127.0.0.1", 3001);
			FileOutputStream fos = new FileOutputStream("eProctorServer/ExamAnswerSheet/ExamHall="+examHallID+"_UserID="+userID+".txt");
			bos = new BufferedOutputStream(fos);
			byte[] buffer = new byte[1024];
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
	private synchronized void endTimer(int userID, String e){
		String examHallID = e;
      for (Session s : ExamHallParticipantList) {

      	if(s.getExamHallID().equals(examHallID) && userID == s.getUserID()){
      		s.writeInt(FINISHTIMER);
      	}
      }
	}
	
	//terminate client session
	private synchronized void terminateSession(int userID, String examHallID){
		for (Session s : ExamHallParticipantList) {
            
        	if(s.getExamHallID().equals(examHallID) && userID == s.getUserID()){

        		System.out.println("Session for UserID="+s.userID+" has ended.");
        		s.closeSession();
        	}
        }
	}
	
	/*
	  Others
	 */
	//method to return list of students in a particular examhall
	private synchronized ArrayList getStudentExamList(String e) {
    	String examHallID = e;
    	ArrayList participantList = new ArrayList();
        for (Session s : ExamHallParticipantList) {
        	if(s.getExamHallID().equals(examHallID)){
        		participantList.add(s.userID);
        	}
        }
        
        return participantList;
    }
	
	public static void main(String[] args) {
		Server server = new Server();
        server.start();
	}


//Session Class
	class Session extends Thread{
		private Socket client;
		private String examHallID;
		private int userID = 0;
		private boolean isStudent = true;
		
		//check if user can enter examhall
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "cz2006?";
		String driver = "com.mysql.jdbc.Driver";
		String username = "user=root&";
		String password = "password=pass";
		
		
		
		public Session(Socket s, String e, int u, boolean i){
			client = s;
			examHallID = e;
			userID= u;
			isStudent = i;
		}
		
		public String getExamHallID(){
			return examHallID;
		}
		public int getUserID(){
			return userID;
		}
		
		public void run(){
			try{
				DataInputStream  in = new DataInputStream(client.getInputStream());
				DataOutputStream out = new DataOutputStream(client.getOutputStream());
				
				Class.forName(driver);
		        Connection conn = DriverManager.getConnection(url+dbName+username+password);
		        Statement st = conn.createStatement();
				
				while(true){
					int code = in.readInt();
					
					if(code == CONNECT){

						connectExam(examHallID, userID, isStudent);
			    		connectInvExam(examHallID, userID);
						
			    		
						System.out.println("UserID = " + userID + " has entered examHallID= "+examHallID);
						
						//transfer only if it is invigilator who enters examhall
						

			    		if(!isStudent){
			    			File examQuestionPaperSource = new File("ntuserver/ExamQuestion/ExamHall=" + examHallID+ ".pdf");
					        File examQuestionPaperDest = new File("eproctorServer/ExamQuestion/ExamHall=" + examHallID+ ".pdf");
					        transferFile(examQuestionPaperSource, examQuestionPaperDest);
			    		}
					    
					}
					else if (code == MSG){
						String msg = in.readUTF();
						
						//get userName
						ResultSet res = st.executeQuery("SELECT user.Name from user where userID='" + userID + "'");
						
						String name="";
						
						while (res.next()){
							name = res.getString("Name");
						}
						sendMsg(msg, name);
						
					}
					else if(code == START){
						startExam(getExamHallID());
						startEventLog();
						
					}
					else if(code == FINISHALL){
						//send list of students in examhall back to examhallManager
						ArrayList participantList = new ArrayList();
						participantList = getStudentExamList(getExamHallID());
						
						endEventLog(participantList);
						sendList(participantList);
						
						//transfer files from eProctor Server to NTU Server
						//transfer eventlog
						File eventLogSource = new File("eProctorServer/EventLog/ExamHall=" + examHallID+ ".txt");
						File eventLogDest = new File("NTUServer/EventLog/ExamHall=" + examHallID+ ".txt");
						transferFile(eventLogSource,eventLogDest);
						
						//terminate session
						int userID=0;
						for(int i=0;i<participantList.size();i++){
							userID = (int)participantList.get(i);
							endTimer(userID, examHallID);
							terminateSession(userID, examHallID);
						}
					}
					else if(code == SENDVIDEO){
						//transfer Recording
						out.writeInt(SENDVIDEO);
						receiveVideo();
					}
					else if(code == SENDANSWER){
						//transfer examanswers				
						studentSendAnswer(examHallID);
						
					}
					else if(code == TERMINATE){

						String examID = in.readUTF();
						int uID = in.readInt();
						String reason = in.readUTF();
						
						//get name of student
						ResultSet res = st.executeQuery("SELECT user.Name from user where userID='" + userID + "'");
						
						String name="";
						
						while (res.next()){
							name = res.getString("Name");
						}
						terminateEventLog(reason, name);
						
						
						out.writeInt(TERMINATE);
		            	out.writeUTF(examID);
		            	out.writeInt(uID);
						
						endTimer(userID, examHallID);
						terminateSession(uID, examID);
					}
				}
				
			}
			catch (IOException e){ 
	        	e.printStackTrace();
			}
	        catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
		/*
		  Enter Exam
		 */
		//Connect to exam
		public void connectExam(String examHallID, int userID, boolean isStudent){
			try{
				Class.forName(driver);
		        Connection conn = DriverManager.getConnection(url+dbName+username+password);
		        Statement st = conn.createStatement();
				//update database set connect =1
				if(isStudent == true){
					st.execute("UPDATE ModuleAttendance SET Connected = '1' WHERE ExamHallID = '" + examHallID + "' and userID='"+ userID + "'");
				}
				
				//get userName
				ResultSet res = st.executeQuery("SELECT user.Name from user where userID='" + userID + "'");
				
				String name="";
				
				while (res.next()){
					name = res.getString("Name");
				}
				
				//Check if eventlog is created
				res = st.executeQuery("Select eventLogID FROM examhall where examHallID='" + examHallID + "'");
				int eventLogID=0;
				while (res.next()){
					eventLogID = res.getInt("eventLogID");
				}
				PrintWriter writer= new PrintWriter(new BufferedWriter(new FileWriter("eProctorServer/EventLog/ExamHall=" + examHallID+ ".txt", true)));
			
				if(eventLogID<=0){
					//no eventlog exist. Need to create eventlog
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					
					writer.println("Eventlog created on "+cal.getTime());
				}
	
				writer.println(name+ " has joined the examHall");
				writer.close();
				
				broadcast(getExamHallID());
			}
			catch(Exception ex){
	        	ex.printStackTrace();
			}
		}
		
			
		
		/*
		  Communication during exam
		 */
		//send msg
		public void sendMsg(String msg, String name){
			try{
				//UPDATE EVENTlOG
				PrintWriter writer= new PrintWriter(new BufferedWriter(new FileWriter("eProctorServer/EventLog/ExamHall=" + examHallID+ ".txt", true)));
	
				//no eventlog exist. Need to create eventlog
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				
	
				writer.println(name+ " ("+cal.getTime()+") says: "+ msg);
				writer.close();
				
				broadcast(getExamHallID());
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
		}
		
		//send to client informing the communication protocol
		private boolean writeInt(int code) {
	
	        // write the message to the stream
	        try {
	            out.writeInt(code);
	        }
	        // if an error occurs, do not abort just inform the user
	        catch (IOException e) {
	            System.out.println("Error sending message");
	            System.out.println(e.toString());
	        }
	        return true;
	    }
		
		private boolean writeUTF(String msg) {
			
	        // write the message to the stream
	        try {
	            out.writeUTF(msg);
	        }
	        // if an error occurs, do not abort just inform the user
	        catch (IOException e) {
	            System.out.println("Error sending message");
	            System.out.println(e.toString());
	        }
	        return true;
	    }
	
		/*
		  Start Exam
		 */
		//Update eventLog
		private void startEventLog(){
			try{
				PrintWriter writer= new PrintWriter(new BufferedWriter(new FileWriter("eProctorServer/EventLog/ExamHall=" + examHallID + ".txt", true)));
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				
				writer.println("ExamHallID = " + examHallID + " has officially started at "+cal.getTime()+".");
				writer.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		/*
		  Finish Exam
		 */
		//close session
		private void closeSession(){
			try{
				in.close();
				out.close();
				client.close();
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
		}
		
		//send to invigilator participantList
		private void sendList(ArrayList participantList){
			try {
	            DataOutputStream out = new DataOutputStream(client.getOutputStream());
	            int userID=0;

	            out.writeInt(FINISHALL);
            	out.writeUTF(examHallID);
            	out.writeInt(participantList.size());
            	
	            for(int i=0;i<participantList.size();i++){
	            	userID = (int)participantList.get(i);
	            	out.writeInt(userID);
	            }
	        }
			// if an error occurs, do not abort just inform the user
	        catch (IOException e) {
	            System.out.println("Error sending participantList");
	        }
		}
	
		//update eventlog
		private void endEventLog(ArrayList participantList){
			try{
				//update eventlog for students whole takable = 1
				//check if user can enter examhall
				String url = "jdbc:mysql://localhost:3306/";
				String dbName = "cz2006?";
				String driver = "com.mysql.jdbc.Driver";
				String username = "user=root&";
				String password = "password=pass";
				
				Class.forName(driver);
		        Connection conn = DriverManager.getConnection(url+dbName+username+password);
		        Statement st = conn.createStatement();
		        
		        int userID=0;
		        PrintWriter writer= new PrintWriter(new BufferedWriter(new FileWriter("eProctorServer/EventLog/ExamHall=" + examHallID + ".txt", true)));
		        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				
		        for(int i=0;i<participantList.size();i++){
	            	userID = (int)participantList.get(i);

	            	ResultSet res = st.executeQuery("select user.name, moduleAttendance.takable from moduleAttendance inner join User on User.userID = moduleattendance.userID " +
			    	        "WHERE moduleAttendance.userID='"+userID+"' and moduleAttendance.examHallID='"+examHallID+"'");
		        	
			        int takable=0;
			        String name="";
			        while(res.next()){
			        	takable = res.getInt("takable");
			        	name = res.getString("name");
			        }
			        
			        if(takable==1){
						writer.println(name+ " has finished exam at "+cal.getTime()+".");
			        }
					
	            }
		        
				writer.println("ExamHallID = " + examHallID + " has officially ended at "+cal.getTime()+".");
				writer.close();
		        				
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		//Send files to server
		private void transferFile(File source, File dest)throws IOException{
			Files.copy(source.toPath(), dest.toPath());
		}
		
		//receive Video File
		private void receiveVideo(){
			BufferedInputStream bis;
			BufferedOutputStream bos;
			byte[] data;
			try{
				Socket socket = new Socket("127.0.0.1", 3000);
				FileOutputStream fos = new FileOutputStream("eProctorServer/ExamRecording/ExamHall="+examHallID+".mov");
				bos = new BufferedOutputStream(fos);
				byte[] buffer = new byte[1024];
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
		
		/*
		 * Terminate Exam
		 */
		//Method to update eventlog for termination
		public void terminateEventLog(String reason, String name){
			try{
				//UPDATE EVENTlOG
				PrintWriter writer= new PrintWriter(new BufferedWriter(new FileWriter("eProctorServer/EventLog/ExamHall=" + examHallID+ ".txt", true)));
	
				//no eventlog exist. Need to create eventlog
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				
	
				writer.println(name+ " is terminated at "+cal.getTime()+". Reason: "+ reason);
				writer.close();
				
				broadcast(getExamHallID());
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
}




