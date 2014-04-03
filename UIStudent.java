package eProctor;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;

import eProctor.UIInvigilator.CountDown;

import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import java.io.*;
import java.net.*;
import java.util.Timer;

import javax.swing.*;

//import UIInvigilator.CountDown;

public class UIStudent extends JFrame implements ActionListener, Runnable{

	private Socket client;
	private User user;
	private ExamHall examHall;


    //GUI
    JScrollPane scrollQuestionField, scrollAnswerField, downScrollPane;
    JPanel questionPlusPhotoPanel,answerPlusButtonPanel, photoPanel, buttonPanel,
            topPanel, downPanel, downPanelLeft, downPanelRight, p1;
    JLabel lblMsg, lblTimer;
    JTextField txtAnswer, txtMsg;
    JTextArea txtDisplay;
    JButton btnSubmit,btnGetPaper,btnNextPage, btnPreviousPage;
    int pageIndex = 1, pageCount;
    PDFPage page;
    PagePanel pagePanel = new PagePanel();
    PDFFile pdffile;



    //Communication Protocol
	private final int CONNECT = 1;
	private final int MSG = 2;
	private final int START = 3;
	private final int FINISHALL = 4;
	private final int FINISHTIMER = 5;
	private final int TERMINATE = 6;
	private final int SENDVIDEO = 7;
	private final int SENDANSWER = 8;
	
	//timer
	Timer timer = new Timer();
    boolean timesUp = false;
    long delay=0;

	//managers
	private ExamHallManager examhallMgr = new ExamHallManager();
	
	public UIStudent(){		
		
	}
	public UIStudent(User u, Socket c, ExamHall e){
		
		//new WebcamClient().start();
		//AudioClient audioClient = new AudioClient(6002);
		//Start all socket connection
		client = c;
		user = u;	
		examHall = e;
		
		Thread t = new Thread(this);
   		t.start();


        JFrame container = new JFrame("Exam");

        container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        container.setLayout(new BorderLayout());

        topPanel = new JPanel(new GridLayout(2,1));
//        pagePanel = new PagePanel();
        scrollQuestionField = new JScrollPane(pagePanel);

        photoPanel = new JPanel(new BorderLayout());
        photoPanel.setSize(200,300);
        photoPanel.add(new JTextField("Here is the photo"));

        //questionPlusPhotoPanel-----------------------------------------------------
        questionPlusPhotoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints bagCons = new GridBagConstraints();

        bagCons.weightx = 1;
        //this is used to set 4 cells (divide into 4 columns)
        questionPlusPhotoPanel.add(new JPanel(), bagCons);
        questionPlusPhotoPanel.add(new JPanel(), bagCons);
        questionPlusPhotoPanel.add(new JPanel(), bagCons);
        questionPlusPhotoPanel.add(new JPanel(), bagCons);

        bagCons.weightx = 1;
        bagCons.weighty = 1;
        bagCons.gridx = 0;
        bagCons.gridy = 0;
        bagCons.gridwidth = 3;
        bagCons.fill = GridBagConstraints.BOTH;
        questionPlusPhotoPanel.add(scrollQuestionField,bagCons);

        bagCons.gridx = 3;
        bagCons.gridwidth = 1;
        questionPlusPhotoPanel.add(photoPanel,bagCons);


        //display pdf page
        pdffile = PDFDisplayManager.setup(examHall.getExamHallID());
        pageCount = pdffile.getNumPages();
//        page = pdffile.getPage(0);
//        pagePanel.showPage(page);
//


        //anwerPlusButtonPanel---------------------------------------
        answerPlusButtonPanel = new JPanel(new GridBagLayout());
        //divide the space into 4 colums
        bagCons=new GridBagConstraints();
        bagCons.weightx=1;
        bagCons.weighty=1;
        for(int i=0;i<10;i++)
            answerPlusButtonPanel.add(new JPanel(), bagCons);

        txtAnswer = new JTextField("");
        txtAnswer.setEnabled(true);

        scrollAnswerField = new JScrollPane(txtAnswer);
        scrollAnswerField.setVisible(true);
        scrollAnswerField.setBackground(Color.WHITE);



        //set fleep page buttons positions in the button panel
        bagCons.gridx = 0;
        bagCons.gridy = 0;
        bagCons.gridwidth = 9;
        bagCons.fill = GridBagConstraints.BOTH;
        answerPlusButtonPanel.add(scrollAnswerField, bagCons);
        buttonPanel = new JPanel(new GridBagLayout());
        btnNextPage = new JButton(">>");
        btnPreviousPage = new JButton("<<");
        btnGetPaper = new JButton("Get Question Paper");
        GridBagConstraints btnBagCons = new GridBagConstraints();
        btnBagCons.gridwidth = GridBagConstraints.REMAINDER;
        buttonPanel.add(btnGetPaper,btnBagCons);
        btnBagCons.gridwidth = 1;
        btnBagCons.fill = GridBagConstraints.BOTH;
        buttonPanel.add(btnPreviousPage,btnBagCons);
        buttonPanel.add(btnNextPage,btnBagCons);
        btnBagCons.gridwidth = GridBagConstraints.REMAINDER;
        //used to fill in space and divide the space into 10 column, let txtArea takes 9 and leave 1 for button panel
        for(int i=0;i<10;i++)
            buttonPanel.add(new JPanel(), btnBagCons);
        bagCons.gridx = 9;
        bagCons.gridwidth = 1;
        answerPlusButtonPanel.add(buttonPanel, bagCons);

        topPanel.add(questionPlusPhotoPanel,BorderLayout.CENTER);
        topPanel.add(answerPlusButtonPanel,BorderLayout.SOUTH);

        //button listeners
        btnGetPaper.addActionListener(this);
        btnNextPage.addActionListener(this);
        btnPreviousPage.addActionListener(this);

        //downPanel---------------------------------------------------------
        downPanel = new JPanel();
        downPanel.setLayout(new BorderLayout());

        downPanelLeft = new JPanel(new BorderLayout());
        txtDisplay = new JTextArea("hihi");
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
        downPanelRight.setLayout(new GridLayout(2, 1));
        lblTimer = new JLabel("--:--:--");
        downPanelRight.add(lblTimer);
        btnSubmit = new JButton("Submit");
        btnSubmit.addActionListener(this);
        downPanelRight.add(btnSubmit);

        downPanel.add(downPanelLeft, BorderLayout.CENTER);
        downPanel.add(downPanelRight, BorderLayout.EAST);




        //add to container
        container.pack();
        container.setSize(1000, 600);
        container.setResizable(false);
        container.setVisible(true);
        container.add(topPanel, BorderLayout.CENTER);
        container.add(downPanel, BorderLayout.SOUTH);


    }
	
	public void actionPerformed(ActionEvent e){
		DataInputStream in;
		DataOutputStream out;
        if (e.getSource() == btnGetPaper){
            page = pdffile.getPage(1);
            pagePanel.showPage(page);
        }
        else if (e.getSource() == btnNextPage){
            if(pageIndex == pageCount-1)
            	JOptionPane.showMessageDialog(null, "It is already the last page!");
            else{ pageIndex++;
                page = pdffile.getPage(pageIndex);
                pagePanel.showPage(page);

            }
        }
        else if (e.getSource() == btnPreviousPage){
            if(pageIndex == 1)
            	JOptionPane.showMessageDialog(null, "It is already the first page!");
            else{
                pageIndex--;
                page = pdffile.getPage(pageIndex);
                pagePanel.showPage(page);
            }
        }
        else if(e.getSource() == btnSubmit){
        	if (JOptionPane.showConfirmDialog(null, "Are you sure to submit your answer script"
        			+ " and end the exam ?", "Request", 
        		    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
        		    == JOptionPane.YES_OPTION)
        		{
        		 //Do the request
        		}
        		else
        		{
        		 //Go back to normal
        		}
        	 
        }
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

            int code=0;
            Thread time = null;
            while (true) {
                try {
                    code = in.readInt();
                    System.out.println("Code is="+code);
                	if(code == MSG){
                		//display msg from eventlog;
                		String msg=in.readUTF();
System.out.println(msg);
                		//txtDisplay.setText(msg);
                		//System.out.println("entered 1");
                		//txtDisplay.selectAll();
                		//System.out.println("entered 2");
                	}
                	else if(code == START){
                		//start timer
                		delay = examHall.getExamSlot().getEndTime().getTime() - examHall.getExamSlot().getStartTime().getTime();
                		delay = delay/1000;
                		time = new Thread(new CountDown(delay));
                		time.start();                                
                        
                        //create exam answer sheet
                        File answerSheetFile = new File("Local/ExamAnswer/ExamHall=" + examHall.getExamHallID()+ "_Userid="+user.getUserID()+".txt");
                        
                        boolean fileCreated = false;
                        fileCreated = answerSheetFile.createNewFile();
                        
                        if(!fileCreated){
                        	JOptionPane.showMessageDialog(null,
                    			    "Answer sheet cannot be created.");
                        }
                	}
                	else if(code == SENDANSWER){
                		//examhallMgr.saveAnswer(txtAnswer.getText(),  examHall.getExamHallID(), user.getUserID());
                		examhallMgr.sendAnswer(client, examHall.getExamHallID(), user.getUserID());
                	}
                	else if(code == FINISHTIMER){
                		if(time != null)
                			time.stop();
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
//    	uiStudent.setBounds(0, 0, 800, 600);
//    	uiStudent.setVisible(true);
//    	uiStudent.setResizable(false);
//    	uiStudent.setTitle("Student Exam");
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
				btnSubmit.setText("FINISH");
				btnSubmit.setEnabled(false);
        	}
		}
	}	
}
