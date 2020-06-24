package br.com.mjv.clockify.dto;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeInterval {

	private LocalDateTime start;

	private LocalDateTime end;

	private Duration duration;

	public LocalDateTime getStart() {
		return start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setStart(String start) {
		
		if(start != null) {
			this.start = LocalDateTime.parse(start.replace("Z", ""));
		}
		
	}

	public void setEnd(String end) {
		if(end != null) {
			this.end = LocalDateTime.parse(end.replace("Z", ""));
		}
		
		
	}

	public void setDuration(String duration) {
		if(duration != null) {
			this.duration = Duration.parse(duration);
		}
	}

}
