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
	private JPanel pCenter, pBelow;
	private JPanel pBEast, pBWest, p1;
	private JLabel lblMsg, lblTimer;
	private JButton btnStartStop;
	private JTextField txtMsg;
	private JTextArea txtDisplay;
	private String displayText;	
	private JScrollPane downScrollPane;
	
	private Screen[] student;
	//Communication Protocol
	private final int CONNECT = 1;
	private final int MSG = 2;
	private final int START = 3;
	private final int ENDTAKABLE = 4;
	
	//managers
	private ExamHallManager examhallMgr = new ExamHallManager();
	
	//timer
	Timer timer = new Timer();
    boolean timesUp = false;
    long delay=0;
	
	public UIInvigilator(){
		
	}
	public UIInvigilator(User u, Socket c, ExamHall e){
		
		//new WebcamServer();

		//Start all socket connection
		client = c;
		user = u;	
		examHall = e;

		Thread t = new Thread(this);
   		t.start(); 
   		
		Container container = getContentPane();
		
		pCenter = new JPanel();
		pCenter.setLayout(new GridLayout(2,3));
		
		student = new Screen[6];
		for (int i=0; i<6; i++){
			student[i] = new Screen(5000+i);
			pCenter.add(student[i]);
			new Thread(student[i]).start(); 
	        SwingUtilities.invokeLater(new Runnable(){ 
	            public void run() { 
	                setVisible(true); 
	            }});
		} 
        
		container.add(pCenter,BorderLayout.CENTER);
	
		//creating pBelow 
		pBelow = new JPanel(new BorderLayout());
		
		//creating pBEast
		
		pBWest = new JPanel(new BorderLayout());
		lblMsg = new JLabel("Enter Message");
		txtMsg = new JTextField();
		txtMsg.addActionListener(this);
		p1 = new JPanel(new BorderLayout());
		p1.add(lblMsg,BorderLayout.WEST);
		p1.add(txtMsg, BorderLayout.CENTER);
		pBWest.add(p1, BorderLayout.SOUTH);
		//creating taDisplay
		txtDisplay = new JTextArea(displayText);
		txtDisplay.setEnabled(false);
		downScrollPane = new JScrollPane(txtDisplay);
		downScrollPane.setPreferredSize(new Dimension(10,60));
		pBWest.add(downScrollPane, BorderLayout.CENTER);
		
		
		pBelow.add(pBWest, BorderLayout.CENTER);
		
		//creating pBEast 
		pBEast = new JPanel(new GridLayout(2,1));
		lblTimer = new JLabel("--:--:--");
		pBEast.add(lblTimer);
		btnStartStop = new JButton("Start");
		btnStartStop.addActionListener(this);
		pBEast.add(btnStartStop);
		btnStartStop.setHorizontalAlignment(2);
		pBelow.add(pBEast,BorderLayout.EAST);
		
		container.add(pBelow,BorderLayout.SOUTH);
		
		
	}
	
	public static void main(String[] args)
	{
		UIInvigilator uiInvigi = new UIInvigilator();
		uiInvigi.setTitle("Invigilator");
		uiInvigi.setSize(800,600);
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
			else if(e.getSource() == btnStartStop){
				if (btnStartStop.getText() == ("Start")){
					boolean start = examhallMgr.startExam(examHall);
					
					if(start){
						btnStartStop.setText("Finish");
						//btnStartStop.setEnabled(false);
						//start exam
						out.writeInt(START);
					}
					
				}
				else if ((btnStartStop.getText() == ("Finish"))){
					//stop exam
						//stop recording
					
						//set takeable to 0
						examhallMgr.endStudentTakable(client, examHall);
						//send answers
					
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

            System.out.println("New Session Started");
            int code=0;
            
            
            while (true) {
            	
                try {
                	code = in.readInt();
                	
                	if(code == MSG){
                		//display msg from eventlog
                		String msg=in.readUTF();
                		txtDisplay.setText(msg);
                		txtDisplay.selectAll();
                	}
                	else if(code == START){
                		//start timer
                		delay = examHall.getExamSlot().getEndTime().getTime() - examHall.getExamSlot().getStartTime().getTime();
                		delay = delay/1000;
                		(new Thread(new CountDown(delay))).start();
                		
                		//get exam paper
                		
                		
                		//start Recording
                		
                	}
                } catch (IOException e) {
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
        		btnStartStop.setText("Finish");
        		btnStartStop.setEnabled(true);
        	}
		}
	}
}
