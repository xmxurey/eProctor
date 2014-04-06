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
    JButton button = null;
    ImageIcon imageIcon = null;
    ImageIcon imageIcon1 = null;

    public CountDown(long s, JLabel label, JButton button){
        this(s, label, button, null, null);
    }

    public CountDown(long s, JLabel label, JButton button, ImageIcon imageIcon, ImageIcon imageIcon1){
        this.sec = s;
        this.lblTimer = label;
        this.button = button;
        this.imageIcon = imageIcon;
        this.imageIcon1 = imageIcon1;
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

        if (timesUp == true){
            button.setEnabled( (!button.isEnabled()) );

            if (imageIcon != null){
                button.setIcon(imageIcon);
                button.setRolloverIcon(imageIcon1);
                button.setPressedIcon(imageIcon1);
            }

        }
    }


}