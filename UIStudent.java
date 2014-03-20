package eProctor;

import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import java.io.*;
import java.net.*;
import java.util.Timer;

import javax.swing.*;

import eProctor.UIInvigilator.CountDown;

public class UIStudent extends JFrame implements ActionListener, Runnable{

	private Socket client;
	private User user;
	private ExamHall examHall;
	
	//GUI
	JScrollPane scrollQuestionField, scrollAnswerField, downScrollPane;
	JPanel topPanel, downPanel, downPanelLeft, downPanelRight, p1;
	JLabel lblTimer, lblMsg;
	JTextField txtQuestion, txtAnswer, txtMsg;
	JTextArea txtDisplay;
	JButton btnSubmit;

	//Communication Protocol
	private final int CONNECT = 1;
	private final int MSG = 2;
	private final int START = 3;
	
	//timer
	Timer timer = new Timer();
    boolean timesUp = false;
    long delay=0;
    
	public UIStudent(){		
		
	}
	public UIStudent(User u, Socket c, ExamHall e){
		
		new WebcamClient().start();
		//Start all socket connection
		client = c;
		user = u;	
		examHall = e;
		
		Thread t = new Thread(this);
   		t.start(); 
		
		Container container = getContentPane();
        
        container.setLayout(new BorderLayout());
        
        topPanel = new JPanel(new GridLayout(2,1));
        txtQuestion = new JTextField("");
        txtQuestion.setEnabled(false);
        scrollQuestionField = new JScrollPane(txtQuestion);
        scrollQuestionField.setVisible(true);
        scrollQuestionField.setBackground(Color.WHITE);
        
        txtAnswer = new JTextField("");
        txtAnswer.setEnabled(false);
        scrollAnswerField = new JScrollPane(txtAnswer);
        scrollAnswerField.setVisible(true);
        scrollAnswerField.setBackground(Color.WHITE);
        
        topPanel.add(scrollQuestionField);
        topPanel.add(scrollAnswerField);
        
        
        downPanel = new JPanel();
        downPanel.setLayout(new BorderLayout());
       
        downPanelLeft = new JPanel(new BorderLayout());
        txtDisplay = new JTextArea();
        txtDisplay.setEnabled(false);
        downScrollPane = new JScrollPane(txtDisplay);
		downScrollPane.setPreferredSize(new Dimension(10,60));
		
		lblMsg = new JLabel("Enter Message");
		txtMsg = new JTextField();
		txtMsg.addActionListener(this);
		p1 = new JPanel(new BorderLayout());
		p1.add(lblMsg,BorderLayout.WEST);
		p1.add(txtMsg, BorderLayout.CENTER);

        downPanelLeft.add(downScrollPane, BorderLayout.CENTER);
		downPanelLeft.add(p1, BorderLayout.SOUTH);
        
        downPanelRight = new JPanel();
        downPanelRight.setLayout(new GridLayout(2,1));
        lblTimer = new JLabel("--:--:--");
        downPanelRight.add(lblTimer);
        btnSubmit = new JButton("Submit");
        downPanelRight.add(btnSubmit);
        
        downPanel.add(downPanelLeft, BorderLayout.CENTER);
        downPanel.add(downPanelRight, BorderLayout.EAST);
        
        //add to container
        container.add(topPanel, BorderLayout.CENTER);
        container.add(downPanel, BorderLayout.SOUTH);
	}
	
	public void actionPerformed(ActionEvent e){
		DataInputStream in;
		DataOutputStream out;
		try{
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
			if (e.getSource() == txtMsg){
				//send code to server informing a msg is send + its msg text
				out.writeInt(2);
				out.writeUTF(txtMsg.getText());
				txtMsg.setText("");
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
	
	public static void main(String[] args){
    	UIStudent uiStudent = new UIStudent();
    	uiStudent.setBounds(0, 0, 800, 600);
    	uiStudent.setVisible(true);
    	uiStudent.setResizable(false);
    	uiStudent.setTitle("Student Exam");
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
			
		}
	}
}
