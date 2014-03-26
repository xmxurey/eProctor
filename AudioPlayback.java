package eProctor;
 
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

class AudioPlayback implements Runnable { 

       final int bufSize = 16384; 
       private SourceDataLine line; 
       private Thread thread; 
       private Socket s; 

       AudioPlayback(Socket s){ 
         this.s=s; 
       } 
       public void start() { 

           thread = new Thread(this); 
           thread.setName("AudioPlayback"); 
           thread.start(); 
       } 

       public void stop() { 
           thread = null; 
       } 

       public void run() { 

           AudioFormat format =new AudioFormat(8000,16,2,true,true);
           BufferedInputStream playbackInputStream; 

           try { 
             playbackInputStream=new BufferedInputStream(new AudioInputStream(s.getInputStream(),format,2147483647));
           } 
           catch (IOException ex) { 
               return; 
           } 

           DataLine.Info info = new DataLine.Info(SourceDataLine.class,format); 

           try { 
               line = (SourceDataLine) AudioSystem.getLine(info); 
               line.open(format, bufSize); 
           } catch (LineUnavailableException ex) { 
               return; 
           } 

           byte[] data = new byte[1024]; 
           int numBytesRead = 0; 
           line.start(); 

           while (thread != null) { 
              try{ 
                 numBytesRead = playbackInputStream.read(data); 
                 line.write(data, 0,numBytesRead); 
              } catch (IOException e) {
               } 
           } 

           if (thread != null) { 
               line.drain(); 
           } 

           line.stop(); 
           line.close(); 
           line = null; 
       } 
}