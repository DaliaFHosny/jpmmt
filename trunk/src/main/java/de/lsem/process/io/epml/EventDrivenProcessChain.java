package de.lsem.process.io.epml;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

class EventDrivenProcessChain {
	private String id;
	private List<Node> nodes;
	private List<Arc> arcs;
	
	public EventDrivenProcessChain(String id) {
		this.nodes = new ArrayList<Node>();
		this.arcs = new ArrayList<Arc>();
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void addArc(Arc arc) {
		this.arcs.add(arc);
	}
	
	public void removeArc(Arc arc) {
		this.arcs.remove(arc);
	}
	
	public int arcsSize() {
		return this.arcs.size();
	}
	
	public Arc getArc(int index) {
		return this.arcs.get(index);
	}
	
	public Iterable<Arc> getArcs() {
		return this.arcs;
	}
	
	public void addNode(Node node) {
		this.nodes.add(node);
	}
	
	public void removeNode(Node node) {
		this.nodes.add(node);
	}
	
	public int nodesSize() {
		return this.nodes.size();
	}
	
	public Node getNode(int index) {
		return this.nodes.get(index);
	}
	
	public Iterable<Node> getNodes() {
		return this.nodes;
	}
	
	private static abstract class Element {
		private String id;
		private String label;
		
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getLabel() {
			return label;
		}
		
		public void setLabel(String label) {
			this.label = label;
		}		
	}
	
	public static class Node extends Element {
		private NodeType type;
		
		public Node(String id, String label, NodeType type) {
			this.setId(id);
			this.setLabel(label);
			this.setType(type);
		}
		
		public NodeType getType() {
			return this.type;
		}
		
		public void setType(NodeType type) {
			this.type = type;
		}
	}
	
	public enum NodeType {
		EVENT,
		FUNCTION,
		OPERATOR_AND,
		OPERATOR_OR,
		OPERATOR_XOR
	}
	
	public static class Arc extends Element {
		private Node source;
		private Node target;
		
		public Arc(String id, String label, Node source, Node target) {
			this.setSource(source);
			this.setTarget(target);
			this.setId(id);
			this.setLabel(label);
		}
		
		public Node getSource() {
			return source;
		}
		
		private void setSource(Node source) {
			this.source = source;
		}
		
		public Node getTarget() {
			return target;
		}
		
		private void setTarget(Node target) {
			this.target = target;
		}		
	}
}
