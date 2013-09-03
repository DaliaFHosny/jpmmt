package de.lsem.process.model;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

/**
 * @author klinke
 *
 */
public class ProcessNode {
	public static final String ACTIVITY = "activity";
	public static final String PARALLEL_GATEWAY = "parallel_gateway";
	public static final String EXCLUSIVE_GATEWAY = "exclusive_gateway";
	public static final String GATEWAY = "gateway";
	public static final String UNKNOWN = "unknown";
	
	private String id;
	private String label;
	private String type;
	private ProcessModel model;
	
	/**
	 * Constructor sets all members to the specified values.
	 * @param id	the unique identification key for the current node object
	 * @param label the label of the current node object
	 * @param type  the type of the current node object
	 */
	public ProcessNode(ProcessModel model, String id, String label, String type) {
		this.setModel(model);
		this.setId(id);
		this.setLabel(label);
		this.setType(type);
	}
	
	/**
	 * Returns the id of the current node object.
	 * @return the id of the current node object
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * Sets the id of the current node object.
	 * @param id the id of the current node object
	 */
	void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the label of the current node object.
	 * @return the label of the current node object
	 */
	public String getLabel() {
		return this.label;
	}
	
	/**
	 * Sets the label of the current node object.
	 * @param label the label of the current node object
	 */
	public void setLabel(String label) {
		if (label == null) {
			this.label = "";
		}
		else {
			this.label = label;
		}
	}

	
	/**
	 * Returns the process model the current node object belongs to.
	 * @return the process model the current node object belongs to 
	 */
	public ProcessModel getModel() {
		return model;
	}

	/**
	 * Sets the process model the current node object belongs to.
	 * @param model the model the current node object belongs to
	 */
	void setModel(ProcessModel model) {
		this.model = model;
	}
	
	/**
	 * Returns the type of the current node object.
	 * @return the type of the current node object.
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * Sets the type of the current node object. If the type does not equal to one of the types specified in the static members it will set the type to "unknown".
	 * @param type the type current node object
	 */
	public void setType(String type) {
		if (!(type.equals(ACTIVITY) || type.equals(PARALLEL_GATEWAY) || type.equals(EXCLUSIVE_GATEWAY) || type.equals(GATEWAY))) {
			this.type = UNKNOWN;
		}
		else {
			this.type = type;
		}
	}
	
	/**
	 * Checks whether the node is an activity or not.
	 * @return true if the node is of type ACTIVITY. Otherwise false.
	 */
	public boolean isActivity() {
		return this.type.equals(ACTIVITY);
	}
	
	@Override
	public String toString() {
		return this.type.equals(ACTIVITY) ? (this.label.equals("") ? this.type : "\"" + this.label + "\"") : this.type;
	}
}
