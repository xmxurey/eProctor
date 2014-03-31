package eProctor;

import java.awt.Component;
import java.util.Vector;

import javax.media.Format;
import javax.media.Manager;
import javax.media.Player;
import javax.media.format.VideoFormat;
import javax.swing.JFrame;

public class Camera extends JFrame implements Runnable{
	
    Vector<javax.media.CaptureDeviceInfo>  deviceList=javax.media.CaptureDeviceManager.getDeviceList(null);
    javax.media.MediaLocator mediaLocator=null;
    Player player=null;
    
    public Camera() {
        this.setTitle("Test");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(400,400);
    }
    
    public void run(){
    	this.initCamera();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    public void initCamera(){

        javax.media.format.VideoFormat currentFormat = null;
        for(int i=0;i<deviceList.size();i++){
            System.out.println("Device Name:"+this.deviceList.get(i).getName());
            if(deviceList.get(i).getName().startsWith("vfw")){
                Format [] vedioFormat=deviceList.get(i).getFormats();
                for(int j=0;j<vedioFormat.length;i++){
                    if(vedioFormat[i] instanceof javax.media.format.VideoFormat){
                        currentFormat=(VideoFormat) vedioFormat[i];
                        break;
                    }
                }

                if(currentFormat==null){
                    System.out.println("Error!");
                }
                this.mediaLocator=deviceList.get(i).getLocator();
                System.out.println("Device Recognizer:"+this.mediaLocator);
                this.createPlayerByMediaLocator();
                break;
            }
        }
    }
    
    public void createPlayerByMediaLocator(){
        try {
            System.out.println(this.mediaLocator);
            player   =   Manager.createRealizedPlayer(this.mediaLocator);
            player.start();
            Component   comp;
            if   ((comp   =   player.getVisualComponent())   !=   null) {
                this.getContentPane().add(comp, "Center");
            }
        } catch   (Exception   e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
    	Camera camera = new Camera();
    	new Thread(camera).start();
    }
}