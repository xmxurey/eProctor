package eProctor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.text.*;
import java.io.*;
import java.net.*;


public class ExamHall {
    private ExamSlot examSlot;
    private String examHallID;
    private ArrayList inExam;

    public ExamHall(ExamSlot es, String ehID) {
        examSlot = es;
        examHallID = ehID;
        inExam = new ArrayList();
    }

    public ExamHall() {

    }

    //get and set methods
    public void setExamSlot(ExamSlot es) {
        this.examSlot = es;
    }

    public ExamSlot getExamSlot() {
        return this.examSlot;
    }

    public void setExamHallID(String ehID) {
        this.examHallID = ehID;
    }

    public String getExamHallID() {
        return this.examHallID;
    }

    public void setAttendanceList(ArrayList al) {
        this.inExam = al;
    }

    public ArrayList getAttendanceList() {
        return this.inExam;
    }

}
