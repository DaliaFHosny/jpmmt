package de.lsem.process.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class ProcessModel {
	private String name;
	private String id;
	private Map<ProcessNode, Set<ProcessEdge>> edgesWithSource;
	private Map<ProcessNode, Set<ProcessEdge>> edgesWithTarget;
	private Map<String, ProcessNode> nodeIds;
	private Map<String, ProcessEdge> edges;
	private Set<ProcessNode> nodes;
	
	/**
	 * Default constructor sets all members to default values.
	 */
	public ProcessModel() {
		this.nodeIds = new HashMap<String, ProcessNode>();
		this.edges = new HashMap<String, ProcessEdge>();	
		this.edgesWithSource = new HashMap<ProcessNode, Set<ProcessEdge>>();
		this.edgesWithTarget = new HashMap<ProcessNode, Set<ProcessEdge>>();
		this.name = "";
		this.id = "";
		this.nodes = new HashSet<ProcessNode>();
	}
	
	public ProcessModel(String id, String name) {
		this();
		this.setId(id);
		this.setName(name);
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ProcessEdge addEdge(String id, String label, ProcessNode source, ProcessNode target) {
		if (id == null || label == null || source == null || target == null || 
			this.edges.containsKey(id) || !this.nodeIds.containsKey(source.getId()) || 
			!this.nodeIds.containsKey(target.getId())) {
			return null;
		}
		
		ProcessEdge edge = new ProcessEdge(this, id, label, source, target);
		this.edges.put(id, edge);
		this.edgesWithSource.get(source).add(edge);
		this.edgesWithTarget.get(target).add(edge);
		
		return edge;
	}
	
	public ProcessEdge addEdge(ProcessEdge edge) {
		this.edges.put(edge.getId(), edge);
		this.edgesWithSource.get(edge.getSource()).add(edge);
		this.edgesWithTarget.get(edge.getTarget()).add(edge);
		return edge;
	}
	
	public ProcessNode addNode(String id, String label, String type) {
		if (id == null || label == null || this.nodeIds.containsKey(id)) {
			return null;
		}
		
		ProcessNode node = new ProcessNode(this, id, label, type);
		this.nodeIds.put(id, node);
		this.edgesWithSource.put(node, new HashSet<ProcessEdge>());
		this.edgesWithTarget.put(node, new HashSet<ProcessEdge>());
		this.nodes.add(node);
		
		return node;
	}
	
	public void addNode(ProcessNode node) {
		this.nodeIds.put(node.getId(), node);
		this.edgesWithSource.put(node, new HashSet<ProcessEdge>());
		this.edgesWithTarget.put(node, new HashSet<ProcessEdge>());
		this.nodes.add(node);
	}
	
	public Collection<ProcessNode> getNodes() {
		return Collections.unmodifiableSet(this.nodes);
	}

	public void removeNode(ProcessNode node) {
		if (node != null && this.nodeIds.containsKey(node.getId())) {
			this.nodeIds.remove(node.getId());
			this.nodes.remove(node);
			for (ProcessEdge edge : this.getEdgesWithNode(node)) {
				this.removeEdge(edge);
			}
		}
	}
	
	public void removeEdge(ProcessEdge edge) {
		if (edge != null && this.edges.containsKey(edge.getId())) {
			this.edges.remove(edge.getId());
			this.edgesWithSource.get(edge.getSource()).remove(edge);
			this.edgesWithTarget.get(edge.getTarget()).remove(edge);
		}
	}
	
	public Collection<ProcessNode> getTargetNodes(ProcessNode node) {
		HashSet<ProcessNode> nodes = new HashSet<ProcessNode>();
		for (ProcessEdge e : this.getEdgesWithSource(node)) {
			nodes.add(e.getTarget());
		}
		return nodes;
	}
	
	public Collection<ProcessNode> getSourceNodes(ProcessNode node) {
		HashSet<ProcessNode> nodes = new HashSet<ProcessNode>();
		for (ProcessEdge e : this.getEdgesWithTarget(node)) {
			nodes.add(e.getSource());
		}
		return nodes;
	}
	
	public Collection<ProcessNode> getAdjacentNodes(ProcessNode node) {
		Collection<ProcessNode> nodes = this.getSourceNodes(node);
		nodes.addAll(this.getTargetNodes(node));		
		return nodes;
	}
	
	public Collection<ProcessEdge> getEdgesWithNode(ProcessNode node) {
		HashSet<ProcessEdge> edges = new HashSet<ProcessEdge>();
		
		Collection<ProcessEdge> es = this.getEdgesWithSource(node);
		if (es.size() > 0) {
			edges.addAll(es);
		}
		
		es = this.getEdgesWithTarget(node);
		if (es.size() > 0) {
			edges.addAll(es);
		}
		
		return edges;
	}

	public Collection<ProcessEdge> getEdgesWithTarget(ProcessNode node) {
		if (node == null || !this.edgesWithTarget.containsKey(node)) {
			return new HashSet<ProcessEdge>();
		}
		
		HashSet<ProcessEdge> edges = new HashSet<ProcessEdge>();
		edges.addAll(this.edgesWithTarget.get(node));
		return edges;
	}

	public Collection<ProcessEdge> getEdgesWithSource(ProcessNode node) {
		if (node == null || !this.edgesWithSource.containsKey(node)) {
			return new HashSet<ProcessEdge>();
		}
		
		HashSet<ProcessEdge> edges = new HashSet<ProcessEdge>();
		edges.addAll(this.edgesWithSource.get(node));
		return edges;
	}
	
	public Set<ProcessEdge> getEdges() {
		HashSet<ProcessEdge> edges = new HashSet<ProcessEdge>();
		
		for (String key : this.edges.keySet()) {
			edges.add(this.edges.get(key));
		}
		
		return edges;
	}

	public Set<ProcessNode> getActivities() {
		HashSet<ProcessNode> activities = new HashSet<ProcessNode>();
		
		for (String key : nodeIds.keySet()) {
			if (nodeIds.get(key).isActivity()) {
				activities.add(nodeIds.get(key));
			}
		}
		
		return activities;
	}
	
	public void traverseActivities(NodeVisitor visitor) {
		for (String key : nodeIds.keySet()) {
			if (nodeIds.get(key).getType().equals(ProcessNode.ACTIVITY)) {
				visitor.visit(nodeIds.get(key));
			}
		}
	}
	
	public Collection<ProcessNode> getStartNodes() {
		ArrayList<ProcessNode> starts = new ArrayList<ProcessNode>();
		
		for (ProcessNode node : this.nodes) {
			if (this.edgesWithTarget.get(node).size() == 0) {
				starts.add(node);
			}
		}
		
		return starts;
	}
	
	public Collection<ProcessNode> getEndNodes() {
		ArrayList<ProcessNode> starts = new ArrayList<ProcessNode>();
		
		for (ProcessNode node : this.nodes) {
			if (this.edgesWithSource.get(node).size() == 0) {
				starts.add(node);
			}
		}
		
		return starts;
	}
	
	public static interface NodeVisitor {
		void visit(ProcessNode node);
	}
	
	public String toString() {
		return this.name;
	}
}
