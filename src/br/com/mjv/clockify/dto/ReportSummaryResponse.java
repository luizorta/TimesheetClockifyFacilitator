package br.com.mjv.clockify.dto;

import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportSummaryResponse {
		
	private List<Entry> timeEntries;
	
	private Duration totalTime;
	
	private Duration totalBillableTime;

	public Duration getTotalTime() {
		return totalTime;
	}

	public Duration getTotalBillableTime() {
		return totalBillableTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = Duration.parse(totalTime);
	}

	public void setTotalBillableTime(String totalBillableTime) {
		this.totalBillableTime = Duration.parse(totalBillableTime);
	}

	public List<Entry> getTimeEntries() {
		return timeEntries;
	}

	public void setTimeEntries(List<Entry> timeEntries) {
		this.timeEntries = timeEntries;
	}

}
