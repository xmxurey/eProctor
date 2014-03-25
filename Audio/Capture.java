package eProctor.Audio;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

class Capture implements Runnable { 

       TargetDataLine line; 
       Thread thread; 
       Socket s; 
       BufferedOutputStream captrueOutputStream; 

       Capture(Socket s){ 
         this.s=s; 
       } 

       public void start() { 

           thread = new Thread(this); 
           thread.setName("Capture"); 
           thread.start(); 
       } 

       public void stop() { 
           thread = null; 
       } 

       public void run() { 

           try { 
             captrueOutputStream=new BufferedOutputStream(s.getOutputStream()); 
           } 
           catch (IOException ex) { 
               return; 
           } 

           AudioFormat format =new AudioFormat(8000,16,2,true,true);
           DataLine.Info info = new DataLine.Info(TargetDataLine.class,format); 

           try { 
               line = (TargetDataLine) AudioSystem.getLine(info); 
               line.open(format, line.getBufferSize()); 
           } catch (Exception ex) { 
               return; 
           } 

           byte[] data = new byte[1024]; 
           int numBytesRead=0; 
           line.start(); 

           while (thread != null) { 
               numBytesRead = line.read(data, 0,128);
               try { 
                 captrueOutputStream.write(data, 0, numBytesRead); 
               } 
               catch (Exception ex) { 
                   break; 
               } 
           } 

           line.stop(); 
           line.close(); 
           line = null; 

           try { 
               captrueOutputStream.flush(); 
               captrueOutputStream.close(); 
           } catch (IOException ex) { 
               ex.printStackTrace(); 
           } 
       } 
     
}