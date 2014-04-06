package eProctor;

import java.util.*;

public class ModuleAttendance {
    private Student student;
    private Module module;
    private ExamHall examHall;
    private boolean takable;
    private int year;
    private int semester;

    public ModuleAttendance(Student s, Module m, ExamHall e, int y, int sem) {
        student = s;
        module = m;
        examHall = e;
        takable = true;
        year = y;
        semester = sem;
    }

    public ModuleAttendance(Student s, Module m, int y, int sem) {
        student = s;
        module = m;
        examHall = null;
        takable = true;
        year = y;
        semester = sem;
    }

    //get and set methods
    public void setStudent(Student s) {
        this.student = s;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setModule(Module m) {
        this.module = m;
    }

    public Module getModule() {
        return this.module;
    }

    public void setExamHall(ExamHall e) {
        this.examHall = e;
    }

    public ExamHall getExamHall() {
        return this.examHall;
    }

    public void setTakable(boolean t) {
        this.takable = t;
    }

    public boolean isTakable() {
        return this.takable;
    }

    public void setYear(int y) {
        this.year = y;
    }

    public int getYear() {
        return this.year;
    }

    public void setSemester(int sem) {
        this.semester = sem;
    }

    public int getSemester() {
        return this.semester;
    }
}
