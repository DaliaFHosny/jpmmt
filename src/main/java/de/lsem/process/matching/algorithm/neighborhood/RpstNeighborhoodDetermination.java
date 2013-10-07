package de.lsem.process.matching.algorithm.neighborhood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.lsem.matrix.Matrix;
import de.lsem.process.model.BagOfWords;
import de.lsem.process.model.ProcessModel;
import de.lsem.process.model.ProcessNode;
import de.lsem.process.rpst.RefinedProcessStructureTree;
import de.lsem.process.rpst.RefinedProcessStructureTree.Node;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class RpstNeighborhoodDetermination extends NeighborhoodDetermination {

	@Override
	protected List<BagOfWords> selectNeighbors(BagOfWords bag, Matrix<BagOfWords> matrix) {
		ArrayList<BagOfWords> list = new ArrayList<BagOfWords>();
		
		for (BagOfWords bag2 : matrix.getObjects2()) {
			if (this.distances.get(bag.getProcessModel().getId()).getValue(bag, bag2) > 0.9d) {
				list.add(bag2);
			}
		}
		
		return list;
	}

	@Override
	protected void addModelDistances(ProcessModel model, Collection<BagOfWords> modelBags) {
		Matrix<BagOfWords> distances = new Matrix<BagOfWords>(modelBags, modelBags);
		HashMap<ProcessNode, Collection<BagOfWords>> nodes = this.getNodeMapping(modelBags);		
		RefinedProcessStructureTree tree = new RefinedProcessStructureTree(model);
		this.updateDistances(distances, nodes, tree);		
	}

	private void updateDistances(Matrix<BagOfWords> distances, HashMap<ProcessNode, Collection<BagOfWords>> nodes, RefinedProcessStructureTree tree) {
		HashMap<RefinedProcessStructureTree.Node, Collection<BagOfWords>> bags = new HashMap<RefinedProcessStructureTree.Node, Collection<BagOfWords>>(); 
		this.getBagsForTreeNodes(nodes, tree.getRoot(), bags);
		
		for (RefinedProcessStructureTree.Node cur : bags.keySet()) {
			for (int a = 0; a < cur.getParent().childrenSize(); a++) {
				RefinedProcessStructureTree.Node aim = cur.getParent().getChild(a);
				for (BagOfWords bag1 : bags.get(cur)) {
					for (BagOfWords bag2 : bags.get(aim)) {
						distances.setValue(bag1, bag2, 1);
					}
				}
			}
		}
	}

	private void getBagsForTreeNodes(HashMap<ProcessNode, Collection<BagOfWords>> nodes, RefinedProcessStructureTree.Node root, HashMap<Node, Collection<BagOfWords>> bags) {
		if (root.childrenSize() == 0) {
			for (int a = 0; a < root.nodesSize(); a++) {
				ProcessNode n = root.getNode(a);
				ArrayList<BagOfWords> b = new ArrayList<BagOfWords>(); 
				bags.put(root, b);
				for (BagOfWords bag : nodes.get(n)) {
					b.add(bag);
				}
			}
		}
		else {
			for (int a = 0; a < root.childrenSize(); a++) {
				this.getBagsForTreeNodes(nodes, root.getChild(a), bags);
			}
		}
	}
}
