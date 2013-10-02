package de.lsem.process.matching.algorithm.neighborhood;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.lsem.matrix.Match;
import de.lsem.matrix.Matrix;
import de.lsem.matrix.ObjectComparer;
import de.lsem.process.matching.ProcessMapping;
import de.lsem.process.matching.ProcessMappingManager;
import de.lsem.process.matching.algorithm.basic.BasicAlgorithm;
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

public class NeighborhoodAlgorithm extends BasicAlgorithm {
	protected double weight;
	protected NeighborhoodDetermination neighborhoodDetermination;
	private ProcessMappingManager manager;
	
	public NeighborhoodAlgorithm(ObjectComparer<BagOfWords> comparer, NeighborhoodDetermination neighborhoodDetermination, double threshold, double distanceWeight) {
		super(comparer, threshold);
		this.weight = distanceWeight;
		this.neighborhoodDetermination = neighborhoodDetermination;
		this.manager = new ProcessMappingManager();
	}

	@Override
	protected ProcessMapping determineProcessMapping(Matrix<BagOfWords> sim, ProcessModel process1, ProcessModel process2) {
		List<Match<BagOfWords>> matches = sim.toDescendingSortedMatchList();	
		
		ProcessMapping processMapping = new ProcessMapping(process1, process2);
		
		while (sim.getValue(matches.get(0).getObject1(), matches.get(0).getObject2()) >= this.getMatchThreshold()) {
			Match<BagOfWords> selectedMatch = matches.get(0);
			for (ProcessNode node1 : selectedMatch.getObject1().getNodes()) {
				for (ProcessNode node2 : selectedMatch.getObject2().getNodes()) {
					Match<ProcessNode> match = new Match<ProcessNode>(node1, node2);
					this.manager.addNodeMatch(match, processMapping);
				}
			}
			this.updateMatchList(matches, sim, selectedMatch);			
		}
		
		return processMapping;
	}

	private void updateMatchList(List<Match<BagOfWords>> matches, final Matrix<BagOfWords> sim, Match<BagOfWords> selectedMatch) {
		sim.setValue(selectedMatch.getObject1(), selectedMatch.getObject2(), -1d);
		
		List<BagOfWords> bags1 = this.neighborhoodDetermination.getNeighborBagOfWords(selectedMatch.getObject1(), sim.getObjects1());
		List<BagOfWords> bags2 = this.neighborhoodDetermination.getNeighborBagOfWords(selectedMatch.getObject2(), sim.getObjects2());
		
		for (BagOfWords bag1 : bags1) {
			for (BagOfWords bag2 : bags2) {
				double s = sim.getValue(bag1, bag2);
				s = s * this.weight > 1 ? 1 : s * this.weight;
				sim.setValue(bag1, bag2, s);
			}
		}
		
		Collections.sort(matches, new Comparator<Match<BagOfWords>>() {
			@Override
			public int compare(Match<BagOfWords> match1, Match<BagOfWords> match2) {
				double sim1 = sim.getValue(match1.getObject1(), match1.getObject2());
				double sim2 = sim.getValue(match2.getObject1(), match2.getObject2());
				return sim1 > sim2 ? -1 : (Math.abs(sim1 - sim2) < 0.0001 ? 0 : 1);
			}			
		});
	}
}
