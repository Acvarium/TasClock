package com.acvarium.tasclock;

public class tpTask {

	private String label;
	private String comment;
	private int state;
	private long period;
	public TimePeriods timePeriods;

	public tpTask(String label, long period) {
		this.label = label;
		timePeriods = new TimePeriods(label);
		state = 0;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public long getPeriod() {
		return period;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getLabel() {
		return label;
	}
}
