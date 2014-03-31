package eProctor;

import java.util.*;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.io.*;
import java.net.*;

public class UIMenu extends JFrame implements ActionListener {
	
	public static int screenWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	public static int screenHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	
	private JTabbedPane tabPane;
	private JPanel tabManage, tabExam;
	private JPanel pNorth, pNLeft, pNCenter, pNRight;
	private JLabel lblName, lblPlace;
	private JLabel lblName2, lblPlace2;
	private JLabel lblMsg, lblBackground;
	private JButton btnRefresh, btnLogout;
	private JLayeredPane layeredPane;
	private ImageIcon background = new ImageIcon("Images/Menubg.jpg");
	private ImageIcon refreshButton = new ImageIcon("Images/refresh1.png");
	private ImageIcon logoutButton = new ImageIcon("Images/logout1.png");
	
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
		
		//layeredPane settings
    	layeredPane = new JLayeredPane();
    	layeredPane.setPreferredSize(new Dimension(800, 600));
    	lblBackground = new JLabel(background);
    	lblBackground.setOpaque(true); 
        lblBackground.setBounds(0,0,800,600); 
        layeredPane.add(lblBackground, new Integer(0));
		
		//pNorth Creation
		//Creating the North Panel
		pNorth = new JPanel(new BorderLayout());
		pNorth.setBounds(0,0,800,80);
				
		pNLeft = new JPanel(new GridLayout(2,1));
		lblName = new JLabel("Name:");
		lblName.setForeground(Color.white);
		
		if(user instanceof Student){
			lblPlace = new JLabel("Course:");
			lblPlace.setForeground(Color.white);
		}
		else{
			lblPlace = new JLabel("Faculty:");	
			lblPlace.setForeground(Color.white);
		}

		pNLeft.add(lblName);
		pNLeft.add(lblPlace);

		pNCenter = new JPanel(new GridLayout(2,1));
		lblName2 = new JLabel(user.getName());
		lblName2.setForeground(Color.white);
		if(user instanceof Invigilator) {
			lblPlace2 = new JLabel(((Invigilator) user).getFaculty());
			lblPlace2.setForeground(Color.white);
		}
		else {
			lblPlace2 = new JLabel(((Student) user).getCourse());
			lblPlace2.setForeground(Color.white);
		}
		
		pNCenter.add(lblName2);
		pNCenter.add(lblPlace2);
				
		pNRight = new JPanel(new GridBagLayout());
		GridBagConstraints b = new GridBagConstraints();
		b.fill = GridBagConstraints.HORIZONTAL;
		pNRight.setLayout(new GridLayout(1, 3, 10, 10));
		
		btnRefresh = new JButton(refreshButton);
		b.weightx = 0.5;
		b.gridx = 0;
		b.gridy = 0;
		btnRefresh.addActionListener(this);
		btnRefresh.setContentAreaFilled(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder());
        btnRefresh.setRolloverIcon(new ImageIcon("Images/refresh2.png"));
        btnRefresh.setPressedIcon(new ImageIcon("Images/refresh3.png"));
		pNRight.add(btnRefresh, b);
		
		btnLogout = new JButton(logoutButton);
		b.weightx = 0.5;
		b.gridx = 1;
		b.gridy = 0;
		b.insets = new Insets(0,0,0,100);
		btnLogout.addActionListener(this);
		btnLogout.addActionListener(this);
		btnLogout.setContentAreaFilled(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createEmptyBorder());
        btnLogout.setRolloverIcon(new ImageIcon("Images/logout2.png"));
        btnLogout.setPressedIcon(new ImageIcon("Images/logout3.png"));
		pNRight.add(btnLogout, b);
		
		pNorth.add(pNLeft, BorderLayout.WEST);
		pNorth.add(pNCenter, BorderLayout.CENTER);
		pNorth.add(pNRight, BorderLayout.EAST);
		
		//tab panel creation
		UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
		UIManager.put("TabbedPane.tabsOpaque", Boolean.FALSE);
		tabPane = new JTabbedPane();
		tabPane.setOpaque(false);
		
		tabManage = createTab1();
		tabExam = createTab2();
		tabExam.setOpaque(false);
		
		tabPane.addTab("Manage Exam", tabManage);
		tabManage.setForeground(Color.white);
		tabPane.addTab("Enter Exam", tabExam);

		tabPane.setSelectedIndex(0);
		tabPane.setBounds(0,81,800,519);
		tabPane.setOpaque(false);

		pNLeft.setOpaque(false);
		pNCenter.setOpaque(false);
		pNRight.setOpaque(false);
		pNorth.setOpaque(false);
		
		
		layeredPane.add(pNorth, new Integer(1));
		layeredPane.add(tabPane, new Integer(2));
		
		c.add(layeredPane);
		
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
		lblModuleReg.setFont(new Font("Serif", Font.BOLD, 14));
		lblExamSlot = new JLabel("ExamHall ID/Exam Slot Timing");
		lblExamSlot.setFont(new Font("Serif", Font.BOLD, 14));
		lblFunction = new JLabel("");
		
		pNorth.add(lblModuleReg, new Integer(1));
		pNorth.add(lblExamSlot, new Integer(1));
		pNorth.add(lblFunction, new Integer(1));
		lblModuleReg.setOpaque(false);
		pNorth.setOpaque(false);
		//pNorth.add(lblModuleReg);
		//pNorth.add(lblExamSlot);
		//pNorth.add(lblFunction);
		
		layeredPane.add(pNorth, new Integer(1));
		UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
		
		//Create pCenter
		pCenter = examListMgr.displayExamList(user);
		pCenter.setBounds(0, 81, 800, 519);
		pCenter.setOpaque(false);
		layeredPane.add(pCenter, new Integer(2));
		
		
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
		jplPanel.setOpaque(false);
		return jplPanel;
	}
	
	public JPanel createTab2() {
		JPanel jplPanel = new JPanel(new BorderLayout());
		
		JPanel pNorth, pNLeft, pNRight, pCenter;
		JLabel lblExamHallID, lblCourseCode, lblCourseName, lblExamDate, lblExamTime;
		JButton btnEnter;
		ImageIcon enterButton = new ImageIcon("Images/enterr1.png");
		
		//Creating the North Panel
		pNorth = new JPanel(new BorderLayout());
		layeredPane.add(pNorth, new Integer(2));
		
		pNLeft = new JPanel(new GridLayout(5,1));
		lblExamHallID = new JLabel("Exam Hall ID:");
		lblExamHallID.setFont(new Font("Serif", Font.BOLD, 14));
		lblCourseCode = new JLabel("Module Code:");
		lblCourseCode.setFont(new Font("Serif", Font.BOLD, 14));
		lblCourseName = new JLabel("Module Name:");
		lblCourseName.setFont(new Font("Serif", Font.BOLD, 14));
		lblExamDate = new JLabel("Exam Date:");
		lblExamDate.setFont(new Font("Serif", Font.BOLD, 14));
		lblExamTime = new JLabel("Exam Time:");
		lblExamTime.setFont(new Font("Serif", Font.BOLD, 14));
		
		pNLeft.add(lblExamHallID, new Integer(2));
		pNLeft.add(lblCourseCode, new Integer(2));
		pNLeft.add(lblCourseName, new Integer(2));
		pNLeft.add(lblExamDate, new Integer(2));
		pNLeft.add(lblExamTime, new Integer(2));
		layeredPane.add(pNLeft, new Integer(2));
		
		pNLeft.setOpaque(false);
		pNorth.setOpaque(false);
				
		pNRight = examListMgr.displayLatestExam(user);
		pNorth.add(pNLeft, BorderLayout.WEST);
		pNorth.add(pNRight, BorderLayout.CENTER);

		pNRight.setOpaque(false);
		
		//Creating the Center Panel
		pCenter = new JPanel();
		pCenter.setOpaque(false);
		layeredPane.add(pCenter, new Integer(2));
		btnEnter = new JButton(enterButton);
		btnEnter.setContentAreaFilled(false);
        btnEnter.setFocusPainted(false);
        btnEnter.setBorder(BorderFactory.createEmptyBorder());
        btnEnter.setRolloverIcon(new ImageIcon("Images/enterr2.png"));
        btnEnter.setPressedIcon(new ImageIcon("Images/enterr3.png"));
		btnEnter.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnEnter.setAlignmentY(Component.CENTER_ALIGNMENT);
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
		jplPanel.setOpaque(false);
		return jplPanel;
	}	
	public void actionPerformed(ActionEvent e){
		ImageIcon enterButton = new ImageIcon("Images/enterr1.png");
		
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
			String btnImage = btnFunction.getIcon().toString();

			if (btnImage.equals(enterButton.toString())){
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
						JOptionPane.showMessageDialog(null,
                			    "Connection Fail: Invigilator has not entered / You are logged in.");
						lblMsg.setText("Connection Fail");
						lblMsg.setForeground(Color.red);
					}
					else{
						//launch UI for student. e.g UIStudent uiStudent = new UIStudent(User user, Socket client)
						//each UIStudent will be able to determine which socket it belongs to.
						if(user instanceof Student){
							UIStudent uiStudent = new UIStudent(user, client, examHall);
							//uiStudent.setTitle("Student Exam");
							//uiStudent.setSize(800,600);
							//uiStudent.setVisible(true);
							//uiStudent.setResizable(false);
							
						}
						else{
							UIInvigilator uiInvigi = new UIInvigilator(user, client, examHall);
							uiInvigi.setTitle("Invigilator");
							uiInvigi.setExtendedState(JFrame.MAXIMIZED_BOTH);
							uiInvigi.setVisible(true);
							uiInvigi.setResizable(false);
							
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
