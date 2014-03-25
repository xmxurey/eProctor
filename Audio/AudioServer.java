package eProctor.Audio;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AudioServer extends Frame
{
	InputStream in;
    BufferedReader br;
    OutputStream out;
    BufferedWriter bw;
    
    ServerSocket serSock1;
    Socket cli1;
    
    ServerSocket serSock2;
    Socket cli2;
    
    ServerSocket serSock3;
    Socket cli3;
    
    ServerSocket serSock4;
    Socket cli4;
    
    ServerSocket serSock5;
    Socket cli5;
    
    ServerSocket serSock6;
    Socket cli6;
    
    public AudioServer()
    {
    	while(true){
	        try
	        {
	        	serSock1=new ServerSocket(6001);
	            cli1=serSock1.accept();
	            Playback player1=new Playback(cli1);
	            player1.start();
	            
	            serSock2=new ServerSocket(6002);
	            cli2=serSock2.accept();
	            Playback player2=new Playback(cli2);
	            player2.start();
	            
	            serSock3=new ServerSocket(6003);
	            cli3=serSock3.accept();
	            Playback player3=new Playback(cli3);
	            player3.start();
	            
	            serSock4=new ServerSocket(6004);
	            cli4=serSock4.accept();
	            Playback player4=new Playback(cli4);
	            player4.start();
	            
	            serSock5=new ServerSocket(6005);
	            cli5=serSock5.accept();
	            Playback player5=new Playback(cli5);
	            player5.start();
	            
	            serSock6=new ServerSocket(6006);
	            cli6=serSock6.accept();
	            Playback player6=new Playback(cli6);
	            player6.start();
	        }
	        catch(Exception e)
	        {}
    	}
    	
    }
    public static void main(String[] args)
    {
    	AudioServer server = new AudioServer();
    }
    
}