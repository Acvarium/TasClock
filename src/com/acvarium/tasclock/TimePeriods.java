package com.acvarium.tasclock;

import java.util.Vector;

import android.content.SharedPreferences.Editor;

public class TimePeriods {
	private Vector<TimePeriod> timePeriods = new Vector<TimePeriod>();
	private String tpID;

	public TimePeriods(String TimePeriodID) {
		tpID = TimePeriodID;
	}

	public void setID(String TimePeriodID) {
		tpID = TimePeriodID;
	}

	public String getID() {
		return tpID;
	}

	public void add(long startTime, long endTime) {
		timePeriods.add(new TimePeriod(startTime, endTime));
	}

	public long getStartTime(int j) {
		return timePeriods.elementAt(j).start;
	}

	public long getEndTime(int j) {
		return timePeriods.elementAt(j).end;
	}
	public void clear(){
		timePeriods.clear();
	}

	public int getQuantity() {
		return timePeriods.size();
	}
	
	public long getSumOfAllPeriods() {
		long sum = 0;

		for (TimePeriod p : timePeriods) {
			sum += p.getDuration();
		}
		return sum;
	}

	public long getSumOfPeriod(int i) {
		return timePeriods.elementAt(i).getDuration();
	}

	public void saveData(Editor ed) {
		ed.putLong("tpnum", timePeriods.size());
		ed.putString("tpID", tpID);
		
		for (int j = 0; j < timePeriods.size(); j++) {
			ed.putLong(String.valueOf(tpID + "_s_" + j), timePeriods.elementAt(j).start);
			ed.putLong(String.valueOf(tpID + "_e_" + j), timePeriods.elementAt(j).end);
		}
	}

}
