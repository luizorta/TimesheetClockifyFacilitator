package br.com.mjv.clockify.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Entry {
	
	private String description;
	
	private TimeInterval timeInterval;
	
	private Project project;
	

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TimeInterval getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(TimeInterval timeInterval) {
		this.timeInterval = timeInterval;
	}



}
