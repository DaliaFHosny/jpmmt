package de.lsem.process.model;

import java.util.Collection;
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
	private Map<String, ProcessNode> nodes;
	private Map<String, ProcessEdge> edges;
	
	/**
	 * Default constructor sets all members to default values.
	 */
	public ProcessModel() {
		this.nodes = new HashMap<String, ProcessNode>();
		this.edges = new HashMap<String, ProcessEdge>();	
		this.edgesWithSource = new HashMap<ProcessNode, Set<ProcessEdge>>();
		this.edgesWithTarget = new HashMap<ProcessNode, Set<ProcessEdge>>();
		this.name = "";
		this.id = "";
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
			this.edges.containsKey(id) || !this.nodes.containsKey(source.getId()) || 
			!this.nodes.containsKey(target.getId())) {
			return null;
		}
		
		ProcessEdge edge = new ProcessEdge(this, id, label, source, target);
		this.edges.put(id, edge);
		this.edgesWithSource.get(source).add(edge);
		this.edgesWithTarget.get(target).add(edge);
		
		return edge;
	}
	
	public ProcessNode addNode(String id, String label, String type) {
		if (id == null || label == null || this.nodes.containsKey(id)) {
			return null;
		}
		
		ProcessNode node = new ProcessNode(this, id, label, type);
		this.nodes.put(id, node);
		this.edgesWithSource.put(node, new HashSet<ProcessEdge>());
		this.edgesWithTarget.put(node, new HashSet<ProcessEdge>());
		
		return node;
	}
	
	public void addNode(ProcessNode node) {
		this.nodes.put(node.getId(), node);
		this.edgesWithSource.put(node, new HashSet<ProcessEdge>());
		this.edgesWithTarget.put(node, new HashSet<ProcessEdge>());
	}
	
	public Set<ProcessNode> getNodes() {
		HashSet<ProcessNode> nodes = new HashSet<ProcessNode>();
		
		for (String key : this.nodes.keySet()) {
			nodes.add(this.nodes.get(key));
		}
		
		return nodes;
	}

	public void removeNode(ProcessNode node) {
		if (node != null && this.nodes.containsKey(node.getId())) {
			this.nodes.remove(node.getId());
			for (ProcessEdge edge : this.getEdgesWithNode(node)) {
				this.removeEdge(edge);
			}
		}
	}
	
	public void removeEdge(ProcessEdge edge) {
		if (edge != null && this.edges.containsKey(edge.getId())) {
			this.edges.get(edge.getId());
			this.edgesWithSource.remove(edge.getSource());
			this.edgesWithTarget.remove(edge.getTarget());
		}
	}
	
	public Collection<ProcessNode> getAdjacentNodes(ProcessNode node) {
		HashSet<ProcessNode> nodes = new HashSet<ProcessNode>();
		
		Collection<ProcessEdge> es = this.getEdgesWithSource(node);
		for (ProcessEdge e : es) {
			nodes.add(e.getTarget());
		}
		
		es = this.getEdgesWithTarget(node);
		for (ProcessEdge e : es) {
			nodes.add(e.getSource());
		}
		
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
		
		return this.edgesWithTarget.get(node);
	}

	public Collection<ProcessEdge> getEdgesWithSource(ProcessNode node) {
		if (node == null || !this.edgesWithSource.containsKey(node)) {
			return new HashSet<ProcessEdge>();
		}
		
		return this.edgesWithSource.get(node);
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
		
		for (String key : nodes.keySet()) {
			if (nodes.get(key).getType().equals(ProcessNode.ACTIVITY)) {
				activities.add(nodes.get(key));
			}
		}
		
		return activities;
	}
	
	public void traverseActivities(NodeVisitor visitor) {
		for (String key : nodes.keySet()) {
			if (nodes.get(key).getType().equals(ProcessNode.ACTIVITY)) {
				visitor.visit(nodes.get(key));
			}
		}
	}
	
	public static interface NodeVisitor {
		void visit(ProcessNode node);
	}
}
