package de.lsem.process.model;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class ProcessEdge {
	private String id;
	private String label;
	private ProcessNode source;
	private ProcessNode target;
	private ProcessModel model;
	
	public ProcessEdge(ProcessNode source, ProcessNode target) {
		this.source = source;
		this.target = target;
	}
	
	public ProcessEdge(ProcessModel model, String id, String label) {
		this.setId(id);
		this.setLabel(label);
	}
	
	public ProcessEdge(ProcessModel model, String id, String label, ProcessNode source, ProcessNode target) {
		this.setModel(model);
		this.setId(id);
		this.setLabel(label);
		this.setSource(source);
		this.setTarget(target);
	}
	
	public String getId() {
		return this.id;
	}
	
	void setId(String id) {
		this.id = id;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public ProcessModel getModel() {
		return model;
	}

	void setModel(ProcessModel model) {
		this.model = model;
	}
	
	public ProcessNode getSource() {
		return this.source;
	}
	
	public void setSource(ProcessNode source) {
		this.source = source;
	}
	
	public ProcessNode getTarget() {
		return this.target;
	}
	
	public void setTarget(ProcessNode target) {
		this.target = target;
	}
}
