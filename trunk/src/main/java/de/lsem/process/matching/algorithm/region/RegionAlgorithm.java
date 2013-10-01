package de.lsem.process.matching.algorithm.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.lsem.matrix.Match;
import de.lsem.matrix.Matrix;
import de.lsem.matrix.ObjectComparer;
import de.lsem.process.matching.algorithm.basic.BasicAlgorithm;
import de.lsem.process.matching.util.MappingDeterminer;
import de.lsem.process.model.BagOfWords;
import de.lsem.process.model.ProcessModel;
import de.lsem.process.model.ProcessNode;
import de.lsem.process.rpst.RefinedProcessStructureTree;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class RegionAlgorithm extends BasicAlgorithm {
	private double regionPenalty;
	private double regionThreshold;
	
	public RegionAlgorithm(ObjectComparer<BagOfWords> comparer, double matchThreshold, double regionPenalty, double regionThreshold) {
		super(comparer, matchThreshold);
		this.regionThreshold = regionThreshold;
		this.regionPenalty = regionPenalty;
	}
	
	@Override
	protected Matrix<BagOfWords> determineSimilarityMatrix(ProcessModel process1, ProcessModel process2) {
		// determine bag of words for activities
		Collection<BagOfWords> bags1 = BagOfWords.getBagsOfWords(process1.getActivities());
		Collection<BagOfWords> bags2 = BagOfWords.getBagsOfWords(process2.getActivities());
				
		// calculate similarity
		Matrix<BagOfWords> sim = this.getMatrixCalculator().calculateMatrix(bags1, bags2);
		
		// update similarity by regional affiliation
		this.updateSimilarityUsingRegions(sim, process1, process2);
		return sim;
	}

	private void updateSimilarityUsingRegions(Matrix<BagOfWords> sim, ProcessModel model1, ProcessModel model2) {
		List<BagOfWords> regionBags1 = this.getTopLevelBagOfWords(model1);
		List<BagOfWords> regionBags2 = this.getTopLevelBagOfWords(model2);
		
		Matrix<BagOfWords> matrix = this.getMatrixCalculator().calculateMatrix(regionBags1, regionBags2);
		this.updateRegionValues(sim, matrix);
	}

	private void updateRegionValues(Matrix<BagOfWords> modelSim, Matrix<BagOfWords> topLevelSim) {
		List<Match<BagOfWords>> bagMatches = topLevelSim.toDescendingSortedMatchList();
		
		// TODO: use ProcessMapping instead of MappingDeterminer
		MappingDeterminer<BagOfWords> determiner = new MappingDeterminer<BagOfWords>();		
		while (bagMatches.size() > 0 && topLevelSim.getValue(bagMatches.get(0).getObject1(), bagMatches.get(1).getObject2()) > this.regionThreshold) {
			determiner.addMatch(bagMatches.get(0));
			bagMatches.remove(0);
		}
		
		HashMap<BagOfWords, BagOfWords> toplevel1 = assignTopLevelBagOfWords(topLevelSim.getObjects1(), modelSim.getObjects1());
		HashMap<BagOfWords, BagOfWords> toplevel2 = assignTopLevelBagOfWords(topLevelSim.getObjects2(), modelSim.getObjects2());
		
		for (BagOfWords bag1 : modelSim.getObjects1()) {
			for (BagOfWords bag2 : modelSim.getObjects2()) {
				double sim = topLevelSim.getValue(toplevel1.get(bag1), toplevel2.get(bag2)) >= this.regionThreshold ? 1 : this.regionPenalty;
				sim *= modelSim.getValue(bag1, bag2);
				modelSim.setValue(bag1, bag2, sim);
			}
		}
	}
	
	private HashMap<BagOfWords, BagOfWords> assignTopLevelBagOfWords(Collection<BagOfWords> topLevelBags, Collection<BagOfWords> modelBags) {
		HashMap<BagOfWords, BagOfWords> map = new HashMap<BagOfWords, BagOfWords>();
		
		for (BagOfWords bag : modelBags) {
			for (BagOfWords top : topLevelBags) {
				boolean contain = true;
				for (ProcessNode n : bag.getNodes()) {
					if (!top.containsNode(n)) {
						contain = false;
					}
				}
				if (contain) {
					map.put(bag, top);
					break;
				}
			}
		}
		
		return map;
	}

	private List<BagOfWords> getTopLevelBagOfWords(ProcessModel model) {
		RefinedProcessStructureTree rpst = new RefinedProcessStructureTree(model);
		List<BagOfWords> bags = new ArrayList<BagOfWords>();
		
		for (int a = 0; a < rpst.getRoot().childrenSize(); a++) {
			RefinedProcessStructureTree.Node rpstNode = rpst.getRoot().getChild(a);
			BagOfWords bag = new BagOfWords(model);
			
			for (int b = 0; b < rpstNode.nodesSize(); b++) {
				ProcessNode node = rpstNode.getNode(b);
				if (node.isActivity()) {
					bag.addNodeAndWords(node);
				}
			}
			
			bags.add(bag);			
		}
		
		return bags;
	}

}
