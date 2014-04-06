package eProctor;

import java.util.*;

public class ExamPaper {
    private int examID;
    private String examFile;
    private int year;
    private int semester;

    public ExamPaper(int e, String ef, int y, int s) {
        examID = e;
        examFile = ef;
        year = y;
        semester = s;
    }

    //get and set methods
    public void setExamID(int e) {
        this.examID = e;
    }

    public int getExamID() {
        return this.examID;
    }

    public void setExamFile(String ef) {
        this.examFile = ef;
    }

    public String getExamFile() {
        return this.examFile;
    }

    public void setYear(int y) {
        this.year = y;
    }

    public int getYear() {
        return this.year;
    }

    public void setSemester(int s) {
        this.semester = s;
    }

    public int getSemester() {
        return this.semester;
    }

}
