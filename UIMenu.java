package eProctor;

import java.util.*;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.io.*;
import java.net.*;

public class UIMenu extends JFrame implements ActionListener {
	
	private JTabbedPane tabPane;
	private JPanel tabManage, tabExam;
	private JPanel pNorth, pNLeft, pNCenter, pNRight;
	private JLabel lblName, lblPlace;
	private JLabel lblName2, lblPlace2;
	private JLabel lblMsg;
	private JButton btnRefresh, btnLogout;
	private User user;
	private Socket client;
	
	//manager list
	private ExamListManager examListMgr = new ExamListManager();
	private ExamHallManager examHallMgr = new ExamHallManager();
	
	public UIMenu(){
		
	}
	public UIMenu(User u){
		user = u;
		Container c = getContentPane();
		
		//pNorth Creation
		//Creating the North Panel
		pNorth = new JPanel(new BorderLayout());
				
		pNLeft = new JPanel(new GridLayout(2,1));
		lblName = new JLabel("Name:");
		
		if(user instanceof Student){
			lblPlace = new JLabel("Course:");
		}
		else{
			lblPlace = new JLabel("Faculty:");			
		}

		pNLeft.add(lblName);
		pNLeft.add(lblPlace);

		pNCenter = new JPanel(new GridLayout(2,1));
		lblName2 = new JLabel(user.getName());
		if(user instanceof Invigilator)
			lblPlace2 = new JLabel(((Invigilator) user).getFaculty());
		else
			lblPlace2 = new JLabel(((Student) user).getCourse());
		
		pNCenter.add(lblName2);
		pNCenter.add(lblPlace2);
				
		pNRight = new JPanel(new GridBagLayout());
		GridBagConstraints b = new GridBagConstraints();
		b.fill = GridBagConstraints.HORIZONTAL;
		
		btnRefresh = new JButton("Refresh");
		b.weightx = 0.5;
		b.gridx = 0;
		b.gridy = 0;
		btnRefresh.addActionListener(this);
		pNRight.add(btnRefresh, b);
		
		btnLogout = new JButton("Logout");
		b.weightx = 0.5;
		b.gridx = 1;
		b.gridy = 0;
		btnLogout.addActionListener(this);
		pNRight.add(btnLogout, b);
		
		pNorth.add(pNLeft, BorderLayout.WEST);
		pNorth.add(pNCenter, BorderLayout.CENTER);
		pNorth.add(pNRight, BorderLayout.EAST);
		
		//tab panel creation
		tabPane = new JTabbedPane();
		tabManage = createTab1();
		tabExam = createTab2();
				
		tabPane.addTab("Manage Exam", tabManage);
		tabPane.addTab("Enter Exam", tabExam);

		tabPane.setSelectedIndex(0);
		
		c.add(pNorth, BorderLayout.NORTH);
		c.add(tabPane,BorderLayout.CENTER);  
		
	}
	
	public JPanel createTab1() {
		JPanel jplPanel = new JPanel(new BorderLayout());
		JPanel pNorth, pCenter;
		JPanel pButton;
		
		JLabel lblModuleReg, lblExamSlot, lblFunction;
		JLabel lblModuleReg2, lblExamSlot2;
		
		//Create pNorth 
		pNorth = new JPanel(new GridLayout(1,3));
		lblModuleReg = new JLabel("Module Registered");
		lblExamSlot = new JLabel("ExamHall ID/Exam Slot Timing");
		lblFunction = new JLabel("");
		
		pNorth.add(lblModuleReg);
		pNorth.add(lblExamSlot);
		pNorth.add(lblFunction);
		
		//Create pCenter
		pCenter = examListMgr.displayExamList(user);
		//adding action listener
		Component[] componentList = pCenter.getComponents();
		for(Component com : componentList) {
			if(com instanceof JPanel){
				JPanel btnComponent = (JPanel)com;
				Component[] componentList2 = btnComponent.getComponents();
				
				//go through all panels
				for(Component com2 : componentList2) {
					//check for buttons
					if(com2 instanceof FunctionButton){
						FunctionButton btnFunction = (FunctionButton)com2;
						btnFunction.addActionListener(this);
					}
				}
				
			}
		}
		
		
		//Adding to jplPanel
		jplPanel.add(pNorth, BorderLayout.NORTH);
		pCenter.setAlignmentY(TOP_ALIGNMENT);
		jplPanel.add(pCenter, BorderLayout.CENTER);
		return jplPanel;
	}
	
	public JPanel createTab2() {
		JPanel jplPanel = new JPanel(new BorderLayout());
		
		JPanel pNorth, pNLeft, pNRight, pCenter;
		JLabel lblExamHallID, lblCourseCode, lblCourseName, lblExamDate, lblExamTime;
		JButton btnEnter;
		
		//Creating the North Panel
		pNorth = new JPanel(new BorderLayout());
		
		pNLeft = new JPanel(new GridLayout(5,1));
		lblExamHallID = new JLabel("Exam Hall ID:");
		lblCourseCode = new JLabel("Module Code:");
		lblCourseName = new JLabel("Module Name:");
		lblExamDate = new JLabel("Exam Date:");
		lblExamTime = new JLabel("Exam Time:");

		pNLeft.add(lblExamHallID);
		pNLeft.add(lblCourseCode);
		pNLeft.add(lblCourseName);
		pNLeft.add(lblExamDate);
		pNLeft.add(lblExamTime);
		
		pNRight = examListMgr.displayLatestExam(user);
		pNorth.add(pNLeft, BorderLayout.WEST);
		pNorth.add(pNRight, BorderLayout.CENTER);
		
		
		//Creating the Center Panel
		pCenter = new JPanel();
		btnEnter = new JButton("ENTER");
		btnEnter.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnEnter.setAlignmentY(Component.CENTER_ALIGNMENT);
		btnEnter.setPreferredSize(new Dimension(150,75));
		btnEnter.addActionListener(this);
		
		Component[] componentList = pNRight.getComponents();
		for(Component com : componentList) {
			if(com instanceof JLabel){
				JLabel lblExam = (JLabel)com;

				if (lblExam.getText().equals("--")){
					btnEnter.setEnabled(false);
				}
			}
		}
		
		pCenter.add(btnEnter, BorderLayout.CENTER);
		
		//Creating south panel
		lblMsg = new JLabel("");
				
		//Adding to jplPanel
		jplPanel.add(pNorth,BorderLayout.NORTH);
		jplPanel.add(pCenter,BorderLayout.CENTER);
		jplPanel.add(lblMsg,BorderLayout.SOUTH);
		return jplPanel;
	}	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() instanceof FunctionButton){
			FunctionButton btnFunction = (FunctionButton)e.getSource();
			if(btnFunction.getText() == "Delete"){
				ExamHall examHall = new ExamHall();
				boolean success = false;
				success = examListMgr.deleteExamSlot(user, btnFunction.getModuleCode());
				
				if(success){
					System.out.println("Update/Add Success");
					btnFunction.setEnabled(false);
				}
				else
					System.out.println("Update/Add fail");
			}
			else if(btnFunction.getText() == "Add" || btnFunction.getText() == "Edit"){
				UIExamSlot uiExamSlot = new UIExamSlot(btnFunction.getModuleCode(), user);
				uiExamSlot.setTitle("eProctor - Add/Edit Exam Slot");
				uiExamSlot.setSize(450,200);
				uiExamSlot.setVisible(true);
				uiExamSlot.setResizable(false);
			}
		}
		else if(e.getSource() == btnLogout){
			user = null;
			UILogin uiLogin = new UILogin();
			uiLogin.setTitle("Log in");
			uiLogin.setSize(800,600);
			uiLogin.setVisible(true);
			uiLogin.setResizable(false);
			
			//close window
			this.dispose();
		}
		
		else if(e.getSource() == btnRefresh){
			UIMenu uimenu = new UIMenu(user);
			uimenu.setTitle("eProctor");
			uimenu.setSize(800,600);
			uimenu.setVisible(true);
			uimenu.setResizable(false);
			uimenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.dispose();
			
		}
		else if(e.getSource() instanceof JButton){
			JButton btnFunction = (JButton)e.getSource();
			
			if (btnFunction.getText().equals("ENTER")){
				//btnEnter
				boolean allow= false;
				ExamHall examHall = null;
				
				//get examHallID
				Component[] componentList = tabExam.getComponents();
				JLabel lblText;
				JPanel pNorth, pnNorth;
				pNorth = (JPanel)componentList[0];
				Component[] componentList2 = pNorth.getComponents();
				pnNorth = (JPanel)componentList2[1];

				Component[] componentList3 = pnNorth.getComponents();
				lblText = (JLabel)componentList3[0];
				
				//check if user can enter exam
				examHall = examHallMgr.enterExamHall(user, lblText.getText());
				if(examHall == null){
					lblMsg.setText("Access Denied");
					lblMsg.setForeground(Color.red);
				}
				else{
					//times up. access allowed
					//connect to server
					client = examHallMgr.connectExamHall(examHall, user);
					if(client == null){
						System.out.println("Connection fail");
						lblMsg.setText("Connection Fail");
						lblMsg.setForeground(Color.red);
					}
					else{
						//launch UI for student. e.g UIStudent uiStudent = new UIStudent(User user, Socket client)
						//each UIStudent will be able to determine which socket it belongs to.
						if(user instanceof Student){
							UIStudent uiStudent = new UIStudent(user, client, examHall);
							uiStudent.setTitle("Student Exam");
							uiStudent.setSize(800,600);
							uiStudent.setVisible(true);
							uiStudent.setResizable(false);
							
						}
						else{
							UIInvigilator uiInvigilator = new UIInvigilator(user, client, examHall);
							uiInvigilator.setTitle("Invigilator Exam");
							uiInvigilator.setSize(800,600);
							uiInvigilator.setVisible(true);
							uiInvigilator.setResizable(false);
							
						}
					}
				}
			}

		}
	}
	
	public static void main(String[] args){
		User user = new Student(2, "testpass", "test@test.com", "Tom", "U1113333", "1");
		UIMenu uimenu = new UIMenu(user);
		uimenu.setTitle("eProctor");
		uimenu.setSize(800,600);
		uimenu.setVisible(true);
		uimenu.setResizable(false);
		uimenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
