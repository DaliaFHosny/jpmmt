package de.lsem.process.matching;

import java.util.HashSet;
import java.util.Set;

import de.lsem.process.model.ProcessModel;
import de.lsem.process.model.ProcessNode;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class Fragment {
	private ProcessModel processModel;
	private Set<ProcessNode> nodes;
	
	public Fragment() {
		this.nodes = new HashSet<ProcessNode>();
	}
	
	public ProcessModel getProcessModel() {
		return this.processModel;
	}
	
	public void setProcessModel(ProcessModel processModel) {
		this.processModel = processModel;
	}
	
	public void addProcessNode(ProcessNode processNode) {
		this.nodes.add(processNode);
	}
	
	public Iterable<ProcessNode> getProcessNodes() {
		return this.nodes;
	}
	
	public void removeProcessNode(ProcessNode processNode) {
		this.nodes.add(processNode);
	}
	
	public boolean containsProcessNode(ProcessNode processNode) {
		return this.nodes.contains(processNode);
	}
	
	public int getProcessNodesSize() {
		return this.nodes.size();
	}
}
