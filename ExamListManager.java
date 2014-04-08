package eProctor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ExamListManager {

	public ExamListManager(){
		
	}
	
	//display list of exams student sign up/has to sign up
	public static  JPanel displayExamList(User user){
		//create panel
        JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints b = new GridBagConstraints();
		b.fill = GridBagConstraints.HORIZONTAL;
		//connect to mySQL
		String url = "jdbc:mysql://"+Protocol.serverAddr+":3306/";
	    String dbName = "cz2006?";
	    String driver = "com.mysql.jdbc.Driver";
        String username = "user=root&";
        String password = "password=pass";
        
        //get Rowcount
        int count = 0;

        if(user instanceof Invigilator){
			try {
		        Class.forName(driver);
		        Connection conn = DriverManager.getConnection(url+dbName+username+password);
		        
		        //get user and password
		        Statement st = conn.createStatement();
		        ResultSet res = st.executeQuery("SELECT ExamHall.ExamHallID, examSlot.startTime, examSlot.endTime, Module.ModuleCode, Module.Description  " + 
		        		"FROM User inner join ExamHall on User.userID = ExamHall.invigilatorID " + 
		        		"inner join examSlot on ExamHall.examslotID = examSlot.examslotID " + 
		        		"inner join Module on examSlot.moduleCode = Module.ModuleCode " + 
		        		"Where User.userID = '" + user.getUserID() + "' and examSlot.endTime > now() ");
		        
		        res.last();
		        count= res.getRow();
		        
		        res = st.executeQuery("SELECT ExamHall.ExamHallID, examSlot.startTime, examSlot.endTime, Module.ModuleCode, Module.Description  " + 
		        		"FROM User inner join ExamHall on User.userID = ExamHall.invigilatorID " + 
		        		"inner join examSlot on ExamHall.examslotID = examSlot.examslotID " + 
		        		"inner join Module on examSlot.moduleCode = Module.ModuleCode " + 
		        		"Where User.userID = '" + user.getUserID() + "' and examSlot.endTime > now() ");
		        
		        //course		        
		        String courseCode="";
		        String courseDesc = "";
		        
		        //examslot
		        java.util.Date examStart;
		        java.util.Date examEnd;
		        java.util.Date date;
		        int examHallID=0;
		        
		        int i=0;
		        
		        JLabel lblCourse;
		        JLabel lblExam;
		        //Get result
		        while (res.next()){
		        	//course
		        	courseCode = res.getString("ModuleCode");
		        	courseDesc = res.getString("Description");
		        	lblCourse = new JLabel(courseCode + " " + courseDesc);
		    		b.weightx = 0.5;
		    		b.gridx = 0;
		    		b.gridy = i;
		        	panel.add(lblCourse, b);
		        	
		        	//examslot
		        	examHallID = res.getInt("ExamHallID");
		        	date = res.getDate("StartTime");
		        	examStart = res.getTime("StartTime");
		        	examEnd = res.getTime("EndTime");
		        	lblExam = new JLabel(examHallID + "/ " + date + " " + examStart + " - " + examEnd);
		        			    		
		        	b.weightx = 0.5;
		    		b.gridx = 1;
		    		b.gridy = i;
		            b.weightx = 1;   
		    		panel.add(lblExam, b);
		    		
		    		JLabel lblEmpty = new JLabel();

		        	b.weightx = 0.5;
		    		b.gridx = 2;
		    		b.gridy = i;
		    		panel.add(lblEmpty, b);
		        	i++;
		        }
		        
			}
	        catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
		else if(user instanceof Student){
			try {
		        Class.forName(driver);
		        Connection conn = DriverManager.getConnection(url+dbName+username+password);
		        
		        //get user and password
		        Statement st = conn.createStatement();
		        ResultSet res = st.executeQuery("SELECT Module.ModuleCode, examSlot.startTime, Module.Description, examslot.endtime, ExamHall.ExamHallID " + 
		        		"FROM User inner join moduleattendance on User.userID = moduleattendance.userID " + 
		        		"inner join Module on moduleattendance.moduleCode = module.ModuleCode " + 
		        		"left join ExamHall on moduleattendance.examHallID = ExamHall.examHallID " + 
		        		"left join examSlot on ExamHall.examslotID = ExamSlot.examSlotID " + 
		        		"Where User.userID = '" + user.getUserID() + "' and ModuleAttendance.takable ='1'");

		        res.last();
		        count= res.getRow();
		        
		        res = st.executeQuery("SELECT Module.ModuleCode, examSlot.startTime, Module.Description, examslot.endtime, ExamHall.ExamHallID " + 
		        		"FROM User inner join moduleattendance on User.userID = moduleattendance.userID " + 
		        		"inner join Module on moduleattendance.moduleCode = module.ModuleCode " + 
		        		"left join ExamHall on moduleattendance.examHallID = ExamHall.examHallID " + 
		        		"left join examSlot on ExamHall.examslotID = ExamSlot.examSlotID " + 
		        		"Where User.userID = '" + user.getUserID() + "' and ModuleAttendance.takable ='1'");
		        
		        //course
		        String courseCode="";
		        String courseDesc = "";
		        JLabel lblCourse;
		        
		        //examslot
		        java.util.Date examStart;
		        java.util.Date examEnd;
		        java.util.Date date;
		        int examHallID=0;
		        JLabel lblExam;
		        
		        //button
		        String moduleCode="";
		        
		        int i=0;
		        //Get result
		        while (res.next()){
		        	//course
		        	courseCode = res.getString("ModuleCode");
		        	courseDesc = res.getString("Description");
		        	lblCourse = new JLabel(courseCode + " " + courseDesc);
		    		b.weightx = 0.5;
		    		b.gridx = 0;
		    		b.gridy = i;
		        	panel.add(lblCourse, b);
		        	
		        	//exam
		        	date = res.getDate("StartTime");
		        	if(date == null){
		        		lblExam = new JLabel("Please add a exam timeslot");
		        	}
		        	else{
			        	examHallID = res.getInt("ExamHallID");
			        	examStart = res.getTime("StartTime");
			        	examEnd = res.getTime("EndTime");
			        	lblExam = new JLabel(examHallID + "/ " + date + " " + examStart + " - " + examEnd);
		        	}
		    		b.weightx = 0.5;
		    		b.gridx = 1;
		    		b.gridy = i;
		        	panel.add(lblExam, b);
		        	
		        	//button
		        	moduleCode = res.getString("ModuleCode");
		        	
		        	JPanel pButton = new JPanel(new GridBagLayout());
		    		GridBagConstraints b1 = new GridBagConstraints();
		    		b1.fill = GridBagConstraints.HORIZONTAL;
		    		
		        	if (date == null){//student havent select exam slot
		        		FunctionButton btnAdd = new FunctionButton("Add", moduleCode);
		        		b1.weightx = 0.5;
		        		b1.gridx = 0;
		        		b1.gridy = i;
		        		pButton.add(btnAdd, b1);
		        	}
		        	else{
		        		FunctionButton btnEdit = new FunctionButton("Edit", moduleCode);
		        		b1.weightx = 0.5;
		        		b1.gridx = 0;
		        		b1.gridy = i;
		        		pButton.add(btnEdit, b1);
		        		FunctionButton btnDelete = new FunctionButton("Delete", moduleCode);
		        		b1.weightx = 0.5;
		        		b1.gridx = 1;
		        		b1.gridy = i;
		        		pButton.add(btnDelete, b1);
		        	}

	        		b.weightx = 0.5;
	        		b.gridx = 3;
	        		b.gridy = i;
		        	panel.add(pButton, b);
		        	
		        	
		        	i++;
		        }

		        conn.close();
			}
	        catch (Exception e) {
	        	e.printStackTrace();
	        }
			
		}

		return panel;
	}
	
	//Needed for enter exam of UIMenu
	public static JPanel displayLatestExam(User user){
		JPanel panel = new JPanel(new GridLayout(5, 1));
		//connect to mySQL
		String url = "jdbc:mysql://"+Protocol.serverAddr+":3306/";
	    String dbName = "cz2006?";
	    String driver = "com.mysql.jdbc.Driver";
        String username = "user=root&";
        String password = "password=pass";
        
        if(user instanceof Invigilator){
			try {
		        Class.forName(driver);
		        Connection conn = DriverManager.getConnection(url+dbName+username+password);
		        
		        //get user and password
		        Statement st = conn.createStatement();
		        ResultSet res = st.executeQuery("SELECT ExamHall.examHallID, Module.ModuleCode, Module.Description, examSlot.startTime " + 
		        		"FROM User inner join ExamHall on User.userID = ExamHall.invigilatorID " + 
		        		"inner join examSlot on ExamHall.examslotID = examSlot.examslotID " + 
		        		"inner join Module on examSlot.moduleCode = Module.ModuleCode " + 
		        		"Where User.userID = '" + user.getUserID() + "' and examslot.endTime > now() " + 
		        		"ORDER BY examSlot.startTime ASC " + 
		        		"LIMIT 1");
		        
		        JLabel lblExamHallID, lblModuleCode, lblModuleDesc, lblDate, lblTime;
		        java.util.Date date;
		        //Get result
		        
		        lblModuleCode = new JLabel("--");
		        lblModuleDesc = new JLabel("--");
		        lblExamHallID = new JLabel("--");
		        lblDate = new JLabel("--");
		        lblTime = new JLabel("--");
		        while(res.next()){
		        	lblModuleCode.setText(res.getString("ModuleCode"));
		        	lblModuleDesc.setText(res.getString("Description"));
		        	date = res.getDate("startTime");
		        	
		        	lblExamHallID.setText(res.getString("examHallID"));
	        		lblDate.setText(""+date);
	        		lblTime.setText(""+res.getTime("startTime"));
		        }
		        panel.add(lblExamHallID);
	        	panel.add(lblModuleCode);
	        	panel.add(lblModuleDesc);
	        	panel.add(lblDate);
	        	panel.add(lblTime);
			}
	        catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
		else if(user instanceof Student){
			try {
		        Class.forName(driver);
		        Connection conn = DriverManager.getConnection(url+dbName+username+password);
		        
		        //get user and password
		        Statement st = conn.createStatement();

		        ResultSet res = st.executeQuery("SELECT ExamHall.examHallID, Module.ModuleCode, Module.Description, examSlot.startTime, examslot.endtime " + 
		        		"FROM User inner join moduleattendance on User.userID = moduleattendance.userID " + 
		        		"inner join Module on moduleattendance.moduleCode = module.ModuleCode " + 
		        		"inner join ExamHall on moduleattendance.examHallID = ExamHall.examHallID " + 
		        		"inner join examSlot on ExamHall.examslotID = ExamSlot.examSlotID " + 
		        		"Where User.userID = '" + user.getUserID() + "' and moduleAttendance.takable = '1' " + 
		        		"ORDER BY examSlot.startTime ASC " + 
		        		"LIMIT 1");
		        
		        JLabel lblExamHallID, lblModuleCode, lblModuleDesc, lblDate, lblTime;
		        java.util.Date date;
		        //Get result
		        
		        lblModuleCode = new JLabel("--");
		        lblModuleDesc = new JLabel("--");
		        lblExamHallID = new JLabel("--");
		        lblDate = new JLabel("--");
		        lblTime = new JLabel("--");
		        while(res.next()){
		        	lblModuleCode.setText(res.getString("ModuleCode"));
		        	lblModuleDesc.setText(res.getString("Description"));
		        	date = res.getDate("startTime");
		        	
		        	lblExamHallID.setText(res.getString("examHallID"));
	        		lblDate.setText(""+date);
	        		lblTime.setText(""+res.getTime("startTime"));
		        }
		        panel.add(lblExamHallID);
	        	panel.add(lblModuleCode);
	        	panel.add(lblModuleDesc);
	        	panel.add(lblDate);
	        	panel.add(lblTime);
			}
	        catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
        return panel;
	}
	
	//return list of examslots and related examhalls
	public ArrayList getExamSlotList(String moduleCode){
		ArrayList examSlotList = new ArrayList();
		
		String url = "jdbc:mysql://"+Protocol.serverAddr+":3306/";
        String dbName = "cz2006?";
        String driver = "com.mysql.jdbc.Driver";
        String username = "user=root&";
        String password = "password=pass";		
        
        try {
	        Class.forName(driver);
	        Connection conn = DriverManager.getConnection(url+dbName+username+password);
	        
	        //get user and password
	        Statement st = conn.createStatement();
	        ResultSet res = st.executeQuery("SELECT examHall.examSlotID, examHall.examHallID, examSlot.startTime, examSlot.EndTime, examHall.SlotsAvailable " + 
	        		"FROM examHall inner join ExamSlot on examHall.examSlotID = examSlot.examSlotID "+
	        		"inner join Module on examSlot.ModuleCode = Module.moduleCode " + 
	        		"WHERE examSlot.startTime > now() and examHall.slotsAvailable > 0 and " + 
	        		"Module.ModuleCode = '" + moduleCode + "'");
	        		
	        String result="";
	        String examHallID="";
	        java.util.Date date;
	        java.util.Date startTime;
	        java.util.Date endTime;
	        int slots=0;
	        
	        
	        while (res.next()) {
	            examHallID = res.getString("examHallID");
	            date = res.getDate("startTime");
	            startTime = res.getTime("startTime");
	            endTime = res.getTime("endTime");
	            slots = res.getInt("SlotsAvailable");
	            
	            result = " " + date + " " + startTime + "-" + endTime + " " + slots;
	            examSlotList.add(examHallID);
	            examSlotList.add(result);
	            
	        }
	        
	        conn.close();
        } 
        catch (Exception e) {
        	e.printStackTrace();
        }
		return examSlotList;
	
	}
	
	//Add/Edit examslot
	public boolean addEditExamSlot(User user, String moduleCode, String examHallID, 
			 Date startTime, Date endTime){
		//check if function is add/edit. Edit = student has been allocated examhall
		int function = 0;
		
		if(user instanceof Student){
			//check if authenticate
			String url = "jdbc:mysql://"+Protocol.serverAddr+":3306/";
		    String dbName = "cz2006?";
		    String driver = "com.mysql.jdbc.Driver";
		    String username = "user=root&";
		    String password = "password=pass";
			
		    int eExamHallID=0;
		    int moduleAttendance = 0;
		    int eExamSlotID=0;
		    int eExamSlotAvail = 0;
		    int eExamHallAvail = 0;
		    
		    try {
		        Class.forName(driver);
		        Connection conn = DriverManager.getConnection(url+dbName+username+password);
		        Statement st = conn.createStatement();
		        ResultSet res = st.executeQuery("select ModuleAttendance.ModuleAttendanceID, ModuleAttendance.ExamHallID "+
		        						"FROM moduleattendance inner join Module on moduleAttendance.moduleCode = Module.moduleCode "+
		        						"inner join ExamSlot on ExamSlot.moduleCode = Module.moduleCode "+
		        						"WHERE moduleAttendance.userID='" + user.getUserID() +"' and moduleAttendance.moduleCode='" 
		        						+ moduleCode +"' and examslot.startTime > now()");
		        
		        while(res.next()){
		        	eExamHallID = res.getInt("ExamHallID");
		        	moduleAttendance = res.getInt("moduleAttendanceID");
			       
		        	System.out.println(eExamHallID);
		        	if(eExamHallID == 0)
		        		function = 1;
		        	else if (eExamHallID >0)
		        		function = 2;
		        	else
		        		function = 0;
		        }
		        
		        //Adding Process
		        if(function == 1){
		        	if(testAddAndEditConflict(user.getUserID(), startTime, endTime, moduleCode)){
		        	    //update module attendance examhallID
		        	    st.execute("UPDATE ModuleAttendance inner join Module on ModuleAttendance.moduleCode = Module.moduleCode " + 
					        "inner join examSlot on examSlot.moduleCode = Module.moduleCode " + 
					        "SET ModuleAttendance.ExamHallID = '"+ examHallID +"' " + 
					        "WHERE ModuleAttendance.ModuleAttendanceID = '" + moduleAttendance + "'");
					        
		        	    //reduce examhall slot by 1
		        	    res = st.executeQuery("Select ExamHall.examSlotID, examSlot.slotsAvailable as 'examSlotAvail', examHall.slotsAvailable as 'examHallAvail' " + 
		        			 "FROM ExamHall inner join examSlot on ExamHall.examSlotID = examSlot.examSlotID " +
		        			 "WHERE ExamHall.examHallID = '"+ examHallID +"'");
		        	
		        	    int examSlotAvail = 0;
		        	    int examHallAvail = 0;
		        	    int examSlotID = 0;

		        	    while(res.next()){
		        		    examSlotAvail = res.getInt("examSlotAvail");
		        		    examHallAvail = res.getInt("examHallAvail");
		        		    examSlotID = res.getInt("examSlotID");
		        	    }

		        	    st.execute("UPDATE examhall SET slotsAvailable = '" + (examHallAvail-1) + "' WHERE ExamHallID = '" + examHallID + "'");
		        	    //reduce examslot slot by 1
		        	    st.execute("UPDATE examSlot SET slotsAvailable = '" + (examSlotAvail-1) + "' WHERE examSlotID = '" + examSlotID + "'");
		        	    conn.close();
		        	    return true;
		            }
		        	else{
		        	    JOptionPane.showMessageDialog(null, "Sorry, the new exam time slot is "
		        	    		+ "conflict with another one!");
		        	    return false;
		        	}
		        }
		        else{
		        	if(testAddAndEditConflict(user.getUserID(),  startTime, endTime, moduleCode)){ // edit
		               //update module attendance examhallID
	        		    res = st.executeQuery("Select ExamHall.examSlotID, examSlot.slotsAvailable as 'examSlotAvail', examHall.slotsAvailable as 'examHallAvail' " + 
		        					"FROM ExamHall inner join examSlot on ExamHall.examSlotID = examSlot.examSlotID " +
		        					"WHERE ExamHall.examHallID = '"+ eExamHallID +"'");

	        		    while(res.next()){
		        		     eExamSlotID = res.getInt("examSlotID");
		        		     eExamSlotAvail = res.getInt("examSlotAvail");
		        		     eExamHallAvail = res.getInt("examHallAvail");
	        		    }
	        		  
		        	
		        	     st.execute("UPDATE ModuleAttendance inner join Module on ModuleAttendance.moduleCode = Module.moduleCode " + 
					           "inner join examSlot on examSlot.moduleCode = Module.moduleCode " + 
					           "SET ModuleAttendance.ExamHallID = '"+ examHallID +"' " + 
					           "WHERE ModuleAttendance.ModuleAttendanceID = '" + moduleAttendance + "'");
			        
			        
		        	     //reduce examhall slot by 1
		        	     res = st.executeQuery("Select ExamHall.examSlotID, examSlot.slotsAvailable as 'examSlotAvail', examHall.slotsAvailable as 'examHallAvail' " + 
		        			   "FROM ExamHall inner join examSlot on ExamHall.examSlotID = examSlot.examSlotID " +
		        			   "WHERE ExamHall.examHallID = '"+ examHallID +"'");
		        	
		        	     int examSlotAvail = 0;
		        	     int examHallAvail = 0;
		               	 int examSlotID = 0;
		        	     while(res.next()){
		        		      examSlotAvail = res.getInt("examSlotAvail");
		        		      examHallAvail = res.getInt("examHallAvail");
		        		      examSlotID = res.getInt("examSlotID");
		        	     }
		        	
		        	     st.execute("UPDATE examhall SET slotsAvailable = '" + (eExamHallAvail+1) + "' WHERE ExamHallID = '" + eExamHallID + "'");
		        	     st.execute("UPDATE examhall SET slotsAvailable = '" + (examHallAvail-1) + "' WHERE ExamHallID = '" + examHallID + "'");
		        	     //examslot slots available has no change.
		        	     conn.close();
		        	     return true;
    		      }	 
		         else{
		        	 JOptionPane.showMessageDialog(null, "You can not change to new exam slot because"
		        	 		+ " the new exam slot conflict with the some of the existing one(except the one "
		        	 		+ "you want to change)!");
		        	 return false;
		         }
		       }
		    }
		    catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
        return false;
}
	
//test whether a new exam slot can be add or not
//test whether a exam slot can be change to another under the same course
public boolean testAddAndEditConflict(int userID, Date startTime, Date endTime, 
		String moduleCode){
		
		   String url = "jdbc:mysql://"+Protocol.serverAddr+":3306/";
		   String dbName = "cz2006?";
		   String driver = "com.mysql.jdbc.Driver";
		   String username = "user=root&";
		   String password = "password=pass";
		   Date slotStartTime;
		   Date slotEndTime;
		   long upperBound, lowerBound, i, upperBound1, lowerBound1;
		   
		   ResultSet res = null;
		   
		   try {
			   //select exisiting exam time slot from the database
		        Class.forName(driver);
		        Connection conn = DriverManager.getConnection(url+dbName+username+password);
		        Statement st = conn.createStatement();
		        res = st.executeQuery("select examSlot.examSlotID, examSlot.startTime, examslot.endTime "
		            + " from user inner join moduleattendance on user.UserID " +
		            " = moduleattendance.UserID inner join examhall on moduleattendance.examHallID " +
		            "= examhall.examHallID inner join examslot on examHall.examSlotID "
		            + " = examslot.examSlotID where user.userid = " + userID +  
		            " and moduleattendance.examHallID != 0 and examSlot.moduleCode != "
		                	    + "'" + moduleCode + "'");
		         
		        //check whether they have overlap
		         while(res.next()){
	                 slotStartTime = res.getTimestamp("examslot.startTime");
		             slotEndTime = res.getTimestamp("examslot.endTime");
		             lowerBound = slotStartTime.getTime();
		             upperBound = slotEndTime.getTime();
		             upperBound = upperBound/(60*1000)+15;
		             lowerBound = lowerBound/(60*1000)-15;
		             upperBound1 = endTime.getTime()/(60*1000);
		             lowerBound1 = startTime.getTime()/(60*1000);
		             i = lowerBound1;
		             while(i <= upperBound1){
			                     if((i>=lowerBound)&&(i<=upperBound))
			            	          return false;
			                 i++;
			         }
	           }
		 }
		 catch(Exception e){
			 e.printStackTrace();
		 } 
		 return true;  
   }
	
	
	public static boolean deleteExamSlot(User user,String m){
		
		String url = "jdbc:mysql://"+Protocol.serverAddr+":3306/";
        String dbName = "cz2006?";
        String driver = "com.mysql.jdbc.Driver";
        String username = "user=root&";
        String password = "password=pass";
        
        try {
	        Class.forName(driver);
	        Connection conn = DriverManager.getConnection(url+dbName+username+password);
	        
	        //get exam slot registered
	        Statement st = conn.createStatement();
	        ResultSet res = st.executeQuery("SELECT examSlot.examslotID, moduleAttendance.examHallID, ExamHall.slotsAvailable as 'examHallAvail', examSlot.slotsAvailable as 'examSlotAvail' " +
	        "FROM examHall inner join examslot on examHall.examslotID = examslot.examSlotID " +
	        "inner join moduleAttendance on moduleAttendance.examHallID = examHall.examHallID " + 
	        "WHERE moduleAttendance.userID = '"+ user.getUserID() +"' and moduleAttendance.moduleCode = '"+ m +"' and moduleAttendance.examHallID > 0 " + 
	        "and examSlot.startTime > now()");
	        
	       int examHallAvail = 0;
	       int examSlotAvail = 0;
	       int examHallID = 0;
	       int examSlotID = 0;
	        
	        while (res.next()) {
	        	examHallID = res.getInt("examHallID");
	        	examSlotID = res.getInt("examSlotID");
	            examHallAvail = res.getInt("examHallAvail");
	            examSlotAvail = res.getInt("examSlotAvail");
	        }
	        //delete process
	        if(examHallID != 0){
	             //update moduleAttendance
	             st.execute("UPDATE ModuleAttendance inner join Module on ModuleAttendance.moduleCode = Module.moduleCode " + 
	             "inner join examSlot on examSlot.moduleCode = Module.moduleCode " + 
	             "SET ModuleAttendance.ExamHallID = '0' " + 
	             "WHERE ModuleAttendance.userID = '" + user.getUserID() + "' and ModuleAttendance.moduleCode = '" + m + "' and examslot.startTime > now()");
        	
	             //add examhall
	             st.execute("UPDATE examhall SET slotsAvailable = '" + (examHallAvail+1) + "' WHERE ExamHallID = '" + examHallID + "'");
        	
	             //add examslot
        	     st.execute("UPDATE examSlot SET slotsAvailable = '" + (examSlotAvail+1) + "' WHERE examSlotID = '" + examSlotID + "'");
        	     JOptionPane.showMessageDialog(null, "Delete successfully! Please refresh!");
        	     return true;
            }
	        else{
	        	JOptionPane.showMessageDialog(null, "Sorry, this exam does not exist! Nothing to delete!");
	        	return false; 
	       }
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
	}
}
