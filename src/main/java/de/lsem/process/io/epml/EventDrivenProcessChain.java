package de.lsem.process.io.epml;

import de.lsem.process.model.ProcessModel;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class EventDrivenProcessChain extends ProcessModel {
	public EventDrivenProcessChain() {
		super();
	}
	
	public EventDrivenProcessChain(String id, String name) {
		super(id, name);
	}
	
	public EventDrivenProcessChainNode addFunction(String id, String label) {
		return this.addEventDrivenProcessChainNode(id, label, EventDrivenProcessChainNode.FUNCTION);
	}
	
	public EventDrivenProcessChainNode addEvent(String id, String label) {
		return this.addEventDrivenProcessChainNode(id, label, EventDrivenProcessChainNode.EVENT);
	}
	
	public EventDrivenProcessChainNode addXorConnector(String id, String label) {
		return this.addEventDrivenProcessChainNode(id, label, EventDrivenProcessChainNode.XOR_CONNECTOR);
	}
	
	public EventDrivenProcessChainNode addAndConnector(String id, String label) {
		return this.addEventDrivenProcessChainNode(id, label, EventDrivenProcessChainNode.AND_CONNECTOR);
	}
	
	public EventDrivenProcessChainNode addORConnector(String id, String label) {
		return this.addEventDrivenProcessChainNode(id, label, EventDrivenProcessChainNode.OR_CONNECTOR);
	}
	
	public EventDrivenProcessChainNode addEventDrivenProcessChainNode(String id, String label, String type) {
		EventDrivenProcessChainNode node = new EventDrivenProcessChainNode(this, id, label, type);
		this.addNode(node);
		return node;
	}
		
	public EventDrivenProcessChainArc addEventDrivenProcessChainArc(String id, String name, EventDrivenProcessChainNode source, EventDrivenProcessChainNode target) {
		EventDrivenProcessChainArc arc = new EventDrivenProcessChainArc(this, id, name, source, target);
		this.addEdge(arc);
		return arc;
	}
}
