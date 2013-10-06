package de.lsem.process.io.pnml;

import java.util.ArrayList;
import java.util.List;

import de.lsem.process.model.GraphicalInformation;
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

class PetriNetTransformation {
	public ProcessModel transform(PetriNet petriNet) {
		this.transformPlaces(petriNet);
		this.transformTransitions(petriNet);
		this.transformSilentTransitions(petriNet);
		this.transformGateways(petriNet);
		return petriNet;
	}

	private void transformGateways(PetriNet petriNet) {
		List<PetriNetNode> splitMerge = new ArrayList<PetriNetNode>();
		for (ProcessNode node : petriNet.getNodes()) {
			PetriNetNode petriNetNode = (PetriNetNode)node;
			int in = node.getModel().getEdgesWithTarget(node).size();
			int out = node.getModel().getEdgesWithSource(node).size();
			
			// split or merge
			if ((in < 2 && out > 1) || (in < 1 && out > 2)) {
				if (petriNetNode.isPlace()) {
					petriNetNode.setType(ProcessNode.EXCLUSIVE_GATEWAY);
				}
				else {
					petriNetNode.setType(ProcessNode.PARALLEL_GATEWAY);
				}
			}
			// split & merge
			else if (in > 1 && out > 1) {
				splitMerge.add(petriNetNode);
			}
		}
		
		for (PetriNetNode n: splitMerge) {
			this.introduceSplitMerge(n);
		}
	}

	private void introduceSplitMerge(PetriNetNode petriNetNode) {
		double minX = Double.MAX_VALUE,
			   maxX = Double.MIN_VALUE,
			   minY = Double.MAX_VALUE,
			   maxY = Double.MIN_VALUE,
			   maxHeight = Double.MIN_VALUE,
			   maxWidth = Double.MIN_VALUE;
		
		for (ProcessNode node : petriNetNode.getModel().getAdjacentNodes(petriNetNode)) {
			if (minX > node.getGraphicalInformation().getX()) {
				minX = node.getGraphicalInformation().getX();
			}
			else if (maxX < node.getGraphicalInformation().getX()) {
				maxX = node.getGraphicalInformation().getX();
			}
			else if (minY > node.getGraphicalInformation().getY()) {
				minY = node.getGraphicalInformation().getY();
			}
			else if (maxY < node.getGraphicalInformation().getY()) {
				maxY = node.getGraphicalInformation().getY();
			}
			else if (maxWidth > node.getGraphicalInformation().getWidth()) {
				maxWidth = node.getGraphicalInformation().getWidth();
			}
			else if (maxHeight < node.getGraphicalInformation().getHeight()) {
				maxHeight = node.getGraphicalInformation().getHeight();
			}
		}
		
		maxX = (maxX - minX) / 3;
		maxY = (maxY - minY) / 3;
		
		petriNetNode.setType(petriNetNode.isPlace() ? ProcessNode.EXCLUSIVE_GATEWAY : ProcessNode.PARALLEL_GATEWAY);
		petriNetNode.getGraphicalInformation().setX(minX + maxX);
		petriNetNode.getGraphicalInformation().setY(minY + maxY);
		
		ProcessNode split = petriNetNode.getModel().addNode(petriNetNode.getId() + "_2", petriNetNode.getLabel(), petriNetNode.getType());
		petriNetNode.getModel().addEdge(petriNetNode.getId() + "->" + split.getId(), "", petriNetNode, split);
		GraphicalInformation info = new GraphicalInformation(minX + maxX*2, minY + maxY*2, maxHeight, maxWidth);
		split.setGraphicalInformation(info);
		
		List<ProcessEdge> edges = new ArrayList<ProcessEdge>();
		for (ProcessEdge edge : petriNetNode.getModel().getEdgesWithSource(petriNetNode)) {
			edges.add(edge);
		}
		
		for (ProcessEdge edge : edges) {
			petriNetNode.getModel().removeEdge(edge);
			petriNetNode.getModel().addEdge(edge.getId(), edge.getLabel(), split, edge.getTarget());
		}
	}

	private void transformSilentTransitions(PetriNet petriNet) {
		for (ProcessNode node : petriNet.getActivities()) {
			PetriNetNode petriNetNode = (PetriNetNode)node;
			if ((petriNetNode.getLabel() == null || petriNetNode.getLabel().equals("")) && this.isSingleInOut(petriNetNode)) {
				ProcessEdge source = null;			
				for (ProcessEdge edge : petriNetNode.getModel().getEdgesWithTarget(petriNetNode)) {
					source = edge;
				}			
				
				ProcessEdge target = null;
				for (ProcessEdge edge : petriNetNode.getModel().getEdgesWithSource(petriNetNode)) {
					target = edge;
				}
				
				if (source != null) {
					petriNet.removeEdge(source);
				}
				if (target != null) {
					petriNet.removeEdge(target);
				}
				petriNet.removeNode(petriNetNode);	
				if (source != null && target != null) {
					petriNet.addEdge(source.getId() + "_" + target.getId(), "", source.getSource(), target.getTarget());
				}
			}
		}
	}

	private void transformTransitions(PetriNet petriNet) {
		for (ProcessNode node : petriNet.getNodes()) {
			PetriNetNode petriNetNode = (PetriNetNode)node;
			if (petriNetNode.isTransition() && this.isSingleInOut(petriNetNode)) {
				petriNetNode.setType(ProcessNode.ACTIVITY);
			}
		}
	}

	private void transformPlaces(PetriNet petriNet) {
		List<PetriNetNode> places = new ArrayList<PetriNetNode>();
		for (ProcessNode node : petriNet.getNodes()) {
			PetriNetNode petriNetNode = (PetriNetNode)node;
			if (petriNetNode.isPlace() && this.isSingleInOut(petriNetNode)) {
				places.add(petriNetNode);
			}
		}
		
		for (PetriNetNode place : places) {			
			ProcessEdge source = null;			
			for (ProcessEdge edge : place.getModel().getEdgesWithTarget(place)) {
				source = edge;
			}			
			
			ProcessEdge target = null;
			for (ProcessEdge edge : place.getModel().getEdgesWithSource(place)) {
				target = edge;
			}
			
			if (source != null) {
				petriNet.removeEdge(source);
			}
			if (target != null) {
				petriNet.removeEdge(target);
			}
			petriNet.removeNode(place);	
			if (source != null && target != null) {
				petriNet.addEdge(source.getId() + "_" + target.getId(), "", source.getSource(), target.getTarget());
			}
		}
	}

	private boolean isSingleInOut(PetriNetNode petriNetNode) {
		int out = petriNetNode.getModel().getEdgesWithSource(petriNetNode).size();
		int in = petriNetNode.getModel().getEdgesWithTarget(petriNetNode).size();
		return (in == 1 || in == 0) && (out == 0 || out == 1) && !(in == 0 && out == 0);
	}
	
	
	/*
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
		for (ProcessNode node : petriNet.getNodes()) {
			PetriNetNode petriNetNode = (PetriNetNode)node; 
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

	private void getTargets(PetriNet petriNet, PetriNetNode petriNetNode, HashSet<ProcessNode> targets) {
		Collection<ProcessEdge> arcs = petriNet.getEdgesWithSource(petriNetNode);
		
		for (ProcessEdge edge : arcs) {
			PetriNetArc arc = (PetriNetArc)edge;
			if (this.input.containsKey(arc.getTarget().getId())) {
				targets.add(this.input.get(arc.getTarget().getId()));
			}
			else {
				this.getTargets(petriNet, (PetriNetNode)arc.getTarget(), targets);
			}
		}
	}

	private void getSources(PetriNet petriNet, PetriNetNode petriNetNode, HashSet<ProcessNode> sources) {
		Collection<ProcessEdge> arcs = petriNet.getEdgesWithTarget(petriNetNode);		
		
		for (ProcessEdge edge : arcs) {
			PetriNetArc arc = (PetriNetArc)edge;
			if (this.output.containsKey(arc.getSource().getId())) {
				sources.add(this.output.get(arc.getSource().getId()));
			}
			else {
				this.getSources(petriNet, (PetriNetNode)arc.getSource(), sources);
			}
		}
	}

	private void transformNodes(PetriNet petriNet, ProcessModel process) {
		for (ProcessNode node : petriNet.getNodes()) {
			PetriNetNode petriNetNode = (PetriNetNode)node;
			int incoming = petriNet.getEdgesWithTarget(petriNetNode).size();
			int outgoing = petriNet.getEdgesWithSource(petriNetNode).size();
			
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

	private void createActivity(PetriNetNode petriNetNode, ProcessModel process) {
		if (!petriNetNode.getLabel().equals("")) {
			ProcessNode node = process.addNode(petriNetNode.getId(), petriNetNode.getLabel(), ProcessNode.ACTIVITY);
			node.setGraphicalInformation(petriNetNode.getGraphicalInformation());
			this.input.put(petriNetNode.getId(), node);
			this.output.put(petriNetNode.getId(), node);
		}		
	}

	private void createXorGateway(PetriNetNode petriNetNode, ProcessModel process) {
		ProcessNode node = process.addNode(petriNetNode.getId() + "_a", petriNetNode.getLabel(), ProcessNode.EXCLUSIVE_GATEWAY);		
		node.setGraphicalInformation(petriNetNode.getGraphicalInformation());
		this.input.put(petriNetNode.getId(), node);
		this.output.put(petriNetNode.getId(), node);
	}

	private void createXorGateways(PetriNetNode petriNetNode, ProcessModel process) {
		ProcessNode source = process.addNode("source_" + petriNetNode.getId(), petriNetNode.getLabel(), ProcessNode.EXCLUSIVE_GATEWAY);
		ProcessNode target = process.addNode("target_" + petriNetNode.getId() + "_b", petriNetNode.getLabel(), ProcessNode.EXCLUSIVE_GATEWAY);		
		process.addEdge("arc_" + petriNetNode.getId(), "", source, target);
		this.input.put(petriNetNode.getId(), source);
		this.output.put(petriNetNode.getId(), target);
	}

	private void createParallelSplitGateway(PetriNetNode petriNetNode, ProcessModel process) {
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

	private void createParallelJoinGateway(PetriNetNode petriNetNode, ProcessModel process) {
		if (petriNetNode.getLabel().equals("")) {
			this.createParallelGateway(petriNetNode, process);
		}
		else {
			ProcessNode source = process.addNode("source_" + petriNetNode.getId() + "_a", "", ProcessNode.PARALLEL_GATEWAY);
			source.setGraphicalInformation(petriNetNode.getGraphicalInformation());
			ProcessNode target = process.addNode("target_" + petriNetNode.getId() + "_b", petriNetNode.getLabel(), ProcessNode.ACTIVITY);
			target.setGraphicalInformation(petriNetNode.getGraphicalInformation());
			process.addEdge("arc_" + petriNetNode.getId(), "", source, target);	
			this.input.put(petriNetNode.getId(), source);
			this.output.put(petriNetNode.getId(), target);
		}
	}
	
	private void createParallelGateway(PetriNetNode petriNetNode, ProcessModel process) {
		ProcessNode node = process.addNode(petriNetNode.getId(), petriNetNode.getLabel(), ProcessNode.PARALLEL_GATEWAY);
		node.setGraphicalInformation(petriNetNode.getGraphicalInformation());
		this.input.put(petriNetNode.getId(), node);
		this.output.put(petriNetNode.getId(), node);
	}

	private void createParallelGateways(PetriNetNode petriNetNode, ProcessModel process) {
		ProcessNode source = process.addNode("source_" + petriNetNode.getId(), petriNetNode.getLabel(), ProcessNode.PARALLEL_GATEWAY);
		source.setGraphicalInformation(petriNetNode.getGraphicalInformation());
		ProcessNode target = process.addNode("target_" + petriNetNode.getId() + "_b", petriNetNode.getLabel(), ProcessNode.PARALLEL_GATEWAY);
		target.setGraphicalInformation(petriNetNode.getGraphicalInformation());
		process.addEdge("arc_" + petriNetNode.getId(), "", source, target);	
		this.input.put(petriNetNode.getId(), source);
		this.output.put(petriNetNode.getId(), target);	
	}*/
}
