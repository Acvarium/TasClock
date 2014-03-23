package com.acvarium.tasclock;

public class TimePeriod {
	public long start,end;
	
	public TimePeriod(long startTime, long endTime){
		start = startTime;
		end = endTime;
	}
	public long getDuration(){
		return end-start;
		
	}
}
