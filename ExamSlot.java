package eProctor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.io.*;

public class ExamSlot {
    private String slotID;
    private int noOfExamHalls;
    private Date startTime;
    private Date endTime;
    private Date date;

    public ExamSlot(String s, int n, Date st, Date et, Date d) {
        slotID = s;
        noOfExamHalls = n;
        startTime = st;
        endTime = et;
        date = d;
    }

    //get and set methods
    public void setSlotID(String s) {
        this.slotID = s;
    }

    public String getSlotID() {
        return this.slotID;
    }

    public void setNoOfExamHalls(int n) {
        this.noOfExamHalls = n;
    }

    public int getNoOfExamHalls() {
        return this.noOfExamHalls;
    }

    public void setStartTime(Date st) {
        this.startTime = st;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setEndTime(Date et) {
        this.endTime = et;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setDate(Date d) {
        this.date = d;
    }

    public Date getDate() {
        return this.date;
    }

}
