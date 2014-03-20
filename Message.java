package eProctor;
import java.util.*;
import java.text.*;

public class Message {
	private int messageID;
	private String sender;
	private String content;
	private Date date;
	private int time;
	
	public Message(int mID, String s, String c){
		try{
			messageID = mID;
			sender = s;
			content = c;
		
			//get date and time values
			Date currentDate;
			int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			int min = Calendar.getInstance().get(Calendar.MINUTE);
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int month = Calendar.getInstance().get(Calendar.MONTH)+1;//January ==0
			int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			currentDate = sdf.parse(year+"-"+month+"-"+day);
				
			//assign attributes
			date = currentDate;
			time = hour*100 + min;
		}
		catch(ParseException ex){
    		ex.getMessage();
    	}
	}
	
	//get and set methods
	public void setMessageID(int mID){
		this.messageID = mID;
	}
	public int getMessageID(){
		return this.messageID;
	}
	public void setSender(String s){
		this.sender = s;
	}
	public String getSender(){
		return this.sender;
	}
	public void setContent(String c){
		this.content = c;
	}
	public String getContent(){
		return this.content;
	}
	public void setDate(Date d){
		this.date = d;
	}
	public Date getDate(){
		return this.date;
	}
	public void setTime(int t){
		this.time = t;
	}
	public int getTime(){
		return this.time;
	}		
	
}
