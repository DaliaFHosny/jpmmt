package de.lsem.process.rpst;

import java.util.HashMap;

import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.graph.abs.IDirectedGraph;
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

class DirectedGraphWrapper {	
	private HashMap<Vertex, ProcessNode> vertexNodes;
	private HashMap<DirectedEdge, ProcessEdge> edges;
	private DirectedGraph graph;
	
	public IDirectedGraph<DirectedEdge, Vertex> getGraph() {
		return graph;
	}
	
	public DirectedGraphWrapper(ProcessModel model) {
		this.graph = new DirectedGraph();
		HashMap<ProcessNode, Vertex> nodeVertecies = new HashMap<ProcessNode, Vertex>();
		this.vertexNodes = new HashMap<Vertex, ProcessNode>();
		this.edges = new HashMap<DirectedEdge, ProcessEdge>();
		
		for (ProcessNode node : model.getNodes()) {
			Vertex v = new Vertex();
			v.setId(node.getId());
			v.setName(node.getLabel());
			graph.addVertex(v);
			nodeVertecies.put(node, v);
			this.vertexNodes.put(v, node);
		}
		
		for (ProcessEdge edge : model.getEdges()) {
			Vertex s = nodeVertecies.get(edge.getSource());
			Vertex t = nodeVertecies.get(edge.getTarget());
			DirectedEdge e = graph.addEdge(s, t);
			this.edges.put(e, edge);
		}		
	}

	public ProcessNode getMappedProcessNode(Vertex v) {
		return this.vertexNodes.get(v);
	}
	
	public ProcessEdge getMappedProcessEdge(DirectedEdge e) {
		return this.edges.get(e);
	}
}
