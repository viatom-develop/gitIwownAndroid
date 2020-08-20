package com.zeroner.bledemo.bean.data;

public class SleepStatusFlag {
	public static final int Placement=0;
	public static final int Deep=1;
	public static final int Light=2;
	private int time;
	// 0: Placement ,1：deep sleep，2 ：light sleep
	private int deepFlag;
	private int startTime;
	
	public SleepStatusFlag() {
	}


	public SleepStatusFlag(int time, int deepFlag, int startTime) {
		super();
		this.time = time;
		this.deepFlag = deepFlag;
		this.startTime=startTime;
	}

	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int isDeepFlag() {
		return deepFlag;
	}
	public void setDeepFlag(int deepFlag) {
		this.deepFlag = deepFlag;
	}


	public int getStartTime() {
		return startTime;
	}


	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}



}
