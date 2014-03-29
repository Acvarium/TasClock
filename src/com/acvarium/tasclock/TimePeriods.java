package com.acvarium.tasclock;

import java.util.Vector;

import android.content.SharedPreferences.Editor;
import android.widget.ArrayAdapter;

public class TimePeriods{
	private Vector<TimePeriod> timePeriods = new Vector<TimePeriod>();
	private String tpID;
	public boolean tpStarted;
	private long sSum = 0L;

	
	public TimePeriods(String TimePeriodID) {
		tpID = TimePeriodID;
		tpStarted = false;
	}

	public void setID(String TimePeriodID) {
		tpID = TimePeriodID;
	}

	public String getID() {
		return tpID;
	}
	
	public void remove(int i){
		timePeriods.remove(i);
	}

	public void start() {
		long t = ((System.currentTimeMillis()));
		timePeriods.add(new TimePeriod(t, 0));
		tpStarted = true;
	}

	public void stop() {
		long t = ((System.currentTimeMillis()));
		timePeriods.lastElement().setEnd(t);
		tpStarted = false;
		sSum = getSumOfAllPeriods();
	}

	public void add(long startTime, long endTime) {
		if (endTime == 0)
			tpStarted = true;
		timePeriods.add(new TimePeriod(startTime, endTime));
		sSum = getSumOfAllPeriods();
	}

	public long getStartTime(int j) {
		return timePeriods.elementAt(j).getStart();
	}

	public long getEndTime(int j) {
		return timePeriods.elementAt(j).getEnd();
	}

	public void clear() {
		timePeriods.clear();
	}

	public int getSize() {
		return timePeriods.size();
	}

	public long getSumOfAllPeriods() {
		long sum = 0;

		if (tpStarted) {
			long lastDuration = ((System.currentTimeMillis()))-timePeriods.lastElement().getStart();
			sum = sSum + lastDuration;
		} else {
			for (TimePeriod p : timePeriods) {
				sum += p.getDuration();
			}
			sSum = sum;
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
			ed.putLong(String.valueOf(tpID + "_s_" + j),
					timePeriods.elementAt(j).getStart());
			ed.putLong(String.valueOf(tpID + "_e_" + j),
					timePeriods.elementAt(j).getEnd());
		}
	}

}
