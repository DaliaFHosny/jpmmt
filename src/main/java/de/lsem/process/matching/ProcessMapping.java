package de.lsem.process.matching;

import java.util.Set;

import de.lsem.process.model.ProcessModel;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class ProcessMapping {
	private ProcessModel model1;
	private ProcessModel model2;
	private Set<FragmentMatch> fragmentMatch;
	
	public ProcessMapping(ProcessModel model1, ProcessModel model2) {
		this.model1 = model1;
		this.model2 = model2;
	}
	
	public ProcessModel getModel1() {
		return model1;
	}
	
	public void setModel1(ProcessModel model1) {
		this.model1 = model1;
	}
	
	public ProcessModel getModel2() {
		return model2;
	}
	
	public void setModel2(ProcessModel model2) {
		this.model2 = model2;
	}
	
	public void addFragmentMatch(FragmentMatch fragmentMatch) {
		this.fragmentMatch.add(fragmentMatch);
	}
	
	public Iterable<FragmentMatch> getFragmentMatch() {
		return this.fragmentMatch;
	}
	
	public void removeFragmentMatch(FragmentMatch fragmentMapping) {
		this.fragmentMatch.remove(fragmentMapping);
	}
}
