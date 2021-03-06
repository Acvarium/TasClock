package com.acvarium.tasclock;

public class tpTask {

	private String label;
	private String comment;
	//private int state;
	private long period;
	private long status;
	private int orderNum;
	public TimePeriods timePeriods;

	public tpTask(String label, long period) {
		this.label = label;
		timePeriods = new TimePeriods(label);
		this.period = period;
		status = 0;
	}
	
	public int getOrderNum(){
		return orderNum;
	}

	public void setOrderNum(int orderNum){
		this.orderNum = orderNum;
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

	public void setStatus(long status) {
		this.status = status;
	}

	public long getStatus() {
		return this.status;
	}

	
	public String getLabel() {
		return label;
	}
}
