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
	private JTextArea eventLogArea = new JTextArea();;

	private JScrollPane downScrollPane;
	private JComboBox ddlTerminate;
	private JLayeredPane layeredPane;
	private ImageIcon background = new ImageIcon("images/Invigilatorbg.jpg");
	private ImageIcon startButton = new ImageIcon("Images/invstartexam1.png");
	private ImageIcon finishButton = new ImageIcon("Images/endexam1.png");
	private ImageIcon terminate= new ImageIcon("Images/terminatestudent1.png");
	
	private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	
	private Screen[] student;
	private Thread[] webcamThread, audioThread;
	private Audio[] audio;

	
	//managers
	private ExamHallManager examhallMgr = new ExamHallManager();
	
	//Arraylist of participants
    private ArrayList terminateList = new ArrayList();
    private int tracker=0;
    private Integer studentArr[] = new Integer[6];
	
	//timer
	Timer timer = new Timer();
    boolean timesUp = false;
    long delay=0;
    
    //Recording
  	private Recorder recorder;
	
	public UIInvigilator(){
	}
	public UIInvigilator(User u, Socket c, ExamHall e){
		
		//Start all socket connection
		client = c;
		this.user = u;
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
		audioThread = new Thread[6];
		//webcamThread = new Thread[6];
		
		for (int i=0; i<6; i++){
			student[i] = new Screen(Protocol.webcamPort[i]);
			audio[i] = new Audio(Protocol.audioPort[i]);
			pCenter.add(student[i],new Integer(1));
			audioThread[i] = new Thread(audio[i]);
			audioThread[i].start();
			new Thread(student[i]).start();
			//webcamThread[i] = new Thread(student[i]);
			//webcamThread[i].start();
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
		Color color=new Color(244,254,232,69);

		eventLogArea.setFont(new Font("Verdana", Font.BOLD, 12));
		eventLogArea.setForeground(Color.BLACK);
		eventLogArea.setEnabled(false);
		
		downScrollPane = new JScrollPane(eventLogArea);
		downScrollPane.setBackground(color);
		downScrollPane.setPreferredSize(new Dimension(d.width-150,100));
		
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
				out.writeInt(Protocol.MSG);
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
							btnStartStop.setEnabled(false);
							//start exam
							out.writeInt(Protocol.START);
						}
						else{
							JOptionPane.showMessageDialog(null, "You cannot start the exam before its scheduled time");
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
                    int terminateID = (Integer)ddlTerminate.getSelectedItem();
					
					//pop out message box for reason of termination
					String reason = JOptionPane.showInputDialog(null,
							"Termination Reason :");    	
					if(reason!=null){
						//Confirm Terminate
						Object[] options = {"Confirm", "Cancel"};
						int n = JOptionPane.showOptionDialog(null, "Confirm Exam Termination", "SYSTEM NOTICE", JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
						if (n == JOptionPane.YES_OPTION){
							audioThread[terminateID].stop();
							audio[terminateID].close();
							//webcamThread[terminateID].stop();
						//send to server to terminate userID from examHall
						examhallMgr.terminateStudent(client, studentArr[terminateID], examHall, reason);
						}
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
                	
                	
                	if(code == Protocol.CONNECT){
                		int userID = in.readInt();
                        studentArr[tracker] = userID;
                        tracker++;
                        terminateList.removeAll(terminateList);
                        for(int i=0;i<6;i++){
                            if(studentArr[i] != null)
                                terminateList.add(i);
                        }
                		ddlTerminate.setModel(new javax.swing.DefaultComboBoxModel(terminateList.toArray()));
                	}
                	else if(code == Protocol.MSG){
                		//display msg from eventlog
                		String msg=in.readUTF();
                		eventLogArea.setText(msg);
                		eventLogArea.selectAll();
                	}
                	else if(code == Protocol.START){
                		//start timer
                		delay = examHall.getExamSlot().getEndTime().getTime() - examHall.getExamSlot().getStartTime().getTime();
                		delay = delay/1000;
                		time = new Thread(new CountDown(delay,lblTimer,btnStartStop,finishButton,new ImageIcon("Images/endexam3.png")));
                		time.start();
                		
                		//get exam paper
                		
                		
                		//start Recording
                		recorder = new Recorder();
            			try {
            				recorder.startRecording();
            			} catch (Exception e1) {
            				// TODO Auto-generated catch block
            				e1.printStackTrace();
            			}
                		
            			//display start message
				        JOptionPane.showMessageDialog(null,
                			    "Exam has started");
                	}
                	else if(code == Protocol.ALLSENDVIDEO){
                		try {
            				recorder.endRecording(examHall.getExamHallID());
                    		//transfer file to server
                    		examhallMgr.sendVideo(client, examHall.getExamHallID());
            			} catch (Exception e1) {
            				// TODO Auto-generated catch block
            				e1.printStackTrace();
            			}
                		
                		JOptionPane.showMessageDialog(null, "The Recording has been saved to your computer.\n"
                							+"Student's Exam answers and Event log have been sent to the server.",
                							"SYSTEM NOTICE",JOptionPane.PLAIN_MESSAGE);
                		
                	}
                	else if(code == Protocol.FINISHALL){
                		String examHallID = in.readUTF();
                		int participantSize= in.readInt();
                		
                		int userID=0;
                		for(int i=0;i<participantSize;i++){
                			userID = in.readInt();
                			
                			examhallMgr.endStudentTakable(userID, examHallID);
                            for(int j=0;j<6;j++){
                                if(studentArr[j] == userID){
                                    studentArr[j] = null;
                                    terminateList.remove(j);
                                    break;
                                }
                            }
                			//Terminate all server interface
                		}
                        ddlTerminate.setModel(new javax.swing.DefaultComboBoxModel(terminateList.toArray()));
                		//display start message
				        JOptionPane.showMessageDialog(null,
                			    "Exam has Ended");

                	}
                	else if(code == Protocol.FINISHTIMER){
                		if(time != null)
                			time.stop();
                	}
                	else if(code == Protocol.STUDENTREMOVAL){
                		String examHallID = in.readUTF();
                		int userID= in.readInt();

                		examhallMgr.endStudentTakable(userID, examHallID);

                		//delete userid from combobox
                        for(int j=0;j<6;j++){
                            if(studentArr[j] == userID){
                                studentArr[j] = null;
                                terminateList.remove(j);
                                break;
                            }
                        }
                        ddlTerminate.setModel(new javax.swing.DefaultComboBoxModel(terminateList.toArray()));

                		//End screen session and audio session
                		
                		
						//display terminate success message	
                		JOptionPane.showMessageDialog(null,
                			    "Student has been removed from examHall.");
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
//	class CountDown implements Runnable{
//		long sec;
//		long HH;
//		long MM;
//		long SS;
//		boolean stop=false;
//
//		public CountDown(long s){
//			sec = s;
//
//		}
//		public void run(){
//			while(sec>=0){
//
//				SS = sec % 60;
//				MM = (sec/60) % 60;
//				HH = sec/3600;
//				try{
//					Thread.sleep(1000);
//				}
//				catch (InterruptedException x) {
//                }
//				lblTimer.setText(HH + ":" + MM + ":" + SS);
//				sec--;
//
//			}
//			lblTimer.setText("--:--:--");
//			timesUp = true;
//
//			if(timesUp==true){
//				btnStartStop.setIcon(finishButton);
//		        btnStartStop.setRolloverIcon(new ImageIcon("Images/endexam3.png"));
//		        btnStartStop.setPressedIcon(new ImageIcon("Images/endexam3.png"));
//        		btnStartStop.setEnabled(true);
//        	}
//		}
//	}
}
