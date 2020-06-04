package br.com.mjv.clockify.dto;

public class TimeInterval {
	
	
	private String start;
	
	private String end;
	
	private String duration;

	public String getStart() {
		return start;
	}

	public String getEnd() {
		return end;
	}

	public String getDuration() {
		return duration;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

}
