package eProctor;

import java.util.*;

public class Module {
    private String moduleID;
    private String desc;
    private float AU;
    private ArrayList examSlotList;

    public Module(String m, String d, float a) {
        moduleID = m;
        desc = d;
        AU = a;
        examSlotList = new ArrayList();
    }

    //get and set methods
    public void setModuleID(String m) {
        this.moduleID = m;
    }

    public String getModuleID() {
        return this.moduleID;
    }

    public void setDesc(String d) {
        this.desc = d;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setAU(float a) {
        this.AU = a;
    }

    public float getAU() {
        return this.AU;
    }

    public void setEmailSlotList(ArrayList e) {
        this.examSlotList = e;
    }

    public ArrayList getExamSlotList() {
        return this.examSlotList;
    }


}
