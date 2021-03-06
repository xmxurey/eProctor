package eProctor;

import com.sun.pdfview.PDFFile;

import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import javax.swing.*;

/**
 * An example of using the PagePanel class to show PDFs. For more advanced
 * usage including navigation and zooming, look at the com.sun.pdfview.PDFViewer class.
 *
 * @author joshua.marinacci@sun.com
 */
public class PDFDisplayManager {

    public static PDFFile setup(String examHallID) {
        try {
//            set up the frame and panel
//            JFrame frame = new JFrame("PDF Test");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//            PagePanel panel = new PagePanel();
//            frame.add(panel);
//            frame.pack();
//            frame.setVisible(true);
//
            //load a pdf from a byte buffer
            File file = new File("Local/ExamQuestion/ExamHall=" + examHallID + ".pdf");
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            PDFFile pdffile = new PDFFile(buf);

            return pdffile;
            // show the first page

//            PDFPage page = pdffile.getPage(0);
//            panel.showPage(page);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void main(String[] args) {
        //PDFDisplayManager.setup();

    }
}
    