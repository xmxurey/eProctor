package eProctor.Audio;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AudioClient extends Frame
{
    InputStream in;
    OutputStream out;
    BufferedReader br;
    BufferedWriter bw;
    Capture cap;
    
    public AudioClient(int portNo)
    {
        try
        {
        	Socket cli=new Socket("172.22.71.183",portNo);
            cap=new Capture(cli);
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