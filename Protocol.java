package eProctor;
public class Protocol {


    //Communication Protocol
    public static final int CONNECT = 1;
    public static final int RECEIVEQUESTION = 2;
    public static final int MSG = 3;
    public static final int START = 4;
    public static final int FINISH = 5;
    public static final int FINISHALL = 6;
    public static final int FINISHTIMER = 7;
    public static final int STUDENTREMOVAL = 8;
    public static final int STUDENTSENDANSWER = 9;
    public static final int ALLSENDVIDEO = 10;
    public static final int ALLSENDANSWER = 11;
    public static final int TERMINATESERVER = 12;
   
    /*
    //server IP
    public static final String serverAddr = "172.22.104.25";    // server host name
    public static final int serverPortNo = 2001;
    public static final String invigilatorAddr = "172.22.104.25";
    public static final int InvigilatorPortNo = 2050;

    public static final String webcamAddr = invigilatorAddr;
    public static final int webcamPort = 5000;

    public static final String audioAddr = invigilatorAddr;
    public static final int audioPort = 6000;

    public static final String transferAddr = "172.22.104.25";
    public static final int videoTransferPort = 3000;
    public static final int answerTransferPort = 3500;
    public static final int questionTransferPort = 3600;
	*/
    
    //server IP
    public static final String serverAddr = "127.0.0.1";    // server host name
    public static final int serverPortNo = 2001;
    public static final String invigilatorAddr = "127.0.0.1";
    public static final int InvigilatorPortNo = 2050;

    public static final String webcamAddr = invigilatorAddr ;
    public static final int webcamPort = 5000;

    public static final String audioAddr = invigilatorAddr;
    public static final int audioPort = 6000;

    public static final String transferAddr = "127.0.0.1";
    public static final int videoTransferPort = 3000;
    public static final int answerTransferPort = 3500;
    public static final int questionTransferPort = 3600;
    
	
}
