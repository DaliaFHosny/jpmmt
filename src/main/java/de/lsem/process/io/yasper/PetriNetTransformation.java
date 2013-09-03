package de.lsem.process.io.yasper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.lsem.process.io.yasper.PetriNet.Arc;
import de.lsem.process.io.yasper.PetriNet.NetNode;
import de.lsem.process.model.ProcessModel;
import de.lsem.process.model.ProcessNode;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

class PetriNetTransformation {
	private HashMap<String, ProcessNode> input;
	private HashMap<String, ProcessNode> output;
	
	public PetriNetTransformation() {
		this.input = new HashMap<String, ProcessNode>();
		this.output = new HashMap<String, ProcessNode>();
	}
	
	public ProcessModel transform(PetriNet petriNet) {
		ProcessModel process = new ProcessModel();
		process.setName(petriNet.getName());
		process.setId(petriNet.getName());
		
		this.transformNodes(petriNet, process);	
		this.transformEdges(petriNet, process);
		
		this.input.clear();
		this.output.clear();
		
		return process;
	}

	private void transformEdges(PetriNet petriNet, ProcessModel process) {
		for (NetNode petriNetNode : petriNet.getNodes()) {
			if (this.input.containsKey(petriNetNode.getId())) {
				HashSet<ProcessNode> sources = new HashSet<ProcessNode>();
				this.getSources(petriNet, petriNetNode, sources);
				
				HashSet<ProcessNode> targets = new HashSet<ProcessNode>();
				this.getTargets(petriNet, petriNetNode, targets);
				
				for (ProcessNode source : sources) {
					ProcessNode target = this.input.get(petriNetNode.getId());
					if (!process.getEdgesWithTarget(target).contains(source)) {
						process.addEdge("edge_" + source.getId() + "_" + target.getId(), "", source, target);
					}
				}
				
				for (ProcessNode target : targets) {
					ProcessNode source = this.output.get(petriNetNode.getId());
					if (!process.getEdgesWithSource(source).contains(target)) {
						process.addEdge("edge_" + source.getId() + "_" + target.getId(), "", source, target);
					}
				}
			}
		}
	}

	private void getTargets(PetriNet petriNet, NetNode petriNetNode, HashSet<ProcessNode> targets) {
		Set<Arc> arcs = petriNet.getOutgoingArcs(petriNetNode);
		
		for (Arc arc : arcs) {
			if (this.input.containsKey(arc.getTarget().getId())) {
				targets.add(this.input.get(arc.getTarget().getId()));
			}
			else {
				this.getTargets(petriNet, arc.getTarget(), targets);
			}
		}
	}

	private void getSources(PetriNet petriNet, NetNode petriNetNode, HashSet<ProcessNode> sources) {
		Set<Arc> arcs = petriNet.getIncomingArcs(petriNetNode);		
		
		for (Arc arc : arcs) {
			if (this.output.containsKey(arc.getSource().getId())) {
				sources.add(this.output.get(arc.getSource().getId()));
			}
			else {
				this.getSources(petriNet, arc.getSource(), sources);
			}
		}
	}

	private void transformNodes(PetriNet petriNet, ProcessModel process) {
		for (NetNode petriNetNode : petriNet.getNodes()) {
			int incoming = petriNet.getIncomingArcs(petriNetNode).size();
			int outgoing = petriNet.getOutgoingArcs(petriNetNode).size();
			
			if (petriNetNode.isTransition()) {
				if (incoming > 1 && outgoing > 1) {
					this.createParallelGateways(petriNetNode, process);
				}
				else if (incoming > 1) {
					this.createParallelJoinGateway(petriNetNode, process);
				}
				else if (outgoing > 1) {
					this.createParallelSplitGateway(petriNetNode, process);
				}
				else {
					this.createActivity(petriNetNode, process);
				}
			}
			else {
				if (incoming > 1 && outgoing > 1) {
					this.createXorGateways(petriNetNode, process);
				}
				else if (incoming > 1 || outgoing > 1) {
					this.createXorGateway(petriNetNode, process);
				}
			}
		}
	}

	private void createActivity(NetNode petriNetNode, ProcessModel process) {
		if (!petriNetNode.getLabel().equals("")) {
			ProcessNode node = process.addNode(petriNetNode.getId(), petriNetNode.getLabel(), ProcessNode.ACTIVITY);
			this.input.put(petriNetNode.getId(), node);
			this.output.put(petriNetNode.getId(), node);
		}		
	}

	private void createXorGateway(NetNode petriNetNode, ProcessModel process) {
		ProcessNode node = process.addNode(petriNetNode.getId() + "_a", petriNetNode.getLabel(), ProcessNode.EXCLUSIVE_GATEWAY);		
		this.input.put(petriNetNode.getId(), node);
		this.output.put(petriNetNode.getId(), node);
	}

	private void createXorGateways(NetNode petriNetNode, ProcessModel process) {
		ProcessNode source = process.addNode("source_" + petriNetNode.getId(), petriNetNode.getLabel(), ProcessNode.EXCLUSIVE_GATEWAY);
		ProcessNode target = process.addNode("target_" + petriNetNode.getId() + "_b", petriNetNode.getLabel(), ProcessNode.EXCLUSIVE_GATEWAY);		
		process.addEdge("arc_" + petriNetNode.getId(), "", source, target);
		this.input.put(petriNetNode.getId(), source);
		this.output.put(petriNetNode.getId(), target);
	}

	private void createParallelSplitGateway(NetNode petriNetNode, ProcessModel process) {
		if (petriNetNode.getLabel().equals("")) {
			this.createParallelGateway(petriNetNode, process);
		}
		else {
			ProcessNode source = process.addNode("source_" + petriNetNode.getId() + "_a", petriNetNode.getLabel(), ProcessNode.ACTIVITY);
			ProcessNode target = process.addNode("target_" + petriNetNode.getId() + "_b", "", ProcessNode.PARALLEL_GATEWAY);
			process.addEdge("arc_" + petriNetNode.getId(), "", source, target);	
			this.input.put(petriNetNode.getId(), source);
			this.output.put(petriNetNode.getId(), target);
		}		
	}

	private void createParallelJoinGateway(NetNode petriNetNode, ProcessModel process) {
		if (petriNetNode.getLabel().equals("")) {
			this.createParallelGateway(petriNetNode, process);
		}
		else {
			ProcessNode source = process.addNode("source_" + petriNetNode.getId() + "_a", "", ProcessNode.PARALLEL_GATEWAY);
			ProcessNode target = process.addNode("target_" + petriNetNode.getId() + "_b", petriNetNode.getLabel(), ProcessNode.ACTIVITY);
			process.addEdge("arc_" + petriNetNode.getId(), "", source, target);	
			this.input.put(petriNetNode.getId(), source);
			this.output.put(petriNetNode.getId(), target);
		}
	}
	
	private void createParallelGateway(NetNode petriNetNode, ProcessModel process) {
		ProcessNode node = process.addNode(petriNetNode.getId(), petriNetNode.getLabel(), ProcessNode.PARALLEL_GATEWAY);
		this.input.put(petriNetNode.getId(), node);
		this.output.put(petriNetNode.getId(), node);
	}

	private void createParallelGateways(NetNode petriNetNode, ProcessModel process) {
		ProcessNode source = process.addNode("source_" + petriNetNode.getId(), petriNetNode.getLabel(), ProcessNode.PARALLEL_GATEWAY);
		ProcessNode target = process.addNode("target_" + petriNetNode.getId() + "_b", petriNetNode.getLabel(), ProcessNode.PARALLEL_GATEWAY);
		process.addEdge("arc_" + petriNetNode.getId(), "", source, target);	
		this.input.put(petriNetNode.getId(), source);
		this.output.put(petriNetNode.getId(), target);	
	}
}
