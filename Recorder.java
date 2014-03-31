package eProctor;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.MediaLocator;

/**
 * Main class that starts the Recording process of EasyCapture.
 * 
 * @author Senthil Balakrishnan
 */
public class Recorder {

	/**
	 * Screen Width.
	 */
	public static int screenWidth = (int) Toolkit.getDefaultToolkit()
			.getScreenSize().getWidth();

	/**
	 * Screen Height.
	 */
	public static int screenHeight = (int) Toolkit.getDefaultToolkit()
			.getScreenSize().getHeight();

	/**
	 * Interval between which the image needs to be captured.
	 */
	public static int captureInterval = 50;

	/**
	 * Temporary folder to store the screenshot.
	 */
	public static String store = "tmp";

	/**
	 * Status of the recorder.
	 */
	public static boolean record = false;

	/**
	 * 
	 */
	
	public Recorder(){}
	
	public static void startRecord() {
		Thread recordThread = new Thread() {
			@Override
			public void run() {
				Robot rt;
				int cnt = 0;
				try {
					rt = new Robot();
					while (cnt == 0 || record) {
						BufferedImage img = rt
								.createScreenCapture(new Rectangle(screenWidth,
										screenHeight));
						ImageIO.write(img, "jpeg", new File("./"+store+"/"
								+ System.currentTimeMillis() + ".jpeg"));
						if (cnt == 0) {
							record = true;
							cnt = 1;
						}
						// System.out.println(record);
						Thread.sleep(captureInterval);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		recordThread.start();
	}

	/**
	 * @throws MalformedURLException 
	 * 
	 */
	public static void makeVideo(String movFile) throws MalformedURLException {
		System.out
				.println("#### Easy Capture making video, please wait!!! ####");
		JpegImagesToMovie imageToMovie = new JpegImagesToMovie();
		Vector<String> imgLst = new Vector<String>();
		File f = new File(store);
		File[] fileLst = f.listFiles();
		for (int i = 0; i < fileLst.length; i++) {
			imgLst.add(fileLst[i].getAbsolutePath());
		}
		// Generate the output media locators.
		MediaLocator oml;
		if ((oml = imageToMovie.createMediaLocator(movFile)) == null) {
			System.err.println("Cannot build media locator from: " + movFile);
			System.exit(0);
		}
		imageToMovie.doIt(screenWidth, screenHeight, (1000 / captureInterval),
				imgLst, oml);

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public void startRecording() throws Exception {
		System.out.println("######### Starting Easy Capture Recorder #######");
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		System.out.println("Your Screen [Width,Height]:" + "["
				+ screen.getWidth() + "," + screen.getHeight() + "]");
		Scanner sc = new Scanner(System.in);
		
		File f = new File(store);
		if(!f.exists()){
			f.mkdir();
		}
		startRecord();
		System.out
		.println("\nEasy Capture is recording now!!!!!!!");
	}
	
	public void endRecording() throws Exception {
		record = false;
		System.out.println("Easy Capture has stopped.");
		makeVideo(System.currentTimeMillis()+".mov");
	}
}
