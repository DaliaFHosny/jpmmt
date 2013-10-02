package de.lsem.evaluation.onetoone;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

/**
 * 
 * @author Christopher Klinkmüller
 *
 */
public class ActivityMatch {
	private String firstLabel;
	private String secondLabel;
	
	public ActivityMatch(String firstLabel, String secondLabel) {
		this.firstLabel = firstLabel;
		this.secondLabel = secondLabel;
	}
	
	public String getFirstLabel() {
		return this.firstLabel;
	}
	
	public void setFirstLabel(String firstLabel) {
		this.firstLabel = firstLabel;
	}
	
	public String getSecondLabel() {
		return this.secondLabel;
	}
	
	public void setSecondLabel(String secondLabel) {
		this.secondLabel = secondLabel;
	}
	
}
