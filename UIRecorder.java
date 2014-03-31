package eProctor;
import java.util.*;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.io.File;
import java.net.MalformedURLException;


public class UIRecorder extends JFrame implements ActionListener{
	
	//create UI
	private JButton btnStart, btnEnd;
	private JPanel p1;
	
	private Recorder recorder;
	
	public UIRecorder(){
    	Container c = getContentPane();
    	
    	btnStart = new JButton("Start Recording");
    	btnStart.addActionListener(this);
    	
    	btnEnd = new JButton("End Recording");
    	btnEnd.addActionListener(this);
    	
    	p1 = new JPanel();
    	p1.add(btnStart);
    	p1.add(btnEnd);
    	
    	c.add(p1);
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == btnStart){
			recorder = new Recorder();
			try {
				recorder.startRecording();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if(e.getSource() == btnEnd){
			try {
				recorder.endRecording();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	
		}
	}

	public static void main(String[] args) throws Exception
    {
		UIRecorder uiRecorder = new UIRecorder();
		uiRecorder.setTitle("Recording");
		uiRecorder.setSize(300,200);
		uiRecorder.setVisible(true);
		uiRecorder.setResizable(false);
    }
	
}
