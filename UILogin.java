package eProctor;
import java.util.*;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

public class UILogin extends JFrame implements ActionListener{
	
	//create UI
		//private JFrame loginFrame;
		private JButton btnLogin;
		private JTextField txtUserID, txtPassword;
		private JLabel lblUser, lblPass, lblMsg, lblBackground;
		private JPanel pCenter;
		private UserManager userMgr = new UserManager();
		private JLayeredPane layeredPane;
		private ImageIcon background = new ImageIcon("Images/loginbg.jpg");
		
		public UILogin(){
	    	Container c = getContentPane();
	    	
	    	//loginFrame = new JFrame();
	    	
	    	lblUser = new JLabel("  Username:");
	    	lblUser.setOpaque(false);
	    	lblUser.setFont(new Font("Serif", Font.BOLD, 22));
	    	lblUser.setForeground(Color.white);
	    	lblPass = new JLabel("  Password:");
	    	lblPass.setOpaque(false);
	    	lblPass.setFont(new Font("Serif", Font.BOLD, 22));
	    	lblPass.setForeground(Color.white);
	    	lblMsg = new JLabel("");
	    	txtUserID = new JTextField(15);
	    	//txtUserID.setSize(100, 60);
	    	txtUserID.setOpaque(false);
	    	txtPassword = new JPasswordField(15);
	    	txtPassword.setOpaque(false);
	    	
	    	btnLogin = new JButton("Login");
	    	btnLogin.addActionListener(this);
	    
	    	//layeredPane settings
	    	layeredPane = new JLayeredPane();
	    	layeredPane.setPreferredSize(new Dimension(800, 600));
	    	lblBackground = new JLabel(background);
	    	lblBackground.setOpaque(true); 
	        lblBackground.setBounds(0,0,800,600); 
	        layeredPane.add(lblBackground, new Integer(0));
	    	//loginFrame.add(background, BorderLayout.CENTER);
	        
	        //panel settings
	    	pCenter = new JPanel();
	    	pCenter.setBounds(230, 220, 340, 300);
	    	pCenter.add(lblUser, new Integer(1));
	        pCenter.add(txtUserID, new Integer(1));
	        pCenter.add(lblPass, new Integer(1));
	        pCenter.add(txtPassword, new Integer(1));
	        pCenter.add(btnLogin, new Integer(1));
	    	pCenter.setOpaque(false);
	    	
	        //layeredPane.add(lblUser, new Integer(1));
	        //layeredPane.add(txtUserID, new Integer(1));
	        //layeredPane.add(lblPass, new Integer(1));
	        //layeredPane.add(txtPassword, new Integer(1));
	        //layeredPane.add(btnLogin, new Integer(1));
	    	
	    	layeredPane.add(pCenter, new Integer(1));

	    	c.add(layeredPane);
	    	//c.add(pCenter,BorderLayout.CENTER);  
	    	//c.add(lblMsg, BorderLayout.SOUTH);
	}
	
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == btnLogin){
			
			User user = userMgr.authenticate(txtUserID.getText().trim(), txtPassword.getText().trim());
			
			if(user == null){
				lblMsg.setText("Incorrect username/password");
			}
			else if(user instanceof Invigilator){
				UIMenu uimenu = new UIMenu(user);
				uimenu.setTitle("eProctor");
				uimenu.setSize(800,600);
				uimenu.setVisible(true);
				uimenu.setResizable(false);
				uimenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				this.dispose();
			}
			else if(user instanceof Student){
				UIMenu uimenu = new UIMenu(user);
				uimenu.setTitle("eProctor");
				uimenu.setSize(800,600);
				uimenu.setVisible(true);
				uimenu.setResizable(false);
				uimenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				this.dispose();
			}
		}
		
	}
	public static void main(String[] args)
    {
		UILogin uiLogin = new UILogin();
		uiLogin.setTitle("Log in");
		uiLogin.setSize(800,600);
		uiLogin.setVisible(true);
		uiLogin.setResizable(false);
		
    }	
}