package de.lsem.process.matching.algorithm.neighborhood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

public class DistanceNeighborhoodDetermination extends NeighborhoodDetermination {
	private HashMap<String, Matrix<BagOfWords>> distances;
	private double maxdistance;
	
	public DistanceNeighborhoodDetermination(int maxdistance) {
		this.maxdistance = maxdistance;
	}
	
	@Override
	protected void addModelDistances(ProcessModel model, Collection<BagOfWords> modelBags) {
		Matrix<BagOfWords> bagOfWords = new Matrix<BagOfWords>(modelBags, modelBags, -1);
		this.distances.put(model.getId(), bagOfWords);
		
		HashMap<ProcessNode, Collection<BagOfWords>> nodes = this.getNodeMapping(modelBags);
		Matrix<ProcessNode> distances = this.getDistances(nodes.keySet(), model);
		this.transfromDistanceMatrix(distances, bagOfWords, nodes);		
	}

	private void transfromDistanceMatrix(Matrix<ProcessNode> distances, Matrix<BagOfWords> bagOfWords,	HashMap<ProcessNode, Collection<BagOfWords>> nodes) {
		for (ProcessNode node1 : distances.getObjects1()) {
			for (ProcessNode node2 : distances.getObjects2()) {
				double nodeVal = distances.getValue(node1, node2);
				for (BagOfWords bag1 : nodes.get(node1)) {
					for (BagOfWords bag2 : nodes.get(node2)) {
						double curVal = bagOfWords.getValue(bag1, bag2); 
						if (curVal < 0 || curVal < nodeVal) {
							bagOfWords.setValue(bag1, bag2, curVal);
						}
					}
				}
			}
		}
	}

	private Matrix<ProcessNode> getDistances(Set<ProcessNode> nodes, ProcessModel model) {
		Matrix<ProcessNode> distances = new Matrix<ProcessNode>(nodes, nodes, -1);
		
		for (ProcessNode node : nodes) {
			this.setDistances(distances, nodes, node, model);
		}
		
		return distances;
	}

	private void setDistances(Matrix<ProcessNode> distances, Set<ProcessNode> nodes, ProcessNode node, ProcessModel model) {
		HashMap<ProcessNode, Integer> dist = new HashMap<ProcessNode, Integer>();
		List<ProcessNode> queue = new ArrayList<ProcessNode>();
		for (ProcessNode n : nodes) {
			dist.put(n, Integer.MAX_VALUE);
			queue.add(n);
		}
		dist.put(node, 0);
		
		while (queue.size() != 0) {
			ProcessNode small = this.getProcessNodeWithSmallestDistance(queue, dist);
			queue.remove(small);
			
			if (dist.get(small) == Integer.MAX_VALUE) {
				break;
			}
			
			List<ProcessNode> neighbors = this.getNeighbors(model, nodes, small, queue, new ArrayList<ProcessNode>());
			for (ProcessNode neighbor : neighbors) {
				int alt = dist.get(small) + 1;
				if (alt < dist.get(neighbor.getId())) {
					dist.put(neighbor, alt);					
				}
			}
			
		}
		
		for (ProcessNode n : nodes) {
			distances.setValue(node, n, dist.get(n));
		}
	}

	private List<ProcessNode> getNeighbors(ProcessModel model, Set<ProcessNode> modelNodes, ProcessNode cur, List<ProcessNode> queue, ArrayList<ProcessNode> visited) {
		List<ProcessNode> nodes = new ArrayList<ProcessNode>();
		visited.add(cur);
		
		for (ProcessNode node : model.getAdjacentNodes(cur)) {
			if (modelNodes.contains(node)) {
				if (queue.contains(node)) {
					nodes.add(node);
				}
				else if (!visited.contains(node)) {
					this.getNeighbors(model, modelNodes, node, queue, visited);
				}
			}
		}
		
		return nodes;
	}

	private ProcessNode getProcessNodeWithSmallestDistance(List<ProcessNode> queue, HashMap<ProcessNode, Integer> dist) {
		ProcessNode node = null;
		
		int min = Integer.MAX_VALUE;
		for (ProcessNode n : queue) {
			if (min > dist.get(n)) {
				min = dist.get(n);
				node = n;
			}
		}
		
		return node;
	}

	@Override
	protected List<BagOfWords> selectNeighbors(BagOfWords bag, Matrix<BagOfWords> matrix, Collection<BagOfWords> modelBags) {
		List<BagOfWords> neighbors = new ArrayList<BagOfWords>();
		
		for (BagOfWords modelBag : modelBags) {
			if (matrix.getValue(bag, modelBag) <= this.maxdistance) {
				neighbors.add(modelBag);
			}
		}
		
		return neighbors;
	}

}
