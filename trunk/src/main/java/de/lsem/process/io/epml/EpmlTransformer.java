package de.lsem.process.io.epml;

import java.util.HashMap;

import de.lsem.process.model.ProcessModel;
import de.lsem.process.model.ProcessNode;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

class EpmlTransformer {

	public ProcessModel transform(EventDrivenProcessChain chain) {
		ProcessModel model = this.createModel(chain);
		HashMap<String, ProcessNode> nodes = new HashMap<String, ProcessNode>();
		this.transformNodes(chain, model, nodes);		
		this.transformArcs(chain, model, nodes);
		return model;
	}

	private void transformArcs(EventDrivenProcessChain chain, ProcessModel model, HashMap<String, ProcessNode> nodes) {
		for (EventDrivenProcessChain.Arc arc : chain.getArcs()) {
			ProcessNode source = nodes.get(arc.getSource().getId());
			ProcessNode target = nodes.get(arc.getTarget().getId());
			model.addEdge(arc.getId(), arc.getLabel(), source, target);
		}		
	}

	private void transformNodes(EventDrivenProcessChain chain, ProcessModel model, HashMap<String, ProcessNode> nodes) {
		for (EventDrivenProcessChain.Node node : chain.getNodes()) {
			String type = ProcessNode.UNKNOWN;
			
			switch (node.getType()) {
				case EVENT : type = ProcessNode.UNKNOWN; break;
				case FUNCTION : type = ProcessNode.ACTIVITY; break;
				case OPERATOR_AND : type = ProcessNode.PARALLEL_GATEWAY; break;
				case OPERATOR_OR : type = ProcessNode.GATEWAY; break;
				case OPERATOR_XOR : type = ProcessNode.EXCLUSIVE_GATEWAY; break;
				default : type = ProcessNode.UNKNOWN; break; 
			}
			
			nodes.put(node.getId(), model.addNode(node.getId(), node.getLabel(), type));
		}
		
	}

	private ProcessModel createModel(EventDrivenProcessChain chain) {
		ProcessModel model = new ProcessModel();
		model.setId(chain.getId());
		model.setName(chain.getId());
		return model;
	}

}
