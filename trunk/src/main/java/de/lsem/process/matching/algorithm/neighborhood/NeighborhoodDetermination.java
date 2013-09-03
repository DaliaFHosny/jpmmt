package de.lsem.process.matching.algorithm.neighborhood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.lsem.matrix.Matrix;
import de.lsem.process.model.BagOfWords;
import de.lsem.process.model.ProcessModel;
import de.lsem.process.model.ProcessNode;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public abstract class NeighborhoodDetermination {
	protected HashMap<String, Matrix<BagOfWords>> distances;
	
	protected NeighborhoodDetermination() {
		this.distances = new HashMap<String, Matrix<BagOfWords>>();
	}
	
	public List<BagOfWords> getNeighborBagOfWords(BagOfWords bag, Collection<BagOfWords> modelBags) {
		ProcessModel model = bag.getProcessModel();
		
		if (!this.distances.containsKey(model.getId())) {
			this.addModelDistances(model, modelBags);
		}
		
		return this.selectNeighbors(bag, this.distances.get(model.getId()), modelBags);
	}
	
	protected abstract List<BagOfWords> selectNeighbors(BagOfWords bag, Matrix<BagOfWords> matrix, Collection<BagOfWords> modelBags);

	protected abstract void addModelDistances(ProcessModel model, Collection<BagOfWords> modelBags);

	protected HashMap<ProcessNode, Collection<BagOfWords>> getNodeMapping(Collection<BagOfWords> modelBags) {
		HashMap<ProcessNode, Collection<BagOfWords>> nodes = new HashMap<ProcessNode, Collection<BagOfWords>>();
		
		for (BagOfWords bag : modelBags) {
			for (ProcessNode node : bag.getNodes()) {
				if (!nodes.containsKey(node)) {
					nodes.put(node, new ArrayList<BagOfWords>());
				}
				nodes.get(node).add(bag);
			}
		}
		
		return nodes;
	}	
}
