package de.lsem.process.io.epml;

import de.lsem.process.model.ProcessModel;
import de.lsem.process.model.ProcessNode;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */
public class EventDrivenProcessChainNode extends ProcessNode {
	public static final String FUNCTION = "EPC FUNCTION";
	public static final String EVENT = "EPC EVENT";
	public static final String XOR_CONNECTOR = "EPC XOR";
	public static final String OR_CONNECTOR = "EPC OR";
	public static final String AND_CONNECTOR = "EPC AND";
	
	public EventDrivenProcessChainNode(ProcessModel model, String id, String label, String type) {
		super(model, id, label, type);
	}

	public boolean isFunction() {
		return this.getType().equals(FUNCTION);
	}
	
	public boolean isEvent() {
		return this.getType().equals(EVENT);
	}
	
	public boolean isXorConnector() {
		return this.getType().equals(XOR_CONNECTOR);
	}
	
	public boolean isAndConnector() {
		return this.getType().equals(AND_CONNECTOR);
	}
	
	public boolean isOrConnector() {
		return this.getType().equals(OR_CONNECTOR);
	}
}
