package de.lsem.process.matching.algorithm.fragmentSummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.lsem.matrix.Match;
import de.lsem.matrix.Matrix;
import de.lsem.matrix.ObjectComparer;
import de.lsem.process.matching.Fragment;
import de.lsem.process.matching.FragmentMatch;
import de.lsem.process.matching.ProcessMapping;
import de.lsem.process.matching.algorithm.basic.BasicAlgorithm;
import de.lsem.process.model.BagOfWords;
import de.lsem.process.model.ProcessModel;
import de.lsem.process.model.ProcessNode;
import de.lsem.process.rpst.RefinedProcessStructureTree;
import de.lsem.word.Utils;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class RpstSummaryAlgorithm extends BasicAlgorithm {
	public RpstSummaryAlgorithm(ObjectComparer<BagOfWords> comparer, double threshold) {
		super(comparer, threshold);
	}

	private RefinedProcessStructureTree tree1;
	private RefinedProcessStructureTree tree2;
	private List<BagOfWords> bags1;
	private List<BagOfWords> bags2;
	private Map<ProcessNode, RefinedProcessStructureTree.Node> processNodes;
	private List<Set<BagOfWords>> sets;
	
	@Override
	public ProcessMapping map(ProcessModel model1, ProcessModel model2) {
		this.tree1 = new RefinedProcessStructureTree(model1);
		this.tree2 = new RefinedProcessStructureTree(model2);
	
		this.bags1 = this.getBagOfWords(this.tree1);
		this.bags2 = this.getBagOfWords(this.tree2);
		
		Matrix<BagOfWords> sim = this.determineSimilarityMatrix(model1, model2);
		
		this.determineProcessMapping(sim, model1, model2);
		
		return null;
	}
	
	@Override
	protected ProcessMapping determineProcessMapping(Matrix<BagOfWords> sim, ProcessModel model1, ProcessModel model2) {
		Matrix<BagOfWords> matrix = sim;
		List<Match<BagOfWords>> matches = matrix.toDescendingSortedMatchList();
		
		Match<BagOfWords> currentMatch = null;
		while (matches.size() > 0 && matrix.getValue(matches.get(0).getObject1(), matches.get(1).getObject2()) > this.getMatchThreshold()) {
			// select current match
			currentMatch = matches.get(0);
			
			this.sets = new LinkedList<Set<BagOfWords>>();
			List<Set<BagOfWords>> linkedBags = this.getLinkedBags(currentMatch.getObject1(), currentMatch.getObject2());
			if (linkedBags.size() == 0) {
				HashSet<BagOfWords> set = new HashSet<BagOfWords>();
				set.add(currentMatch.getObject1());
				set.add(currentMatch.getObject2());
				matches.remove(0);
			}
			else {
				// check whether RPST allows summary 
				if (this.summaryPossible(currentMatch, linkedBags, model1, model2)) {
					List<BagOfWords> bags1 = new ArrayList<BagOfWords>();
					List<BagOfWords> bags2 = new ArrayList<BagOfWords>();
					
					this.seperateBagsOfWords(linkedBags, bags1, bags2, model1, model2);
					
					BagOfWords sumBag1 = this.summarizeBagOfWords(bags1, currentMatch.getObject1());
					BagOfWords sumBag2 = this.summarizeBagOfWords(bags2, currentMatch.getObject2());
					Set<BagOfWords> bags = new HashSet<BagOfWords>();
					bags.add(sumBag1);
					bags.add(sumBag2);
					this.bags1.add(sumBag1);
					this.bags2.add(sumBag2);
					this.sets.add(bags);
					
					// new SimilarityMatrix
					matrix = this.getMatrixCalculator().calculateMatrix(this.bags1, this.bags2);				
					matches = matrix.toDescendingSortedMatchList();
				}
				else {
					matches.remove(0);
				}
			}		
		}
		
		// transform matches to processmapping
		return this.createProcessMapping(model1, model2);
	}

	private BagOfWords summarizeBagOfWords(List<BagOfWords> bags, BagOfWords bag) {
		BagOfWords newBag = new BagOfWords(bag.getProcessModel());
		
		newBag.addBagOfWords(bag);
		newBag.addBagsOfWords(bags);
		
		return newBag;
	}

	private boolean summaryPossible(Match<BagOfWords> currentMatch, List<Set<BagOfWords>> linkedBags, ProcessModel model1, ProcessModel model2) {
		List<BagOfWords> bags1 = new ArrayList<BagOfWords>();
		bags1.add(currentMatch.getObject1());
		List<BagOfWords> bags2 = new ArrayList<BagOfWords>();
		bags2.add(currentMatch.getObject1());
		this.seperateBagsOfWords(linkedBags, bags1, bags2, model1, model2);
		
		if (this.checkRpst(bags1, this.tree1)) {
			return this.checkRpst(bags2, this.tree2);
		}
		
		return false;
	}
	
	private boolean checkRpst(List<BagOfWords> bags, RefinedProcessStructureTree tree) {
		Set<RefinedProcessStructureTree.Node> nodes = new HashSet<RefinedProcessStructureTree.Node>();
		for (BagOfWords bag : bags) {
			for (ProcessNode node : bag.getNodes()) {
				nodes.add(this.processNodes.get(node));
			}
		}
		
		Set<RefinedProcessStructureTree.Node> parents = new HashSet<RefinedProcessStructureTree.Node>(); 
		for (RefinedProcessStructureTree.Node n : nodes) {
			parents.add(n.getParent());
		}
		
		// TODO: check whether they can be summarized or not, e.g. in case of a polygon (sequence) each node needs to be adjacent to at least one other node
		if (parents.size() == 1) {
			return true;
		}
				
		return false;
	}

	private void seperateBagsOfWords(List<Set<BagOfWords>> bags, List<BagOfWords> bags1, List<BagOfWords> bags2, ProcessModel model1, ProcessModel model2) {
		for (Set<BagOfWords> set : bags) {
			for (BagOfWords bag : set) {
				if (model1 == bag.getProcessModel()) {
					bags1.add(bag);
				}
				else if (model2 == bag.getProcessModel()) {
					bags2.add(bag);
				}
			}
		}
	}

	private ProcessMapping createProcessMapping(ProcessModel model1, ProcessModel model2) {
		ProcessMapping mapping = new ProcessMapping(model1, model2);
		
		for (Set<BagOfWords> set : this.sets) {
			FragmentMatch match = new FragmentMatch();
			match.setFragment1(new Fragment());
			match.setFragment2(new Fragment());
			mapping.addFragmentMatch(match);
			for (BagOfWords bag : set) {
				if (bag.getProcessModel() == model1) {
					this.addNodesToFragment(bag, match.getFragment1());
				}
				else if (bag.getProcessModel() == model2) {
					this.addNodesToFragment(bag, match.getFragment2());
				}
			}
			
		}
		
		return mapping;
	}

	private void addNodesToFragment(BagOfWords bag, Fragment fragment) {
		for (ProcessNode node : bag.getNodes()) {
			fragment.addProcessNode(node);
		}
	}

	private List<Set<BagOfWords>> getLinkedBags(BagOfWords bag1, BagOfWords bag2) {
		List<Set<BagOfWords>> linkedBags = new ArrayList<Set<BagOfWords>>();
		
		for (Set<BagOfWords> set : this.sets) {
			if (set.contains(bag1) || set.contains(bag2)) {
				linkedBags.add(set);
			}
		}
		
		return linkedBags;
	}

	private List<BagOfWords> getBagOfWords(RefinedProcessStructureTree tree) {
		this.processNodes = new HashMap<ProcessNode, RefinedProcessStructureTree.Node>();
		List<BagOfWords> bags = new ArrayList<BagOfWords>();		
		this.traverseTree(bags, tree.getRoot());				
		return bags;
	}

	private void traverseTree(List<BagOfWords> bags, RefinedProcessStructureTree.Node root) {
		if (root.childrenSize() == 0) {
			BagOfWords bag = new BagOfWords(root.getTree().getModel());
			for (int a = 0; a < root.nodesSize(); a++) {
				ProcessNode node = root.getNode(a);
				bag.addBagOfWords(bag);
				bag.addWords(Utils.tokenizeAndRemoveStopWords(node.getLabel()));
				// activities should not be an exit or an entry of any fragment 
				this.processNodes.put(node, root);
			}
			bags.add(bag);	
		}
		else {
			for (int a = 0; a < root.childrenSize(); a++) {
				this.traverseTree(bags, root.getChild(a));
			}
		}
	}

}
