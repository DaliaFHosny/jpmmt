package de.lsem.process.matching.algorithm.fragmentSummary;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.lsem.matrix.Match;
import de.lsem.matrix.Matrix;
import de.lsem.matrix.MatrixCalculator;
import de.lsem.process.matching.FragmentMatch;
import de.lsem.process.matching.ProcessMapping;
import de.lsem.process.matching.algorithm.MappingAlgorithm;
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

public class SummaryAlgorithm extends MappingAlgorithm {
	private double threshold = 0.75;
	private MatrixCalculator<BagOfWords> matrixCalculator;
	private MatchIdentifier matchIdentifier;
	private FragmentSummary fragmentSummary;
	
	public SummaryAlgorithm(FragmentSummary fragmentSummary, MatrixCalculator<BagOfWords> matrixCalculator, double threshold) {
		this.matrixCalculator = matrixCalculator;
		this.threshold = threshold;
		this.matchIdentifier = new MatchIdentifier();
		this.fragmentSummary = fragmentSummary;
	}
		
	public FragmentSummary getFragmentSummary() {
		return this.fragmentSummary;
	}
	
	public MatrixCalculator<BagOfWords> getMatrixCalculator() {
		return this.matrixCalculator;
	}
	
	public void setMatrixCalculator(MatrixCalculator<BagOfWords> matrixCalculator) {
		this.matrixCalculator = matrixCalculator;
	}
	
	public double getThreshold() {
		return this.threshold;
	}
	
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	public ProcessMapping map(ProcessModel process1, ProcessModel process2) {
		Collection<BagOfWords> bags1 = BagOfWords.getBagsOfWords(process1.getActivities());
		Collection<BagOfWords> bags2 = BagOfWords.getBagsOfWords(process2.getActivities());
		
		HashMap<BagOfWords, BagOfWords> bagMatches = new HashMap<BagOfWords, BagOfWords>();
		
		// calculate Similarities 
		Matrix<BagOfWords> similarityMatrix = this.matrixCalculator.calculateMatrix(bags1, bags2);
		
		this.matchIdentifier.determine(similarityMatrix, bagMatches);
		
		while (this.matchIdentifier.getSim() >= this.threshold) {
			if (this.fragmentSummary.summarize(this.matchIdentifier.getMatches(), bagMatches, bags1, bags2)) {
				similarityMatrix = this.matrixCalculator.calculateMatrix(bags1, bags2);
			}
		}
		
		return this.createProcessMapping(process1, process2, bagMatches);
	}
	
	private ProcessMapping createProcessMapping(ProcessModel process1, ProcessModel process2, HashMap<BagOfWords, BagOfWords> bagMatches) {
		ProcessMapping processMapping = new ProcessMapping(process1, process2);
		
		for (BagOfWords bag1 : bagMatches.keySet()) {
			BagOfWords bag2 = bagMatches.get(bag1);
			
			FragmentMatch fragmentMatch = new FragmentMatch();
			for (ProcessNode node1 : bag1.getNodes()) {
				fragmentMatch.getFragment1().addProcessNode(node1);
			}
			for (ProcessNode node2 : bag2.getNodes()) {
				fragmentMatch.getFragment2().addProcessNode(node2);
			}
			
			processMapping.addFragmentMatch(fragmentMatch);
		}
		
		return processMapping;
	}

	private class MatchIdentifier {
		private double sim = 0;
		private Set<Match<BagOfWords>> matches;
		
		public double getSim() {
			return this.sim;
		}
		
		public Set<Match<BagOfWords>> getMatches() {
			return this.matches;
		}
		
		public void determine(Matrix<BagOfWords> similarityMatrix, HashMap<BagOfWords, BagOfWords> bagMatches) {
			this.sim = 0;
			this.matches = new HashSet<Match<BagOfWords>>();
			
			for (BagOfWords bag1 : similarityMatrix.getObjects1()) {
				for (BagOfWords bag2 : similarityMatrix.getObjects2()) {
					double value = similarityMatrix.getValue(bag1, bag2);
					if (!(bagMatches.containsKey(bag1) && bagMatches.get(bag1) == bag2) && this.sim <= value) {
						if (this.sim < value) {
							this.matches.clear();
						}
						this.matches.add(new Match<BagOfWords>(bag1, bag2));						
						this.sim = value;
					}
				}
			}
		}
	}
}
