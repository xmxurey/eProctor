package eProctor;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.text.*;
import java.nio.file.Files;

import javax.swing.*;

public class Server extends Thread {
    int connectors = 50;
    Socket client;
    DataInputStream in;
    DataOutputStream out;

    String examHallID;
    int userID;
    boolean isStudent;
    ArrayList<Session> ExamHallParticipantList;


    public Server() {
        ExamHallParticipantList = new ArrayList();
    }

    public void run() {
        try {
            ServerSocket ss = new ServerSocket(Protocol.serverPortNo, connectors);
            System.out.println("Waiting for client connection .. ");

            boolean allow = false;
            while (true) {
                client = ss.accept();
                in = new DataInputStream(client.getInputStream());
                out = new DataOutputStream(client.getOutputStream());
                examHallID = in.readUTF();
                userID = in.readInt();
                isStudent = in.readBoolean();
                Session s = new Session(client, examHallID, userID, isStudent);
                allow = checkLogin(s, isStudent);
                out.writeBoolean(allow);
                if (allow) {
                    ExamHallParticipantList.add(s);
                    s.start();
                    System.out.println("Client successfully connected.");
                } else {
                    client.close();
                }


            }
        } catch (Exception e) {
            System.out.println("Could not listen on port: " + Protocol.serverPortNo + ".");

        }
    }

    /*
     connect exam
     */
    private synchronized boolean checkLogin(Session session, boolean isStudent) {
        boolean allow = true;

        //check if user is inside examhall already
        for (Session s : ExamHallParticipantList) {
            if (s.getExamHallID().equals(session.examHallID)) {
                if (s.userID == session.userID)
                    allow = false;
            }
        }
        if (allow) {
            if (isStudent) {
                allow = false;
                for (Session s : ExamHallParticipantList) {
                    if (s.getExamHallID().equals(session.examHallID)) {
                        if (s.isStudent == false)
                            allow = true;
                    }
                }
            }
        }
        return allow;
    }

    private synchronized void connectInvExam(String examHallID, int userID) {
        //check if userID input is prof
        boolean student = true;
        for (Session s : ExamHallParticipantList) {
            if (s.getExamHallID().equals(examHallID) && s.getUserID() == userID) {
                student = s.isStudent;
                break;
            }
        }

        if (student) {
            for (Session s : ExamHallParticipantList) {
                if (s.getExamHallID().equals(examHallID)) {
                    if (s.isStudent == false) {
                        //isInvigilator
                        s.writeInt(Protocol.CONNECT);
                        s.writeInt(userID);
                        break;
                    }
                }
            }
        }
    }

    private synchronized int checkJoinNo(String eID) {
        int count = 0;
        for (Session s : ExamHallParticipantList) {
            if (s.getExamHallID().equals(eID)) {
                count++;
            }
        }
        return count;
    }

    /*
      Send Msg
     */
    private synchronized void broadcast(String examHallID) {
        for (Session s : ExamHallParticipantList) {

            if (s.getExamHallID().equals(examHallID)) {
                s.writeInt(Protocol.MSG);
                try {
                    BufferedReader bIn = new BufferedReader(new FileReader("eProctorServer/EventLog/ExamHall=" + examHallID + ".txt"));
                    String msg = "";
                    String str = "";

                    while ((str = bIn.readLine()) != null) {
                        msg += str + " \n";
                    }
                    s.writeUTF(msg);
                } catch (IOException ex) {
                    System.out.println(" Exception reading Streams: " + ex);
                    break;
                }
                //send msg here
            }
        }
    }

    /*
      Start Exam
     */
    //method to invoke all students to start exam
    private synchronized void startExam(String e) {
        String examHallID = e;
        for (Session s : ExamHallParticipantList) {

            if (s.getExamHallID().equals(examHallID)) {
                s.writeInt(Protocol.START);
            }
        }
    }

    /*
      Finish Exam
     */
    private synchronized void endTimer(int userID, String e) {
        String examHallID = e;
        for (Session s : ExamHallParticipantList) {

            if (s.getExamHallID().equals(examHallID) && userID == s.getUserID()) {
                s.writeInt(Protocol.FINISHTIMER);
            }
        }
    }

    private synchronized void allSendAnswer(String e) {
        for (int i = 0; i < ExamHallParticipantList.size(); i++) {
            Session s = (Session) ExamHallParticipantList.get(i);
            if (s.getExamHallID().equals(examHallID)) {
                if (s.isStudent) {
                    s.writeInt(Protocol.ALLSENDANSWER);
                    receiveAnswer(s.examHallID, s.userID);
                }
            }
        }
    }

    //receive Answer File
    private void receiveAnswer(String eID, int uID) {
        BufferedInputStream bis;
        BufferedOutputStream bos;
        byte[] data;
        try {
            ServerSocket ss = new ServerSocket(Protocol.answerTransferPort, 1);
            Socket socket = ss.accept();

            FileOutputStream fos = new FileOutputStream("eProctorServer/ExamAnswerSheet/ExamHall=" + eID + "_UserID=" + uID + ".txt");
            bos = new BufferedOutputStream(fos);
            byte[] buffer = new byte[2022386];
            int count;
            InputStream in = socket.getInputStream();
            while ((count = in.read(buffer)) >= 0) {

                fos.write(buffer, 0, count);
            }
            //send to NTU server
            File answerSource = new File("eProctorServer/ExamAnswerSheet/ExamHall=" + eID + "_UserID=" + uID + ".txt");
            File answerDest = new File("NTUServer/ExamAnswer/ExamHall=" + eID + "_UserID=" + uID + ".txt");
            transferFile(answerSource, answerDest);

            fos.close();

            socket.close();
            ss.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //terminate client session
    private synchronized void terminateSession(int userID, String examHallID) {
        Iterator<Session> ite = ExamHallParticipantList.iterator();

        while (ite.hasNext()) {
            Session s = ite.next();
            if (s.getExamHallID().equals(examHallID) && userID == s.getUserID()) {

                System.out.println("Session for UserID=" + s.userID + " has ended.");
                if (s.isStudent) {
                    s.writeInt(Protocol.TERMINATESERVER);
                }

                ite.remove();
                s.closeSession();
            }
        }
        for (Session s : ExamHallParticipantList) {

            if (s.getExamHallID().equals(examHallID) && userID == s.getUserID()) {

                System.out.println("Session for UserID=" + s.userID + " has ended.");
                s.closeSession();
                ExamHallParticipantList.remove(s);
            }
        }
    }

    private synchronized void endStudent(int userID, String examHallID) {
        for (Session s : ExamHallParticipantList) {
            if (s.examHallID.equals(examHallID) && !s.isStudent) {
                s.writeInt(Protocol.STUDENTREMOVAL);
                s.writeUTF(examHallID);
                s.writeInt(userID);
                break;
            }
        }
    }

    /*
      Others
     */
    //method to return list of students in a particular examhall
    private synchronized ArrayList getStudentExamList(String e) {
        String examHallID = e;
        ArrayList participantList = new ArrayList();
        for (Session s : ExamHallParticipantList) {
            if (s.getExamHallID().equals(examHallID)) {
                participantList.add(s.userID);
            }
        }

        return participantList;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    //Send files to server
    private void transferFile(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }

    //Session Class
    class Session extends Thread {
        private Socket client;
        private String examHallID;
        private int userID = 0;
        private boolean isStudent = true;

        //check if user can enter examhall
        String url = "jdbc:mysql://" + Protocol.serverAddr + ":3306/";
        String dbName = "cz2006?";
        String driver = "com.mysql.jdbc.Driver";
        String username = "user=root&";
        String password = "password=pass";


        public Session(Socket s, String e, int u, boolean i) {
            client = s;
            examHallID = e;
            userID = u;
            isStudent = i;
        }

        public String getExamHallID() {
            return examHallID;
        }

        public int getUserID() {
            return userID;
        }

        public void run() {
            try {
                DataInputStream in = new DataInputStream(client.getInputStream());
                DataOutputStream out = new DataOutputStream(client.getOutputStream());

                Class.forName(driver);
                Connection conn = DriverManager.getConnection(url + dbName + username + password);
                Statement st = conn.createStatement();

                while (true) {
                    int code = in.readInt();
                    System.out.println("Code=" + code);
                    if (code == Protocol.CONNECT) {

                        connectExam(examHallID, userID, isStudent);
                        connectInvExam(examHallID, userID);

                        System.out.println("UserID = " + userID + " has entered examHallID= " + examHallID);

                        if (!isStudent) {
                            File examQuestionPaperSource = new File("ntuserver/ExamQuestion/ExamHall=" + examHallID + ".pdf");
                            File examQuestionPaperDest = new File("eProctorServer/ExamQuestion/ExamHall=" + examHallID + ".pdf");
                            transferFile(examQuestionPaperSource, examQuestionPaperDest);
                        } else if (isStudent) {
                            sendQuestion();
                            out.writeInt(Protocol.CONNECT);
                        }
                    } else if (code == Protocol.CHECKJOINNO) {
                        int joinNo = checkJoinNo(examHallID) - 2;
                        out.writeInt(joinNo);
                    } else if (code == Protocol.RECEIVEQUESTION) {
                        sendQuestion();
                    } else if (code == Protocol.MSG) {
                        String msg = in.readUTF();

                        //get userName
                        ResultSet res = st.executeQuery("SELECT user.Name from user where userID='" + userID + "'");

                        String name = "";

                        while (res.next()) {
                            name = res.getString("Name");
                        }
                        sendMsg(msg, name);

                    } else if (code == Protocol.START) {
                        startExam(getExamHallID());
                        startEventLog();

                    } else if (code == Protocol.FINISHALL) {
                        //send list of students in examhall back to examhallManager
                        ArrayList participantList = new ArrayList();
                        participantList = getStudentExamList(getExamHallID());

                        int uID = 0;
                        for (int i = 0; i < participantList.size(); i++) {
                            uID = (Integer) participantList.get(i);

                            endEventLog(uID, true);
                        }

                        //transfer files from eProctor Server to NTU Server
                        //transfer eventlog
                        File eventLogSource = new File("eProctorServer/EventLog/ExamHall=" + examHallID + ".txt");
                        File eventLogDest = new File("NTUServer/EventLog/ExamHall=" + examHallID + ".txt");
                        transferFile(eventLogSource, eventLogDest);

                        sendList(participantList);

                        //terminate Timer
                        int userID = 0;
                        for (int i = 0; i < participantList.size(); i++) {
                            userID = (Integer) participantList.get(i);
                            endTimer(userID, examHallID);
                        }

                        //terminate session
                        for (int i = 0; i < participantList.size(); i++) {
                            userID = (Integer) participantList.get(i);
                            terminateSession(userID, examHallID);
                        }


                    } else if (code == Protocol.ALLSENDVIDEO) {
                        //transfer Recording
                        out.writeInt(Protocol.ALLSENDVIDEO);
                        receiveVideo();
                    } else if (code == Protocol.ALLSENDANSWER) {
                        //transfer examanswers
                        allSendAnswer(examHallID);

                    } else if (code == Protocol.STUDENTREMOVAL) {

                        String examID = in.readUTF();
                        int uID = in.readInt();
                        String reason = in.readUTF();

                        //get name of student
                        ResultSet res = st.executeQuery("SELECT user.Name from user where userID='" + uID + "'");

                        String name = "";

                        while (res.next()) {
                            name = res.getString("Name");
                        }
                        terminateEventLog(reason, name);


                        endStudent(uID, examID);

                        endTimer(uID, examHallID);
                        terminateSession(uID, examID);
                    } else if (code == Protocol.STUDENTSENDANSWER) {
                        studentSendAnswer(examHallID, userID);
                    } else if (code == Protocol.FINISH) {
                        //send list of students in examhall back to examhallManager
                        ArrayList participantList = new ArrayList();

                        endEventLog(userID, false);

                        endStudent(userID, examHallID);

                        //terminate Timer
                        endTimer(userID, examHallID);

                        //terminate session
                        terminateSession(userID, examHallID);


                    }
                }

            } catch (IOException e) {
                System.out.println("IO Error: " + e.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*
          Enter Exam
         */
        //Connect to exam
        public void connectExam(String examHallID, int userID, boolean isStudent) {
            try {
                Class.forName(driver);
                Connection conn = DriverManager.getConnection(url + dbName + username + password);
                Statement st = conn.createStatement();

                //get userName
                ResultSet res = st.executeQuery("SELECT user.Name from user where userID='" + userID + "'");

                String name = "";

                while (res.next()) {
                    name = res.getString("Name");
                }

                PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("eProctorServer/EventLog/ExamHall=" + examHallID + ".txt", true)));

                //no eventlog exist. Need to create eventlog
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();

                writer.println(name + " has joined the examHall at " + cal.getTime());
                writer.close();

                broadcast(getExamHallID());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        //send Question File
        private void sendQuestion() {
            BufferedInputStream bis;
            BufferedOutputStream bos;
            byte[] data;
            try {
                ServerSocket ss = new ServerSocket(Protocol.questionTransferPort, 1);
                out.writeInt(Protocol.RECEIVEQUESTION);
                Socket socket = ss.accept();

                String fileToSend = "eProctorServer/ExamQuestion/ExamHall=" + examHallID + ".pdf";
                File myFile = new File(fileToSend);

                int count;
                byte[] buffer = new byte[2022386];

                OutputStream out = socket.getOutputStream();
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(myFile));
                while ((count = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, count);
                    out.flush();
                }
                socket.close();
                ss.close();

            } catch (Exception ex) {
                System.out.println("Exception: " + ex.toString());
                ex.printStackTrace();
            }
        }

        /*
          Communication during exam
         */
        //send msg
        public void sendMsg(String msg, String name) {
            try {
                //UPDATE EVENTlOG
                PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("eProctorServer/EventLog/ExamHall=" + examHallID + ".txt", true)));

                //no eventlog exist. Need to create eventlog
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();


                writer.println(name + " (" + cal.getTime() + ") says: " + msg);
                writer.close();

                broadcast(getExamHallID());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        //send to client informing the communication protocol
        private boolean writeInt(int code) {

            // write the message to the stream
            try {
                DataOutputStream sOutput = new DataOutputStream(client.getOutputStream());
                sOutput.writeInt(code);
            }
            // if an error occurs, do not abort just inform the user
            catch (IOException e) {
                System.out.println("Error sending message");
                System.out.println(e.toString());
            }
            return true;
        }

        private boolean writeUTF(String msg) {

            // write the message to the stream
            try {
                DataOutputStream sOutput = new DataOutputStream(client.getOutputStream());
                sOutput.writeUTF(msg);
            }
            // if an error occurs, do not abort just inform the user
            catch (IOException e) {
                System.out.println("Error sending message");
                System.out.println(e.toString());
            }
            return true;
        }

        /*
          Start Exam
         */
        //Update eventLog
        private void startEventLog() {
            try {
                PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("eProctorServer/EventLog/ExamHall=" + examHallID + ".txt", true)));
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();

                writer.println("ExamHallID = " + examHallID + " has officially started at " + cal.getTime() + ".");
                writer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        /*
          Finish Exam
         */
        //close session
        private void closeSession() {
            try {
                in.close();
                out.close();
                client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //student send answer to server

        public void studentSendAnswer(String examHallID, int userID) {
            try {
                out.writeInt(Protocol.STUDENTSENDANSWER);
                receiveAnswer(examHallID, userID);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

        //receive Video File
        private void receiveVideo() {
            BufferedInputStream bis;
            BufferedOutputStream bos;
            byte[] data;
            try {
                ServerSocket ss = new ServerSocket(Protocol.videoTransferPort, 1);
                Socket socket = ss.accept();

                FileOutputStream fos = new FileOutputStream("eProctorServer/ExamRecording/ExamHall=" + examHallID + ".mov");
                bos = new BufferedOutputStream(fos);
                byte[] buffer = new byte[2022386];
                int count;
                InputStream in = socket.getInputStream();
                while ((count = in.read(buffer)) >= 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();

                socket.close();
                ss.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        //send to invigilator participantList
        private void sendList(ArrayList participantList) {
            try {
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                int userID = 0;

                out.writeInt(Protocol.FINISHALL);
                out.writeUTF(examHallID);
                out.writeInt(participantList.size());

                for (int i = 0; i < participantList.size(); i++) {
                    userID = (Integer) participantList.get(i);
                    out.writeInt(userID);
                }
            }
            // if an error occurs, do not abort just inform the user
            catch (IOException e) {
                System.out.println("Error sending participantList");
            }
        }

        //update eventlog
        private void endEventLog(int userID, boolean examHallEnd) {
            try {
                //update eventlog for students whole takable = 1
                //check if user can enter examhall
                String url = "jdbc:mysql://" + Protocol.serverAddr + ":3306/";
                String dbName = "cz2006?";
                String driver = "com.mysql.jdbc.Driver";
                String username = "user=root&";
                String password = "password=pass";

                Class.forName(driver);
                Connection conn = DriverManager.getConnection(url + dbName + username + password);
                Statement st = conn.createStatement();


                PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("eProctorServer/EventLog/ExamHall=" + examHallID + ".txt", true)));
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();

                ResultSet res = st.executeQuery("select user.name, moduleAttendance.takable from moduleAttendance inner join User on User.userID = moduleattendance.userID " +
                        "WHERE moduleAttendance.userID='" + userID + "' and moduleAttendance.examHallID='" + examHallID + "'");

                int takable = 0;
                String name = "";
                while (res.next()) {
                    takable = res.getInt("takable");
                    name = res.getString("name");
                }

                if (takable == 1) {
                    writer.println(name + " has finished exam at " + cal.getTime() + ".");
                }
                if (examHallEnd)
                    writer.println("ExamHallID = " + examHallID + " has officially ended at " + cal.getTime() + ".");
                writer.close();

                broadcast(examHallID);

            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        /*
         * Terminate Exam
         */
        //Method to update eventlog for termination
        public void terminateEventLog(String reason, String name) {
            try {
                //UPDATE EVENTlOG
                PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("eProctorServer/EventLog/ExamHall=" + examHallID + ".txt", true)));

                //no eventlog exist. Need to create eventlog
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();


                writer.println(name + " is terminated at " + cal.getTime() + ". Reason: " + reason);
                writer.close();

                broadcast(getExamHallID());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}




