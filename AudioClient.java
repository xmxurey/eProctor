package eProctor;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
  
public class AudioClient extends Frame
{
	private InputStream in;
	private OutputStream out;
	private BufferedReader br;
	private BufferedWriter bw;
	private AudioCapture cap;
    
    public AudioClient(int portNo)
    {
        try
        {
        	Socket cli=new Socket("172.34.21.32",portNo);
            cap=new AudioCapture(cli);
            cap.start();
        }
        catch(Exception e)
        {}
    }
    
    public static void main(String[] args)
    {
    	AudioClient client = new AudioClient(6002);
    }
}