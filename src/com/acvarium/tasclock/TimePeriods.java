package com.acvarium.tasclock;

import java.util.Vector;

public class TimePeriods {
	private Vector<TimePeriod> timePeriods = new Vector<TimePeriod>();
	private String label;

	public TimePeriods(String label) {
		this.label = label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void remove(int i) {
		timePeriods.remove(i);
	}

	public void start() {
		long t = (System.currentTimeMillis());
		timePeriods.add(new TimePeriod(t, 0));
	}

	public void stop() {
		long t = ((System.currentTimeMillis()));
		timePeriods.lastElement().setEnd(t);
	}

	public void add(long startTime, long endTime) {
		timePeriods.add(new TimePeriod(startTime, endTime));
	}

	public long getStartTime(int j) {
		return timePeriods.elementAt(j).getStart();
	}

	public long getEndTime(int j) {
		return timePeriods.elementAt(j).getEnd();
	}

	public void setStartTime(int j, long stertTime) {
		timePeriods.elementAt(j).setStart(stertTime);
	}

	public void setEndTime(int j, long endTime) {
		timePeriods.elementAt(j).setEnd(endTime);
	}

	public void clear() {
		timePeriods.clear();
	}

	public int getSize() {
		return timePeriods.size();
	}

	public Boolean getState() {
		if(timePeriods.isEmpty())
			return false;
		if (timePeriods.lastElement().getEnd() == 0) {
			return true;
		} else
			return false;
	}

	public long getSumOfAllPeriods() {
		long sum = 0;
		if(timePeriods.isEmpty()){
			return 0;
		}
		for (TimePeriod p : timePeriods) {
			sum += p.getDuration();
		}
		if (timePeriods.lastElement().getEnd() == 0) {
			long lastDuration = ((System.currentTimeMillis()))
					- timePeriods.lastElement().getStart();
			sum = sum - timePeriods.lastElement().getDuration() + lastDuration;
		}

		return sum;
	}

	public long getSumOfPeriod(int i) {
		return timePeriods.elementAt(i).getDuration();
	}

}
