package eProctor;
import java.text.SimpleDateFormat;

import java.util.*;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

public class UIExamSlot extends JFrame implements ActionListener{

	private String moduleCode;
	private JPanel pCenter, pButton;
	private JLabel lblModuleCode, lblExamSlot, lblModuleCode2, lblMsg, lblFiller, lblBackground;
	private JComboBox ddlExamSlot;
	private JButton btnSelect;
	private ArrayList<String> examSlotList;
	private ArrayList<String> unSortedExamSlotList = new ArrayList();;
	private User user;
	private JLayeredPane layeredPane;
	private ImageIcon selectButton = new ImageIcon("Images/select1.png");
	private ImageIcon background = new ImageIcon("Images/loginbg.jpg");
	
	private ExamListManager examListMgr = new ExamListManager();
	
	public UIExamSlot(){
		
	}
	
	public UIExamSlot(String m, User u){
		user = u;
		moduleCode = m;
		
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
		//layeredPane settings
    	layeredPane = new JLayeredPane();
    	layeredPane.setPreferredSize(new Dimension(450, 200));
    	lblBackground = new JLabel(background);
    	lblBackground.setOpaque(true); 
        lblBackground.setBounds(0,0,450,200); 
        layeredPane.add(lblBackground, new Integer(0));
		
		//creating pCenter
		pCenter = new JPanel(new GridBagLayout());
		GridBagConstraints b = new GridBagConstraints();
		b.fill = GridBagConstraints.HORIZONTAL;
		
		
		lblModuleCode = new JLabel("Module Code: ");
		b.weightx = 0.5;
		b.gridx = 0;
		b.gridy = 0;
		pCenter.add(lblModuleCode, b);
		
		lblModuleCode2 = new JLabel(moduleCode);
		b.weightx = 0.5;
		b.gridx = 1;
		b.gridy = 0;
		pCenter.add(lblModuleCode2, b);
		
		
		lblExamSlot = new JLabel("Exam Slot: ");
		b.weightx = 0.5;
		b.gridx = 0;
		b.gridy = 1;
		pCenter.add(lblExamSlot, b);
		
		//populate ddlExamslot
		ExamHall examhall = new ExamHall();
		unSortedExamSlotList = examListMgr.getExamSlotList(moduleCode);
		examSlotList = new ArrayList<String>();
		
		String result="";
		for(int i=0;i<unSortedExamSlotList.size();i+=2){
			String examHallID = (String)unSortedExamSlotList.get(i);
			String addInfo = (String)unSortedExamSlotList.get(i+1);
			result = examHallID + addInfo;
			examSlotList.add(result);
		}
				
		String header = "ExamHallID / Date / Timing / Slots Available";
		examSlotList.add(header);
		
		int index = examSlotList.indexOf((String)header);
		examSlotList.remove(index);
		examSlotList.add(0, header);
		
		ddlExamSlot = new JComboBox(examSlotList.toArray());
		ddlExamSlot.setPreferredSize(new Dimension(300, 20));
		
		b.weightx = 0.5;
		b.gridx = 1;
		b.gridy = 1;
		pCenter.add(ddlExamSlot, b);
		
		btnSelect = new JButton(selectButton);
		btnSelect.addActionListener(this);
		btnSelect.setContentAreaFilled(false);
		btnSelect.setFocusPainted(false);
		btnSelect.setBorder(BorderFactory.createEmptyBorder());
		btnSelect.setRolloverIcon(new ImageIcon("Images/select2.png"));
		btnSelect.setPressedIcon(new ImageIcon("Images/select3.png"));
		b.weightx = 0.5;
		b.gridx = 1;
		b.gridy = 2;
		b.anchor = GridBagConstraints.LINE_START;
		pButton = new JPanel();
		pButton.add(btnSelect, b);
		pCenter.add(pButton, b);
		
		
		lblFiller = new JLabel("");		
		lblMsg = new JLabel("");
		
		pCenter.setBounds(15,0,420,200);
		pButton.setOpaque(false);
		pCenter.setOpaque(false);
		
		layeredPane.add(pCenter, new Integer(1));
		c.add(layeredPane);
		
		
	}
	
	public void actionPerformed(ActionEvent e){
		Date startTime=null;
		Date endTime=null;
		int i;
		
  		if(e.getSource() == btnSelect){
			int examHallIndex = ddlExamSlot.getSelectedIndex();
			String examHallID = (String)unSortedExamSlotList.get((examHallIndex-1)*2);
			String timeInfo = (String)unSortedExamSlotList.get((examHallIndex-1)*2+1);
			
			StringTokenizer splited1 = new StringTokenizer(timeInfo);
			String dateInfo =(String)splited1.nextElement();
			String[] splited2 =((String) splited1.nextElement()).split("-");
			
			String startTimeS = dateInfo + " " + splited2[0].trim();
			String endTimeS = dateInfo + " " + splited2[1].trim();
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			try{
			   startTime = simpleDateFormat.parse(startTimeS);
			   endTime = simpleDateFormat.parse(endTimeS);
			  
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			
			ExamHall examhall = new ExamHall();
			boolean success = false;
			success = examListMgr.addEditExamSlot(user, lblModuleCode2.getText(), examHallID, 
					         startTime, endTime);
			
			if(success){
				JOptionPane.showMessageDialog(null, "Update/Add exam slot successfully!");
				this.dispose();
			}
			else{
				JOptionPane.showMessageDialog(null, "Add/Update failed!");
			}
		}
	}
	
	public static void main(String[] args){
		UIExamSlot uiExamSlot = new UIExamSlot();
		uiExamSlot.setTitle("eProctor - Add/Edit Exam Slot");
		uiExamSlot.setSize(450,200);
		uiExamSlot.setVisible(true);
		uiExamSlot.setResizable(false);
	}
}
