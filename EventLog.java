package eProctor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EventLog {
	private int eventLogID;
	private Date date;
	private String content;
	private int createTime;
	private ArrayList messageList;
	
	public EventLog(int elID, Date d, String c, int ct){
		try{
			eventLogID = elID;
			content = c;
			messageList = new ArrayList();
			
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
			createTime = hour*100 + min;
		}
		catch(ParseException ex){
    		ex.getMessage();
    	}
	}
	
	//get and set methods
	public void setEventLogID(int elID){
		this.eventLogID = elID;
	}
	public int getEventLogID(){
		return this.eventLogID;
	}
	public void setDate(Date d){
		this.date = d;
	}
	public Date getDate(){
		return this.date;
	}
	public void setContent(String c){
		this.content = c;
	}
	public String getContent(){
		return this.content;
	}
	public void setCreateTime(int ct){
		this.createTime = ct;
	}
	public int getCreateTime(){
		return this.createTime;
	}
	public void setMessageList(ArrayList m){
		this.messageList = m;
	}
	public ArrayList getMessageList(){
		return this.messageList;
	}	
	
}
