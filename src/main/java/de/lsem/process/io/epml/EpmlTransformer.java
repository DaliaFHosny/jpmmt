package de.lsem.process.io.epml;

import java.util.ArrayList;
import java.util.List;

import de.lsem.process.model.ProcessEdge;
import de.lsem.process.model.ProcessModel;
import de.lsem.process.model.ProcessNode;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

/**
 * 
 * @author Christopher Klinkmüller
 *
 */
class EpmlTransformer {

	public ProcessModel transform(EventDrivenProcessChain chain) {
		this.removeEvents(chain);
		this.transformNodes(chain);
		return chain;
	}
	
	private void transformNodes(EventDrivenProcessChain chain) {
		for (ProcessNode node : chain.getNodes()) {
			EventDrivenProcessChainNode epcNode = (EventDrivenProcessChainNode)node;
			if (epcNode.isFunction()) {
				epcNode.setType(ProcessNode.ACTIVITY);
			}
			else if (epcNode.isAndConnector()) {
				epcNode.setType(ProcessNode.PARALLEL_GATEWAY);
			}
			else if (epcNode.isXorConnector()) {
				epcNode.setType(ProcessNode.EXCLUSIVE_GATEWAY);
			}
			else if (epcNode.isOrConnector()) {
				epcNode.setType(ProcessNode.GATEWAY);
			}
		}
	}

	private void removeEvents(EventDrivenProcessChain chain) {
		List<EventDrivenProcessChainNode> events = new ArrayList<EventDrivenProcessChainNode>();
		for (ProcessNode node : chain.getNodes()) {
			EventDrivenProcessChainNode epcNode = (EventDrivenProcessChainNode)node;
			if (epcNode.isEvent()) {
				events.add(epcNode);
			}
		}
		
		for (EventDrivenProcessChainNode event : events) {
			EventDrivenProcessChainArc source = null;
			for (ProcessEdge edge : chain.getEdgesWithTarget(event)) {
				source = (EventDrivenProcessChainArc)edge;
			}
			
			EventDrivenProcessChainArc target = null;
			for (ProcessEdge edge : chain.getEdgesWithSource(event)) {
				target = (EventDrivenProcessChainArc)edge;
			}
			
			if (target != null) {
				chain.removeEdge(target);
			}
			if (source != null) {
				chain.removeEdge(source);
			}
			chain.removeNode(event);
			
			if (target != null && source != null) {
				chain.addEventDrivenProcessChainArc(source.getId() + "_" + target.getId(), "", 
						(EventDrivenProcessChainNode)source.getSource(),
						(EventDrivenProcessChainNode)target.getTarget());
			}
		}
	}
}
