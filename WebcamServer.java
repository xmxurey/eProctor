package eProctor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class WebcamServer extends JFrame { 
    private static final long serialVersionUID = 1L; 
    Dimension screenSize; 
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
 
    public WebcamServer() { 
        super("Screen"); 
        screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
        this.setSize(d.width, d.height); 
        Screen p = new Screen(); 
        Container c = this.getContentPane(); 
        c.setLayout(new BorderLayout()); 
        c.add(p, SwingConstants.CENTER); 
        new Thread(p).start(); 
        SwingUtilities.invokeLater(new Runnable(){ 
            public void run() { 
                setVisible(true); 
            }}); 
    } 
 
    public static void main(String[] args) { 
        new WebcamServer(); 
    } 
 
} 
