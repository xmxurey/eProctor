package eProctor;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Audio extends JPanel implements Runnable {
	
	private int port;
	private ServerSocket ss;
	private Socket s;
	private AudioPlayback player;
	
	public Audio(int port){
		this.port = port;
	};
	
	public void run(){
		//ServerSocket ss; 
        try { 
            ss = new ServerSocket(port); 
            while (true) { 
                //s = null; 
                try { 
                    s = ss.accept();
                    player=new AudioPlayback(s);
    	            player.start();
                } catch (Exception e) { 
                    e.printStackTrace(); 
                } finally {  
                } 
            } 
        } catch (Exception e) { 
        } finally { 
            
        } 
	}
	
	public void close(){
		player.stop();
	}
}
