package eProctor;
import javax.swing.*;

//countdown timer
class CountDown implements Runnable{
    long sec;
    long HH;
    long MM;
    long SS;
    JLabel lblTimer = null;
    boolean timesUp = false;

    public CountDown(long s, JLabel label){
        sec = s;
        this.lblTimer = label;
    }
    public void run(){

        while(sec>=0){

            SS = sec % 60;
            MM = (sec/60) % 60;
            HH = sec/3600;
            try{
                Thread.sleep(1000);
            }
            catch (InterruptedException x) {
            }
            lblTimer.setText(HH + ":" + MM + ":" + SS);
            sec--;

//            System.out.println(HH + ":" + MM + ":" + SS);

        }
        lblTimer.setText("Times Up");
        timesUp = true;

    }

    public boolean isTimesUp(){
        return timesUp;
    }
}