package com.acvarium.tasclock;

public class TimePeriod {
	private long start,end;
	
	public TimePeriod(long startTime, long endTime){
		start = startTime;
		end = endTime;
	}
	public void setStart(long startTime){
		start = startTime;
	}
	
	public long getStart(){
		return start;
	}
	
	public long getEnd(){
		return end;
	}
	
	public void setEnd(long endTime){
		end = endTime;
	}	
	
	public long getDuration(){
		return end-start;
		
	}
}
