package eProctor;

import java.util.*;

public class Invigilator extends User {
    private String faculty;
    private ArrayList examHallList;

    public Invigilator(int u, String p, String e, String n, String f) {
        super(u, p, e, n);
        faculty = f;
        examHallList = new ArrayList();
    }

    //get and set methods
    public void setFaculty(String f) {
        this.faculty = f;
    }

    public String getFaculty() {
        return this.faculty;
    }

    public void setExamHallList(ArrayList e) {
        this.examHallList = e;
    }

    public ArrayList getMExamHallList() {
        return this.examHallList;
    }
}
