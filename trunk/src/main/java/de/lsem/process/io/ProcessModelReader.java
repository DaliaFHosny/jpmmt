package de.lsem.process.io;

import java.util.HashSet;
import java.util.Set;

import de.lsem.process.model.ProcessModel;
import de.lsem.process.model.ProcessNode;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public abstract class ProcessModelReader {
	public ProcessModelReader() {
		
	}
	
	public abstract ProcessModel read(String filename); 
	
	protected void checkForMultipleExits(ProcessModel process) {
		Set<ProcessNode> exits = new HashSet<ProcessNode>();
		
		for (ProcessNode node : process.getNodes()) {
			if (process.getEdgesWithSource(node).size() == 0) {
				exits.add(node);
			}
		}
		
		if (exits.size() > 1) {
			ProcessNode end = process.addNode("endEvent", "", ProcessNode.GATEWAY);
			for (ProcessNode exit : exits) {
				process.addEdge("edge_" + exit.getId() + "_" + end.getId(), "", exit, end);
			}
		}
	}

	protected void checkForMultipleEntries(ProcessModel process) {
		Set<ProcessNode> entries = new HashSet<ProcessNode>();
		
		for (ProcessNode node : process.getNodes()) {
			if (process.getEdgesWithTarget(node).size() == 0) {
				entries.add(node);
			}
		}
		
		if (entries.size() > 1) {
			ProcessNode start = process.addNode("startEvent", "", ProcessNode.GATEWAY);
			for (ProcessNode entry : entries) {
				process.addEdge("edge_" + start.getId() + "_" + entry.getId(), "", start, entry);
			}
		}
	}
}
