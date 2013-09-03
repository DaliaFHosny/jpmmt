package de.lsem.process.rpst;

import org.jbpt.algo.tree.tctree.TCTree;
import org.jbpt.algo.tree.tctree.TCTreeNode;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.hypergraph.abs.Vertex;

import de.lsem.process.model.ProcessModel;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class TriconnectedComponents {

	public TriconnectedComponents(ProcessModel model) {
		DirectedGraphWrapper wrapper = new DirectedGraphWrapper(model);
		
		TCTree<DirectedEdge, Vertex> tree = new TCTree<DirectedEdge, Vertex>(wrapper.getGraph());
		
		printNode(0, tree.getRoot(), tree);
		
		
	}

	private void printNode(int depth, TCTreeNode<DirectedEdge, Vertex> root, TCTree<DirectedEdge, Vertex> tree) {
		System.out.print("Node: ");
		for (int a = 0; a < depth; a++) {
			System.out.print(" ");
		}
		
		for (Vertex vertex : root.getSkeleton().getVertices()) {
			System.out.print(vertex.getLabel() + " ");
		}
		System.out.println();
		
		for (TCTreeNode<DirectedEdge, Vertex> node : tree.getChildren(root)) {
			this.printNode(depth + 1, node, tree);
		}
	}

}
