package eProctor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

class Screen extends JPanel implements Runnable { 
	 
    private static final long serialVersionUID = 1L; 
    private Image cimage; 
    private Socket s;
    private int port;

    public void run() { 
        ServerSocket ss = null; 
        try { 
            ss = new ServerSocket(port); 
            while (true) { 
                s = null; 
                try { 
                    s = ss.accept(); 
                    ZipInputStream zis = new ZipInputStream(s 
                            .getInputStream()); 
                    zis.getNextEntry(); 
                    cimage = ImageIO.read(zis); 
                    repaint(); 
                } catch (Exception e) { 
                    e.printStackTrace(); 
                } finally { 
                    if (s != null) { 
                        try { 
                            s.close(); 
                        } catch (IOException e) { 
                            e.printStackTrace(); 
                        } 
                    } 
                } 
            } 
        } catch (Exception e) { 
        } finally { 
            if (ss != null) { 
                try { 
                    ss.close(); 
                } catch (IOException e) { 
                    e.printStackTrace(); 
                } 
            } 
        } 
    } 
    
//    public void closeSocket(){
//    	if (s != null) { 
//            try { 
//                s.close(); 
//            } catch (IOException e) { 
//                e.printStackTrace(); 
//            } 
//        } 
//    }

    public Screen(int port) { 
        super(); 
        this.port = port;
        this.setLayout(null); 
    } 

    public void paint(Graphics g) { 
        super.paint(g); 
        Graphics2D g2 = (Graphics2D) g; 
        g2.drawImage(cimage, 0, 0, null); 
    } 
}
