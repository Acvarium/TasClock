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
		long duration = 0;
		if(end == 0){
			duration = System.currentTimeMillis() - start;
		}else{
			duration = end - start;
		}
		
		return duration;
		
	}
}
