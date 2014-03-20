package eProctor;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.text.*;

public class Server extends Thread{
	int port = 2000;  
	int connectors = 50;
	Socket client;
	DataInputStream in;
	
	String examHallID;
	ArrayList<Session> ExamHallParticipantList;
	
	//Communication Protocol
	private final int CONNECT = 1;
	private final int MSG = 2;
	private final int START = 3;
	
	
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
    private synchronized void startExam(String e) {
    	String examHallID = e;
        for (Session s : ExamHallParticipantList) {
            
        	if(s.getExamHallID().equals(examHallID)){
        		s.writeInt(START);
        	}
        }
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
				DataInputStream  socketIn = new DataInputStream(client.getInputStream());
				DataOutputStream socketOut = new DataOutputStream(client.getOutputStream());
				
				Class.forName(driver);
		        Connection conn = DriverManager.getConnection(url+dbName+username+password);
		        Statement st = conn.createStatement();
				
				while(true){
					int code = socketIn.readInt();
					
					if(code == CONNECT){
						examHallID = socketIn.readUTF();
						userID = socketIn.readInt();
						isStudent = socketIn.readInt();
						
						System.out.println("examhallID=" + examHallID + " userID="+userID);
						
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
						
					}
					else if (code == MSG){
						String msg = socketIn.readUTF();
						
						//get userName
						ResultSet res = st.executeQuery("SELECT user.Name from user where userID='" + userID + "'");
						
						String name="";
						
						while (res.next()){
							name = res.getString("Name");
						}
						//UPDATE EVENTlOG
						PrintWriter writer= new PrintWriter(new BufferedWriter(new FileWriter("eProctorServer/EventLog/ExamHall=" + examHallID+ ".txt", true)));

						//no eventlog exist. Need to create eventlog
						DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						Calendar cal = Calendar.getInstance();
						
	
						writer.println(name+ " ("+cal.getTime()+") says: "+ msg);
						writer.close();
						
						broadcast(getExamHallID());
					}
					else if(code == START){
						startExam(getExamHallID());
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
	}
}



