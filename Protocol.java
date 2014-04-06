package eProctor;

public class Protocol {


    //Communication Protocol
    public static final int CONNECT = 1;
    public static final int RECEIVEQUESTION = 2;
    public static final int CHECKJOINNO = 3;
    public static final int MSG = 4;
    public static final int START = 5;
    public static final int FINISH = 6;
    public static final int FINISHALL = 7;
    public static final int FINISHTIMER = 8;
    public static final int STUDENTREMOVAL = 9;
    public static final int STUDENTSENDANSWER = 10;
    public static final int ALLSENDVIDEO = 11;
    public static final int ALLSENDANSWER = 12;
    public static final int TERMINATESERVER = 13;


    //server IP
    public static final String serverAddr = "172.22.64.239";    // server host name //bryant
    public static final int serverPortNo = 2001;
    public static final String invigilatorAddr = "172.27.121.36"; //meng xing
    public static final int InvigilatorPortNo = 2050;

    public static final String webcamAddr = invigilatorAddr;
    public static final int webcamPort[] = {5000, 5001, 5002, 5003, 5004, 5005};

    public static final String audioAddr = invigilatorAddr;
    public static final int audioPort[] = {6000, 6001, 6002, 6003, 6004, 6005};

    public static final String videoTransferAddr = "172.22.64.239"; //bryant
    public static final int videoTransferPort = 3000;
    public static final String answerTransferAddr = "172.22.64.239";//bryant
    public static final int answerTransferPort = 3500;
    public static final String questionTransferAddr = "172.22.64.239";//bryant
    public static final int questionTransferPort = 3600;

    /*
    //server IP
    public static final String serverAddr = "127.0.0.1";    // server host name
    public static final int serverPortNo = 2001;
    public static final String invigilatorAddr = "127.0.0.1";
    public static final int InvigilatorPortNo = 2050;

    public static final String webcamAddr = invigilatorAddr;
    public static final int webcamPort[] = {5000, 5001, 5002, 5003, 5004, 5005};

    public static final String audioAddr = invigilatorAddr;
    public static final int audioPort[] = {6000, 6001, 6002, 6003, 6004, 6005};

    public static final String videoTransferAddr = "127.0.0.1";
    public static final int videoTransferPort = 3000;
    public static final String answerTransferAddr = "127.0.0.1";
    public static final int answerTransferPort = 3500;
    public static final String questionTransferAddr = "127.0.0.1";
    public static final int questionTransferPort = 3600;
    */

}
