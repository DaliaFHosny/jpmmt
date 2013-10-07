package de.lsem.process.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
	private boolean removeMultipleExits;
	private boolean removeMultipleEntries;
	
	public ProcessModelReader() {
		this.removeMultipleEntries = false;
		this.removeMultipleExits = false;
	}
	
	public ProcessModelReader(boolean removeMultipleEntries, boolean removeMultipleExits) {
		this.removeMultipleEntries = removeMultipleEntries;
		this.removeMultipleExits = removeMultipleExits;
	}	
	
	public boolean isRemoveMultipleExits() {
		return this.removeMultipleExits;
	}

	public void setRemoveMultipleExits(boolean removeMultipleExits) {
		this.removeMultipleExits = removeMultipleExits;
	}

	public boolean isRemoveMultipleEntries() {
		return this.removeMultipleEntries;
	}

	public void setRemoveMultipleEntries(boolean removeMultipleEntries) {
		this.removeMultipleEntries = removeMultipleEntries;
	}

	public final ProcessModel read(String filename) {
		ProcessModel model = this.readModel(filename);
		this.checkForMultipleEntries(model);
		this.checkForMultipleExits(model);		
		return model;
	}
	
	protected abstract ProcessModel readModel(String filename);
	
	public List<ProcessModel> readModels(Collection<String> filenames) {
		List<ProcessModel> models = new ArrayList<ProcessModel>();
		
		for (String file : filenames) {
			models.add(this.read(file));
		}
		
		return models;
	}
	
	public List<ProcessModel> readModels(String foldername) {
		List<ProcessModel> models = new ArrayList<ProcessModel>();
		
		File folder = new File(foldername);
		for (File file : folder.listFiles()) {
			models.add(this.read(file.getAbsolutePath()));
		}
		
		return models;
	}
	
	protected void checkForMultipleExits(ProcessModel process) {
		if (this.removeMultipleExits) {
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
	}

	protected void checkForMultipleEntries(ProcessModel process) {
		if (this.removeMultipleEntries) {
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
}
