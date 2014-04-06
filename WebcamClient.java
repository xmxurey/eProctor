package eProctor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.sun.image.codec.jpeg.JPEGCodec;

public class WebcamClient extends Thread {
    private Dimension screenSize;
    private Rectangle rectangle;
    private Robot robot;
    private Thread cam;
    int port;


    public WebcamClient(int p) {
        port = p;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        rectangle = new Rectangle(screenSize);
        try {
            robot = new Robot();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        //cam = new Thread();
        //cam.start();
    }

    public void run() {
        ZipOutputStream os = null;
        Socket socket = null;
        while (true) {
            try {
                //socket = new Socket("127.0.0.1", 5000);
                //socket = new Socket(Protocol.webcamAddr, Protocol.webcamPort);
                socket = new Socket(Protocol.webcamAddr, Protocol.webcamPort[port]);
                BufferedImage image = robot.createScreenCapture(rectangle);

                int width = image.getWidth();
                int height = image.getHeight();

                width = width / 3;
                height = height / 3;

                BufferedImage newImage = new BufferedImage(width, height, image.getType());
                Graphics g = newImage.getGraphics();
                g.drawImage(image, 0, 0, width, height, null);
                g.dispose();

                os = new ZipOutputStream(socket.getOutputStream());

                os.setLevel(9);
                os.putNextEntry(new ZipEntry("test.jpg"));
                JPEGCodec.createJPEGEncoder(os).encode(newImage);
                os.close();
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (Exception ioe) {
                    }
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        //new WebcamClient().start(); 
    }
} 