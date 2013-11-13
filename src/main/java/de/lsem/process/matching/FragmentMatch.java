package de.lsem.process.matching;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class FragmentMatch {
	private Fragment fragment1;
	private Fragment fragment2;
	private double similarity;
	
	public FragmentMatch() {		
	}
	
	public FragmentMatch(Fragment fragment1, Fragment fragment2) {
		this.fragment1 = fragment1;
		this.fragment2 = fragment2;
	}
	
	public FragmentMatch(Fragment fragment1, Fragment fragment2, double similarity) {
		this.fragment1 = fragment1;
		this.fragment2 = fragment2;
		this.similarity = similarity;
	}
	
	public Fragment getFragment1() {
		return fragment1;
	}
	
	public void setFragment1(Fragment fragment1) {
		this.fragment1 = fragment1;
	}
	
	public Fragment getFragment2() {
		return fragment2;
	}
	
	public void setFragment2(Fragment fragment2) {
		this.fragment2 = fragment2;
	}
	
	public double getSimilarity() {
		return this.similarity;
	}
	
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
}
