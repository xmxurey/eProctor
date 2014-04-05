package eProctor;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;

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
    int joinNo=0;

	//GUI
    private JScrollPane scrollQuestionField, scrollAnswerField, downScrollPane;
    private JPanel questionPlusPhotoPanel,answerPlusButtonPanel, photoPanel, buttonPanel,
            topPanel, downPanel, downPanelLeft, downPanelRight, p1;
    private JLabel lblMsg, lblTimer;
    private JTextField txtMsg;
    private JTextArea txtAnswer, eventLogArea;

    private JButton btnSubmit,btnGetPaper,btnNextPage, btnPreviousPage;
    private int pageIndex = 1, pageCount;
    private PDFPage page;
    private PagePanel pagePanel = new PagePanel();
    private PDFFile pdffile;

    //design part
    private JLabel lblBackground;
    private JLayeredPane layeredPane;
    public Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

    private ImageIcon background = new ImageIcon("images/Invigilatorbg.jpg");
    private ImageIcon submitButton = new ImageIcon("Images/savesubmit1.png");

    //List of Threads
    WebcamClient webcamClient;
    AudioClient audioClient;
	//timer
	Timer timer = new Timer();
    boolean timesUp = false;
    long delay=0;

	//managers
	private ExamHallManager examhallMgr = new ExamHallManager();
	
	public UIStudent(){		
		
	}
	public UIStudent(User u, Socket c, ExamHall e){
		//Start all socket connection
		client = c;
		user = u;	
		examHall = e;
		
		Container container = getContentPane();

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(d.width,(int)(d.height/1.05)));
        lblBackground = new JLabel(background);
        lblBackground.setOpaque(true);
        lblBackground.setBounds(0,0,d.width,(int)(d.height/1.05));
        layeredPane.add(lblBackground, new Integer(0));


        topPanel = new JPanel(new GridLayout(2,1));
//        pagePanel = new PagePanel();
        scrollQuestionField = new JScrollPane(pagePanel);
        scrollQuestionField.setOpaque(false);

        
      //camera
        photoPanel = new JPanel(new BorderLayout());
        photoPanel.setSize(200,300);
        
        Camera camera = new Camera();
        Component comp;
        comp = camera.Return();
        photoPanel.add(comp);
		
        //questionPlusPhotoPanel-----------------------------------------------------
        questionPlusPhotoPanel = new JPanel(new GridBagLayout());
        questionPlusPhotoPanel.setOpaque(false);
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
        bagCons.gridwidth = 4;
        bagCons.fill = GridBagConstraints.BOTH;
        questionPlusPhotoPanel.add(scrollQuestionField,bagCons);

        bagCons.gridx = 4;
        bagCons.gridwidth = 1;
        questionPlusPhotoPanel.add(photoPanel,bagCons);


        


        //anwerPlusButtonPanel---------------------------------------
        answerPlusButtonPanel = new JPanel(new GridBagLayout());
        answerPlusButtonPanel.setOpaque(false);
        //divide the space into 4 colums
        bagCons=new GridBagConstraints();
        bagCons.weightx=1;
        bagCons.weighty=1;
        for(int i=0;i<10;i++)
            answerPlusButtonPanel.add(new JPanel(), bagCons);

        Color color=new Color(244,254,232,69);
        txtAnswer = new JTextArea("");
        txtAnswer.setEnabled(false);

        scrollAnswerField = new JScrollPane(txtAnswer);
        scrollAnswerField.setVisible(true);
        scrollAnswerField.setBackground(Color.WHITE);

//        scrollAnswerField.setOpaque(false);

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
        btnNextPage.setEnabled(false);
        btnPreviousPage.setEnabled(false);
        btnGetPaper.setEnabled(false);

        GridBagConstraints btnBagCons = new GridBagConstraints();
        btnBagCons.gridwidth = GridBagConstraints.REMAINDER;
        buttonPanel.add(btnGetPaper,btnBagCons);
        btnBagCons.gridwidth = 1;
        btnBagCons.fill = GridBagConstraints.BOTH;
        buttonPanel.add(btnPreviousPage,btnBagCons);
        buttonPanel.add(btnNextPage,btnBagCons);
        buttonPanel.setOpaque(false);
        btnBagCons.gridwidth = GridBagConstraints.REMAINDER;
        //used to fill in space and divide the space into 10 column, let txtArea takes 9 and leave 1 for button panel
        for(int i=0;i<10;i++)
            buttonPanel.add(new JPanel(), btnBagCons);
        bagCons.gridx = 9;
        bagCons.gridwidth = 1;
        answerPlusButtonPanel.add(buttonPanel, bagCons);

        answerPlusButtonPanel.setOpaque(false);

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

        eventLogArea = new JTextArea();
        eventLogArea.setEnabled(false);
        eventLogArea.setFont(new Font("Verdana", Font.BOLD, 12));
        eventLogArea.setForeground(Color.BLACK);

        downScrollPane = new JScrollPane(eventLogArea);
        downScrollPane.setPreferredSize(new Dimension(10,60));

        lblMsg = new JLabel("Enter Message");
        txtMsg = new JTextField();
        txtMsg.addActionListener(this);
        txtMsg.setOpaque(false);
        p1 = new JPanel(new BorderLayout());
        p1.add(lblMsg,BorderLayout.WEST);
        p1.add(txtMsg, BorderLayout.CENTER);
        p1.setOpaque(false);

        downPanelLeft.add(downScrollPane, BorderLayout.CENTER);
        downPanelLeft.add(p1, BorderLayout.SOUTH);
        downPanelLeft.setOpaque(false);

        downPanelRight = new JPanel();
        downPanelRight.setLayout(new GridLayout(2, 1));
        lblTimer = new JLabel("--:--:--");
        lblTimer.setFont(new Font("Serif", Font.BOLD, 20));
    	lblTimer.setForeground(Color.white);
        downPanelRight.add(lblTimer);
        btnSubmit = new JButton(submitButton);
        btnSubmit.addActionListener(this);
        btnSubmit.setContentAreaFilled(false);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setBorder(BorderFactory.createEmptyBorder());
        btnSubmit.setPressedIcon(new ImageIcon("Images/savesubmit3.png"));
        btnSubmit.setEnabled(false);

        downPanelRight.add(btnSubmit);
        downPanelRight.setOpaque(false);

        downPanel.add(downPanelLeft, BorderLayout.CENTER);
        downPanel.add(downPanelRight, BorderLayout.EAST);

        //add to container
        topPanel.setBounds(0, 0, d.width, (int) (d.height / 1.35));
        downPanel.setBounds(0, (int) (d.height / 1.35), d.width, (int) (d.height / 5.8));
        topPanel.setOpaque(false);
        downPanel.setOpaque(false);
        layeredPane.add(topPanel, new Integer(1));
        layeredPane.add(downPanel, new Integer(1));

        container.add(layeredPane);

		Thread t = new Thread(this);
        t.start();
        
    }
	
	public void actionPerformed(ActionEvent e){
		DataInputStream in;
		DataOutputStream out;
        if (e.getSource() == btnGetPaper){
            page = pdffile.getPage(1);
            pagePanel.showPage(page);
        }
        else if (e.getSource() == btnNextPage){
            if(pageIndex == pageCount-1){
            	JOptionPane.showMessageDialog(null, "It is already the last page!");
            }
            else{ pageIndex++;
                page = pdffile.getPage(pageIndex);
                pagePanel.showPage(page);

            }
        }
        else if (e.getSource() == btnPreviousPage){
            if(pageIndex == 1){
            	JOptionPane.showMessageDialog(null, "It is already the first page!");
            }
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
        		    == JOptionPane.YES_OPTION){
        		 //Do the request
        		try{
        			String fileToSend = "Local/ExamAnswer/ExamHall=" + examHall.getExamHallID() +"_UserID="+ user.getUserID() +".txt";
        			
        			PrintWriter writer= new PrintWriter(new BufferedWriter(new FileWriter(fileToSend, false)));
        			System.out.println("text="+txtAnswer.getText());
        			writer.println(txtAnswer.getText());
        			
        			examhallMgr.studentFinishExam(client, examHall, user);
        			
    				writer.close();
        			
        		}
        		catch(Exception ex){
        			ex.printStackTrace();
        		}
        	}
        	else{
        		 //Go back to normal
        	}
        	
			
        }

        try{
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
			if (e.getSource() == txtMsg){
				//send code to server informing a msg is send + its msg text
				out.writeInt(Protocol.MSG);
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
            Thread time = null;

            System.out.println("New Session Started");
            int code=0;
            while (true) {
                try {
                    code = in.readInt();
                    System.out.println("code="+code);
                    if(code == Protocol.CONNECT){
                    	synchronized(this){
                			joinNo = examhallMgr.checkJoinNo(client);
                			System.out.println("JoinNo="+ joinNo);
                		}
                		webcamClient = new WebcamClient(joinNo);
                		webcamClient.start();
                		audioClient = new AudioClient(Protocol.audioPort[joinNo]);
                    }
                    else if(code == Protocol.RECEIVEQUESTION){
                		//get pdfQuestion
                        examhallMgr.receiveQuestion(client, examHall);
                        //display pdf page
                        pdffile = PDFDisplayManager.setup(examHall.getExamHallID());
                        pageCount = pdffile.getNumPages();
                    }
                    else if(code == Protocol.MSG){
                		//display msg from eventlog
                		String msg=in.readUTF();

                        //for testing only
                        if (msg == null)
                            System.out.println("msg is null");
                        if (eventLogArea == null)
                            System.out.println("txtDisplay is null");

                        eventLogArea.setText(msg);
                        eventLogArea.selectAll();
                	}
                	else if(code == Protocol.START){
                		//Enable answer panel and submit button
                		btnSubmit.setEnabled(true);
                		txtAnswer.setEnabled(true);
                		btnGetPaper.setEnabled(true);
                		btnNextPage.setEnabled(true);
                		btnPreviousPage.setEnabled(true);
                		
                		//start timer
                		delay = examHall.getExamSlot().getEndTime().getTime() - examHall.getExamSlot().getStartTime().getTime();
                		delay = delay/1000;
                		time = new Thread(new CountDown(delay,lblTimer,btnSubmit));
                		time.start();

                		//Create answer file
				        File examAnswer = new File("Local/ExamAnswer/ExamHall=" + examHall.getExamHallID()+"_UserID="+user.getUserID()+".txt");
				        boolean fileCreated = false;
				        examAnswer.createNewFile();
				        
				        //display start message
				        JOptionPane.showMessageDialog(null,
                			    "Exam has started");
                	}
                	else if(code == Protocol.ALLSENDANSWER){
                		sendAnswer();
                	}
                	else if(code == Protocol.STUDENTSENDANSWER){

            			System.out.println("Enter 4");
                		sendAnswer();
                        endInvigilatorclient();
                	}
                	else if(code == Protocol.FINISHTIMER){
                		if(time != null){
                			time.stop();
                			btnSubmit.setEnabled(false);
                		}
                	}
                	else if(code==Protocol.TERMINATESERVER){
                		//display end message
				        JOptionPane.showMessageDialog(null,
                			    "Exam has Ended");
				        
				        endInvigilatorclient();
                		System.out.println("Entered");
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
	
	public void endInvigilatorclient(){
		//end webcam
		webcamClient.stop();
		//end audio
		audioClient.audioStop();
	}
	
	public void sendAnswer(){
		try{

			System.out.println("Enter 5");
			String fileToSend = "Local/ExamAnswer/ExamHall=" + examHall.getExamHallID() +"_UserID="+ user.getUserID() +".txt";
			
			PrintWriter writer= new PrintWriter(new BufferedWriter(new FileWriter(fileToSend, false)));
			writer.println(txtAnswer.getText());

			writer.close();
			
    		examhallMgr.sendAnswer(client, fileToSend);
			
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	public void receiveQuestion(){
		examhallMgr.receiveQuestion(client, examHall);
	}
	
	public static void main(String[] args){
    	UIStudent uiStudent = new UIStudent();
//    	uiStudent.setBounds(0, 0, 800, 600);
//    	uiStudent.setVisible(true);
//    	uiStudent.setResizable(false);
//    	uiStudent.setTitle("Student Exam");
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
//			lblTimer.setText("Times Up");
//			timesUp = true;
//			
//			if(timesUp==true){
//				btnSubmit.setEnabled(false);
//        	}
//		}
//	}
}
