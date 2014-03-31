package eProctor;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.Timer;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

public class UIInvigilator extends JFrame implements ActionListener, Runnable{
	
	private Socket client;
	private User user;
	private ExamHall examHall;
	
	//create UI
	private JPanel pCenter, pBLeft,pBRight;
	private JLabel lblMsg, lblTimer,lblBackground;
	private JButton btnStartStop, btnTerminate;
	private JTextField txtMsg;
	private JTextArea txtDisplay;
	private String displayText;	
	private JScrollPane downScrollPane;
	private JComboBox ddlTerminate;
	private JLayeredPane layeredPane;
	private ImageIcon background = new ImageIcon("images/Invigilatorbg.jpg");
	private ImageIcon startButton = new ImageIcon("Images/invstartexam1.png");
	private ImageIcon finishButton = new ImageIcon("Images/endexam1.png");
	private ImageIcon terminate= new ImageIcon("Images/login1.png");
	
	private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	
	private Screen[] student;
	private Audio[] audio;
	//Communication Protocol
	private final int CONNECT = 1;
	private final int MSG = 2;
	private final int START = 3;
	private final int FINISHALL = 4;
	private final int FINISHTIMER = 5;
	private final int TERMINATE = 6;
	
	//managers
	private ExamHallManager examhallMgr = new ExamHallManager();
	
	//Arraylist of participants
	private ArrayList studentList = new ArrayList();
	
	//timer
	Timer timer = new Timer();
    boolean timesUp = false;
    long delay=0;
	
	public UIInvigilator(){
		
	}
	public UIInvigilator(User u, Socket c, ExamHall e){
		
		//Start all socket connection
		client = c;
		user = u;	
		examHall = e;

		Thread t = new Thread(this);
   		t.start(); 
   		
   		Container container = getContentPane();
		
		//layeredPane settings
		layeredPane = new JLayeredPane();
    	layeredPane.setPreferredSize(d);
    	lblBackground = new JLabel(background);
    	lblBackground.setOpaque(true); 
        lblBackground.setBounds(0,0,d.width,d.height); 
        layeredPane.add(lblBackground, new Integer(0));
		
		pCenter = new JPanel();
		pCenter.setLayout(new GridLayout(2,3,10,10));
		pCenter.setBounds(2,3,(d.width-4),(int)(d.height/1.35));
		pCenter.setOpaque(false);
		
		student = new Screen[6];
		audio = new Audio[6];
		for (int i=0; i<6; i++){
			student[i] = new Screen(5000+i);
			audio[i] = new Audio(6000+i);
			pCenter.add(student[i],new Integer(1));
			new Thread(audio[i]).start();
			new Thread(student[i]).start(); 
	        SwingUtilities.invokeLater(new Runnable(){ 
	            public void run() { 
	                setVisible(true); 
	            }});
		} 
		//creating pBelow 
		pBLeft = new JPanel();
		pBLeft.setOpaque(false);
		pBLeft.setBounds(0,(int)(d.height/1.34),(d.width-120),(int)(d.height*(1-1/1.28)));
		
		pBRight = new JPanel();
		pBRight.setOpaque(false);
		pBRight.setBounds((d.width-120),(int)(d.height/1.34),120,(int)(d.height*(0.96-1/1.28)));
	
		//creating taDisplay
		txtDisplay = new JTextArea(displayText);
		txtDisplay.setEnabled(false);
		Color color=new Color(244,254,232,69);
		txtDisplay.setBackground(color);
		txtDisplay.setBounds(0,(int)(d.height/1.28),(d.width-150),100);
		txtDisplay.setPreferredSize(new Dimension((d.width-150),100));
		downScrollPane = new JScrollPane(txtDisplay);
		downScrollPane.setBounds((d.width-150),(int)(d.height/1.25),10,100);
		downScrollPane.setPreferredSize(new Dimension(10,100));
		
		//creating pBEast
		
		lblMsg = new JLabel("Enter Message");
		lblMsg.setBounds(0,400,40,20);
		lblMsg.setFont(new Font("Serif", Font.BOLD, 15));
    	lblMsg.setForeground(Color.white);
		lblMsg.setOpaque(false);
		txtMsg = new JTextField(100);
		txtMsg.setBounds(0,((int)(d.height/1.28)),700,20);
		txtMsg.setOpaque(false);
		txtMsg.addActionListener(this);
		
		ddlTerminate = new JComboBox();
		ddlTerminate.setBounds((d.width-80),(int)(d.height/1.28),50,20);
		ddlTerminate.setPreferredSize(new Dimension(50,20));
		
		btnTerminate = new JButton(terminate);
		btnTerminate.addActionListener(this);
    	btnTerminate.setContentAreaFilled(false);
        btnTerminate.setFocusPainted(false);
        btnTerminate.setBorder(BorderFactory.createEmptyBorder());
        btnTerminate.setRolloverIcon(new ImageIcon("Images/terminate2.png"));
        btnTerminate.setPressedIcon(new ImageIcon("Images/terminate3.png"));
        btnTerminate.setBounds((d.width-120),(int)(d.height/1.28),20,10);
		
		lblTimer = new JLabel("--:--:--");
		lblTimer.setFont(new Font("Serif", Font.BOLD, 20));
    	lblTimer.setForeground(Color.white);
		lblTimer.setBounds((d.width-80),(int)(d.height/1.28),80,20);
		btnStartStop = new JButton(startButton);
		//btnStartStop = new JButton("Start");
		btnStartStop.addActionListener(this);
    	btnStartStop.setContentAreaFilled(false);
        btnStartStop.setFocusPainted(false);
        btnStartStop.setBorder(BorderFactory.createEmptyBorder());
        btnStartStop.setRolloverIcon(new ImageIcon("Images/invstartexam2.png"));
        btnStartStop.setPressedIcon(new ImageIcon("Images/invstartexam3.png"));
        btnStartStop.setBounds((d.width-120),(int)(d.height/1.28),20,10);
		
		pBLeft.add(txtDisplay, new Integer(1));
		pBLeft.add(downScrollPane,new Integer(1));
		pBLeft.add(lblMsg,new Integer(1));
		pBLeft.add(txtMsg,new Integer(1));
		
		pBRight.add(ddlTerminate,new Integer(1));
		pBRight.add(btnTerminate,new Integer(1));
		pBRight.add(lblTimer,new Integer(1));
		pBRight.add(btnStartStop,new Integer(1));
		
	
		
		
		layeredPane.add(pCenter,new Integer(1));
		layeredPane.add(pBLeft, new Integer(1));
		layeredPane.add(pBRight, new Integer(1));
		
		container.add(layeredPane);
		
		
	}
	
	public static void main(String[] args)
	{
		UIInvigilator uiInvigi = new UIInvigilator();
		uiInvigi.setTitle("Invigilator");
		uiInvigi.setExtendedState(JFrame.MAXIMIZED_BOTH);
		uiInvigi.setVisible(true);
		uiInvigi.setResizable(false);
		uiInvigi.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void actionPerformed(ActionEvent e){
		DataInputStream in;
		DataOutputStream out;
		try{
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
			if (e.getSource() == txtMsg){
				//send code to server informing a msg is send + its msg text
				out.writeInt(MSG);
				out.writeUTF(txtMsg.getText());
				txtMsg.setText("");
			}
			else if(e.getSource() instanceof JButton){
				JButton btn = (JButton)e.getSource();
				String btnImage = btn.getIcon().toString();
				
				if(e.getSource() == btnStartStop){
					if (btnImage.equals(startButton.toString())){
						boolean start = examhallMgr.startExam(examHall);
						
						if(start){
							btnStartStop.setIcon(finishButton);
					        btnStartStop.setRolloverIcon(new ImageIcon("Images/endexam3.png"));
					        btnStartStop.setPressedIcon(new ImageIcon("Images/endexam3.png"));
							//btnStartStop.setEnabled(false);
							//start exam
							out.writeInt(START);
						}
						
					}
					else if (btnImage.equals(finishButton.toString())){
						//stop exam
							//stop recording
						
							//set takeable to 0
							examhallMgr.finishExam(client, examHall);
							btnStartStop.setEnabled(false);
							//send answers
							
						
					}
				}
				else if (btnImage.equals(terminate.toString())){
					//get user ID
					String uID = (String)ddlTerminate.getSelectedItem();
					int userID = Integer.parseInt(uID);
					
					//pop out message box for reason of termination
					String reason = JOptionPane.showInputDialog(null,
							"Termination Reason :");    	
					if(reason!=null){
						//send to server to terminate userID from examHall
						examhallMgr.terminateStudent(client, userID, examHall, reason);
					}
					else{
						//No termination
					}
					
					
				}
			}
			
		}
		catch (IOException ex){
			System.out.println("IO Exception");
		}
	}
	
	public void run() {
		try {
            DataInputStream in = new DataInputStream(client.getInputStream());
            int code=0;
            Thread time = null;
            
            while (true) {
            	
                try {
                	code = in.readInt();
                	
                	
                	if(code == CONNECT){
                		int userID = in.readInt();
                		studentList.add(""+userID);
                		ddlTerminate.setModel(new javax.swing.DefaultComboBoxModel(studentList.toArray()));
                	}
                	else if(code == MSG){
                		//display msg from eventlog
                		String msg=in.readUTF();
                		txtDisplay.setText(msg);
                		txtDisplay.selectAll();
                	}
                	else if(code == START){
                		//start timer
                		delay = examHall.getExamSlot().getEndTime().getTime() - examHall.getExamSlot().getStartTime().getTime();
                		delay = delay/1000;
                		time = new Thread(new CountDown(delay));
                		time.start();
                		
                		//get exam paper
                		
                		
                		//start Recording
                		
                	}
                	else if(code == FINISHALL){
                		String examHallID = in.readUTF();
                		int participantSize= in.readInt();
                		
                		int userID=0;
                		for(int i=0;i<participantSize;i++){
                			userID = in.readInt();
                			
                			examhallMgr.endStudentTakable(userID, examHallID);
                			studentList.remove(""+userID);
                    		ddlTerminate.setModel(new javax.swing.DefaultComboBoxModel(studentList.toArray()));
                			//Terminate all server interface
                		}

                	}
                	else if(code == FINISHTIMER){
                		if(time != null)
                			time.stop();
                	}
                	else if(code == TERMINATE){
                		String examHallID = in.readUTF();
                		int userID= in.readInt();

                		examhallMgr.endStudentTakable(userID, examHallID);

                		//delete userid from combobox
                		studentList.remove(""+userID);
                		ddlTerminate.setModel(new javax.swing.DefaultComboBoxModel(studentList.toArray()));

                		//End screen session and audio session
                		
                		
						//display terminate success message	
                		JOptionPane.showMessageDialog(null,
                			    "Student has been terminated.");
                	}
                } 
                catch (IOException e) {
                    System.out.println(" Exception reading Streams: " + e);
                    break;
                }

            }
        } 
		catch (IOException e) {
	        System.out.println("IO Exception: " + e);
	        e.printStackTrace();
        }
    }
	
	//countdown timer
	class CountDown implements Runnable{
		long sec;
		long HH;
		long MM;
		long SS;
		boolean stop=false;
		
		public CountDown(long s){
			sec = s;
			
		}
		public void run(){
			while(sec>=0){

				SS = sec % 60;
				MM = (sec/60) % 60;
				HH = sec/3600;
				try{
					Thread.sleep(1000);
				}
				catch (InterruptedException x) {
                }
				lblTimer.setText(HH + ":" + MM + ":" + SS);
				sec--;
				
			}
			lblTimer.setText("Times Up");
			timesUp = true;
			
			if(timesUp==true){
				btnStartStop.setIcon(finishButton);
		        btnStartStop.setRolloverIcon(new ImageIcon("Images/endexam3.png"));
		        btnStartStop.setPressedIcon(new ImageIcon("Images/endexam3.png"));
        		btnStartStop.setEnabled(true);
        	}
		}
	}
}
