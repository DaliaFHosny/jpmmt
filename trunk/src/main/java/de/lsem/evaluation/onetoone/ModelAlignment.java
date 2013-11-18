package de.lsem.evaluation.onetoone;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class ModelAlignment {
	private String firstModel;
	private String secondModel;
	private double similarity;
	private Set<ActivityMatch> activityMatches;
	
	public ModelAlignment(String firstModel, String secondModel) {
		this(firstModel, secondModel, 0);
	}

	public ModelAlignment(String firstModel, String secondModel, double similarity) {
		this.activityMatches = new HashSet<ActivityMatch>();
		this.firstModel = firstModel;
		this.secondModel = secondModel;
		this.similarity = similarity;
	}
	
	public String getFirstModel() {
		return firstModel;
	}
	
	public String getSecondModel() {
		return secondModel;
	}
	
	public void addActivityMatch(ActivityMatch match) {
		this.activityMatches.add(match);
	}
	
	public void addActivityMatches(Collection<ActivityMatch> matches) {
		this.activityMatches.addAll(activityMatches);
	}
	
	public Set<ActivityMatch> getActivityMatches() {
		return Collections.unmodifiableSet(this.activityMatches);
	}
	
	public void removeActivityMatch(ActivityMatch match) {
		this.activityMatches.remove(match);
	}
	
	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
}
