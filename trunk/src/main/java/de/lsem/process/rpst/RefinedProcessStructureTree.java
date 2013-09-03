package de.lsem.process.rpst;

import java.util.ArrayList;
import java.util.List;

import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.hypergraph.abs.Vertex;

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

public class RefinedProcessStructureTree {

	private ProcessModel model;
	private Node root;
	
	public RefinedProcessStructureTree(ProcessModel model) {
		this.model = model;
		
		// transform model to jbpt-graph
		DirectedGraphWrapper wrapper = new DirectedGraphWrapper(model);
		
		// create jbpt-rpst for jbpt-graph 
		RPST<DirectedEdge, Vertex> rpst = new RPST<DirectedEdge, Vertex>(wrapper.getGraph());
		
		// transform jbpt-rpst to refinedprocessstructuretree
		this.transformRpst(wrapper, rpst);
	}
	
	public ProcessModel getModel() {
		return this.model;
	}
	
	public Node getRoot() {
		return this.root;
	}

	private void transformRpst(DirectedGraphWrapper wrapper, RPST<DirectedEdge, Vertex> rpst) {
		IRPSTNode<DirectedEdge, Vertex> root = rpst.getRoot();
		this.root = this.transformRpstNodes(wrapper, root, rpst);				
	}
	
	private Node transformRpstNodes(DirectedGraphWrapper wrapper, IRPSTNode<DirectedEdge, Vertex> root, RPST<DirectedEdge, Vertex> rpst) {
		Node node = new Node(this);
		
		ProcessNode entry = wrapper.getMappedProcessNode(root.getEntry());
		ProcessNode exit = wrapper.getMappedProcessNode(root.getExit());
		node.setEntry(entry);
		node.setExit(exit);
		
		for (DirectedEdge edge : root.getFragment()) {
			ProcessEdge pEdge = wrapper.getMappedProcessEdge(edge);
			node.addEdge(pEdge);
		}
		
		switch (root.getType()) {
			case RIGID 		: node.setType(Type.RIGID); 	break;
			case TRIVIAL 	: node.setType(Type.TRIVIAL); 	break;
			case POLYGON 	: node.setType(Type.POLYGON); 	break;
			case BOND 		: node.setType(Type.BOND); 		break;
			default			: node.setType(Type.UNDEFINED);	break;
		}
		
		for (IRPSTNode<DirectedEdge, Vertex> rpstNode : rpst.getChildren(root)) {
			Node child = this.transformRpstNodes(wrapper, rpstNode, rpst);
			child.setParent(child);
			node.addChild(child);		
		}
				
		return node;
	}	
	
	public enum Type {
		TRIVIAL,
		POLYGON,
		RIGID,
		BOND,
		UNDEFINED
	}

	public class Node {
		private List<ProcessEdge> edges;
		private ProcessNode entry;
		private ProcessNode exit;
		private Node parent;
		private List<Node> children;
		private Type type;
		private List<ProcessNode> nodes;
		private RefinedProcessStructureTree tree;		
		
		public Node(RefinedProcessStructureTree tree) {
			this.children = new ArrayList<Node>();
			this.edges = new ArrayList<ProcessEdge>();
			this.nodes = new ArrayList<ProcessNode>();
			this.tree = tree;
		}
		
		public RefinedProcessStructureTree getTree() {
			return this.tree;
		}
		
		public ProcessNode getEntry() {
			return this.entry;
		}		

		public void setEntry(ProcessNode entry) {
			this.entry = entry;
		}

		public ProcessNode getExit() {
			return exit;
		}

		public void setExit(ProcessNode exit) {
			this.exit = exit;
		}

		public Node getParent() {
			return parent;
		}

		public void setParent(Node predecessor) {
			this.parent = predecessor;
		}
		
		public int edgesSize() {
			return this.edges.size();
		}
		
		public int childrenSize() {
			return this.children.size();
		}
		
		public ProcessEdge getEdge(int index) {
			return this.edges.get(index);
		}
		
		public Node getChild(int index) {
			return this.children.get(index);
		}
		
		public void addEdge(ProcessEdge edge) {
			this.edges.add(edge);
			
			this.addNode(edge.getSource());
			this.addNode(edge.getTarget());
		}
		
		public void addChild(Node child) {
			this.children.add(child);
		}	
		
		public Type getType() {
			return this.type;
		}
		
		public void setType(Type type) {
			this.type = type;
		}
		
		private void addNode(ProcessNode node) {
			if (!this.nodes.contains(node)) {
				this.nodes.add(node);
			}
		}
		
		public int nodesSize() {
			return this.nodes.size();
		}
		
		public ProcessNode getNode(int index) {
			return this.nodes.get(index);
		}
	}
}
