package eProctor;
import java.util.*;
import java.io.*;
import java.text.*;

public class Recording {
	private Date startTime;
	private Date endTime;
	private Date date;
	
	public Recording(){
		try{
			//get date and time values
			Date currentDate;
			int year = Calendar.getInstance().get(Calendar.YEAR);
			int month = Calendar.getInstance().get(Calendar.MONTH)+1;//January ==0
			int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			currentDate = sdf.parse(year+"-"+month+"-"+day);
				
			//assign attributes
			date = currentDate;
			startTime = Calendar.getInstance().getTime();
		}
		catch(ParseException ex){
    		ex.getMessage();
    	}
	}

	//get and set methods
	public void setStartTime(Date s){
		this.startTime = s;
	}
	public Date getStartTime(){
		return this.startTime;
	}
	public void setEndTime(Date e){
		this.endTime = e;
	}
	public Date getEndTime(){
		return this.endTime;
	}
	public void setDate(Date d){
		this.date = d;
	}
	public Date getDate(){
		return this.date;
	}
	
	
}
