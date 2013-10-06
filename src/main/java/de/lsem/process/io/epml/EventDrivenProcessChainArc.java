package de.lsem.process.io.epml;

import de.lsem.process.model.ProcessEdge;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */
class EventDrivenProcessChainArc extends ProcessEdge {
	public EventDrivenProcessChainArc(EventDrivenProcessChainNode source, EventDrivenProcessChainNode target) {
		super(source, target);
	}
	
	public EventDrivenProcessChainArc(EventDrivenProcessChain net, String id, String label) {
		super(net, id, label);
	}
	
	public EventDrivenProcessChainArc(EventDrivenProcessChain net, String id, String label, EventDrivenProcessChainNode source, EventDrivenProcessChainNode target) {
		super(net, id, label, source, target);
	}
}
