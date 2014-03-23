package com.acvarium.tasclock;

public class tpTask {

	private String tpId,label;
	private String comment;
	private int state;
	
	public tpTask(String label, String tpId){
		this.label = label;
		this.tpId = tpId;
		state = 0;
	}
	
	public String generateId(){
		tpId = Integer.toHexString(label.hashCode());
		return tpId;
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	
	public void setID(String tpId){
		this.tpId = tpId;
	}
	
	public void setComment(String comment){
		this.comment = comment;
	}
	
	public void setState(int state){
		this.state = state;
	}
	
	public String getLabel(){
		return label;
		
	}
	
	public String getId(){
		return tpId;
		
	}
}
