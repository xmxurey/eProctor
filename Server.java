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
	
	String examHallID;
	ArrayList<Session> ExamHallParticipantList;
	
	//Communication Protocol
	private final int CONNECT = 1;
	private final int MSG = 2;
	private final int START = 3;
	private final int FINISHALL = 4;
	private final int FINISHTIMER = 5;
	
	
	public Server(){
		ExamHallParticipantList = new ArrayList();
	}
	
	public void run() {
		try{
			ServerSocket ss = new ServerSocket(port, connectors);
    		System.out.println("Waiting for client connection .. ");
    		
			while(true){
				client = ss.accept();
				in = new DataInputStream(client.getInputStream());  		
	    		examHallID = in.readUTF();	
	    		Session s = new Session(client, examHallID);	
	    		ExamHallParticipantList.add(s);
	    		System.out.println("Client successfully connected.");
	    		s.start();	
			}
		}
		catch(Exception e){
			System.out.println("Could not listen on port: "+port+".");

		}
    }
	private synchronized void endTimer(String e){
		String examHallID = e;
        for (Session s : ExamHallParticipantList) {
            
        	if(s.getExamHallID().equals(examHallID)){
        		s.writeInt(FINISHTIMER);
        	}
        }
	}
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
    //method to invoke all students to start exam
    private synchronized void startExam(String e) {
    	String examHallID = e;
        for (Session s : ExamHallParticipantList) {
            
        	if(s.getExamHallID().equals(examHallID)){
        		s.writeInt(START);
        	}
        }
    }
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
		private int isStudent = 0;
		
		//check if user can enter examhall
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "cz2006?";
		String driver = "com.mysql.jdbc.Driver";
		String username = "user=root&";
		String password = "password=pass";
		
		
		
		public Session(Socket s, String e){
			client = s;
			examHallID = e;
		}
		
		public String getExamHallID(){
			return examHallID;
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
						examHallID = in.readUTF();
						userID = in.readInt();
						isStudent = in.readInt();
						
						connectExam(examHallID, userID, isStudent);
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
						//update eventlog
					}
					else if(code == FINISHALL){
						//send list of students in examhall back to examhallManager
						ArrayList participantList = new ArrayList();
						participantList = getStudentExamList(getExamHallID());
						
						endTimer(examHallID);
						endEventLog(participantList);
						sendList(participantList);
						
						//transfer files from eProctor Server to NTU Server
						//transfer eventlog
						File eventLogSource = new File("eProctorServer/EventLog/ExamHall=" + examHallID+ ".txt");
						File eventLogDest = new File("NTUServer/EventLog/ExamHall=" + examHallID+ ".txt");
						transferFile(eventLogSource,eventLogDest);
						/*
						//transfer ExamAnswers
						File examAnswerSource = new File("eProctorServer/ExamAnswer/ExamHall=" + examHallID+ ".txt");
						File examAnswerDest = new File("NTUServer/ExamAnswer/ExamHall=" + examHallID+ ".txt");
						sendEventLogFile(eventLogSource,eventLogDest);
						
						//transfer Recording
						File examRecordingSource = new File("eProctorServer/ExamRecording/ExamHall=" + examHallID+ ".mov");
						File examRecordingDest = new File("NTUServer/ExamRecording/ExamHall=" + examHallID+ ".mov");
						sendEventLogFile(examRecordingSource,examRecordingDest);
						*/
					}
				}
				
			}
			catch (IOException e){ 
				System.out.println("IO Error");
			}
	        catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
		/*
		  Enter Exam
		 */
		//Connect to exam
		public void connectExam(String examHallID, int userID, int isStudent){
			try{
				Class.forName(driver);
		        Connection conn = DriverManager.getConnection(url+dbName+username+password);
		        Statement st = conn.createStatement();
				//update database set connect =1
				if(isStudent == 1){
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
	            DataOutputStream sOutput = new DataOutputStream(client.getOutputStream());
	            sOutput.writeInt(code);
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
	            DataOutputStream sOutput = new DataOutputStream(client.getOutputStream());
	            sOutput.writeUTF(msg);
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
		
		
	}
	
}




