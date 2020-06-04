package br.com.mjv.clockify.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClockifyResponse {
		
	private List<Entry> timeEntries;

	public List<Entry> getTimeEntries() {
		return timeEntries;
	}

	public void setTimeEntries(List<Entry> timeEntries) {
		this.timeEntries = timeEntries;
	}

}
