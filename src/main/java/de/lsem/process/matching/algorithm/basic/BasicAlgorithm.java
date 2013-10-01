package de.lsem.process.matching.algorithm.basic;

import java.util.Collection;
import java.util.List;

import de.lsem.matrix.Match;
import de.lsem.matrix.Matrix;
import de.lsem.matrix.MatrixCalculator;
import de.lsem.matrix.ObjectComparer;
import de.lsem.process.matching.Fragment;
import de.lsem.process.matching.FragmentMatch;
import de.lsem.process.matching.ProcessMapping;
import de.lsem.process.matching.ProcessMappingManager;
import de.lsem.process.matching.algorithm.MappingAlgorithm;
import de.lsem.process.model.BagOfWords;
import de.lsem.process.model.ProcessModel;
import de.lsem.process.model.ProcessNode;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

/**
 * 
 * @author Christopher Klinkmüller
 *
 */
public class BasicAlgorithm extends MappingAlgorithm {
	private double matchThreshold = 0.75;
	private MatrixCalculator<BagOfWords> matrixCalculator;
	
	public BasicAlgorithm(ObjectComparer<BagOfWords> comparer, double threshold) {
		this.matrixCalculator = new MatrixCalculator<BagOfWords>(comparer);
		this.matchThreshold = threshold;
	}
	
	public MatrixCalculator<BagOfWords> getMatrixCalculator() {
		return this.matrixCalculator;
	}
	
	public void setMatrixCalculator(MatrixCalculator<BagOfWords> matrixCalculator) {
		this.matrixCalculator = matrixCalculator;
	}
	
	public double getMatchThreshold() {
		return this.matchThreshold;
	}
	
	public void setMatchThreshold(double threshold) {
		this.matchThreshold = threshold;
	}
	
	public ProcessMapping map(ProcessModel process1, ProcessModel process2) {
		Matrix<BagOfWords> sim = this.determineSimilarityMatrix(process1, process2);
		return this.determineProcessMapping(sim, process1, process2);		
	}
	
	protected ProcessMapping determineProcessMapping(Matrix<BagOfWords> sim, ProcessModel process1, ProcessModel process2) {
		List<Match<BagOfWords>> matches = sim.toDescendingSortedMatchList();	
				
		ProcessMapping processMapping = new ProcessMapping(process1, process2);
		
		int i = 0;
		while (i < matches.size() && sim.getValue(matches.get(i).getObject1(), matches.get(i).getObject2()) >= this.matchThreshold) {
			Match<BagOfWords> selectedMatch = matches.get(i);
			for (ProcessNode node1 : selectedMatch.getObject1().getNodes()) {
				for (ProcessNode node2 : selectedMatch.getObject2().getNodes()) {
					Match<ProcessNode> match = new Match<ProcessNode>(node1, node2);
					this.addMatchToProcessMapping(processMapping, match);					
				}
			}
			matches.remove(i);
		}
		
		return processMapping;
	}

	private void addMatchToProcessMapping(ProcessMapping processMapping, Match<ProcessNode> match) {
		Fragment fragment1 = new Fragment();
		fragment1.addProcessNode(match.getObject1());
		
		Fragment fragment2 = new Fragment();
		fragment2.addProcessNode(match.getObject2());
		
		processMapping.addFragmentMatch(new FragmentMatch(fragment1, fragment2));		
	}

	protected Matrix<BagOfWords> determineSimilarityMatrix(ProcessModel process1, ProcessModel process2) {
		Collection<BagOfWords> bags1 = BagOfWords.getBagsOfWords(process1.getActivities());
		Collection<BagOfWords> bags2 = BagOfWords.getBagsOfWords(process2.getActivities());
		return this.matrixCalculator.calculateMatrix(bags1, bags2);		
	}	
}
