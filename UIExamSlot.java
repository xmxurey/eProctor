package eProctor;

import java.util.*;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

public class UIExamSlot extends JFrame implements ActionListener{

	private String moduleCode;
	private JPanel pCenter, pButton;
	private JLabel lblModuleCode, lblExamSlot, lblModuleCode2, lblMsg, lblFiller;
	private JComboBox ddlExamSlot;
	private JButton btnSelect;
	private ArrayList<String> examSlotList;
	private ArrayList<String> unSortedExamSlotList = new ArrayList();;
	private User user;
	
	private ExamListManager examListMgr = new ExamListManager();
	
	public UIExamSlot(){
		
	}
	
	public UIExamSlot(String m, User u){
		user = u;
		moduleCode = m;
		
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		
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
		
		btnSelect = new JButton("Select");
		btnSelect.addActionListener(this);
		b.weightx = 0.5;
		b.gridx = 1;
		b.gridy = 2;
		b.anchor = GridBagConstraints.LINE_START;
		pButton = new JPanel();
		pButton.add(btnSelect, b);
		pCenter.add(pButton, b);
		
		
		lblFiller = new JLabel("");		
		lblMsg = new JLabel("");
		
		
		//Adding to container
		c.add(pCenter, BorderLayout.NORTH);
		c.add(lblMsg, BorderLayout.SOUTH);
		
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == btnSelect){
			int examHallIndex = ddlExamSlot.getSelectedIndex();
			String examHallID = (String)unSortedExamSlotList.get((examHallIndex-1)*2);
			
			ExamHall examhall = new ExamHall();
			boolean success = false;
			success = examListMgr.addEditExamSlot(user, lblModuleCode2.getText(), Integer.parseInt(examHallID));
			
			if(success){
				System.out.println("Update/Add Success");
				this.dispose();
			}
			else
				System.out.println("Update/Add fail");
			
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