package de.lsem.process.matching.algorithm.fragmentSummary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.lsem.matrix.Match;
import de.lsem.process.model.BagOfWords;
import de.lsem.process.model.ProcessModel;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class DefaultFragmentSummary extends FragmentSummary {

	@Override
	public boolean summarize(Set<Match<BagOfWords>> matches, HashMap<BagOfWords, BagOfWords> bagMatches, Collection<BagOfWords> bags1, Collection<BagOfWords> bags2) {
		boolean summarized = false;
		
		List<HashSet<BagOfWords>> bags = new ArrayList<HashSet<BagOfWords>>();
		for (BagOfWords bag1 : bagMatches.keySet()) {
			HashSet<BagOfWords> set = new HashSet<BagOfWords>();
			set.add(bag1);
			set.add(bagMatches.get(bag1));
			bags.add(set);
		}
		
		ProcessModel model1 = null;
		ProcessModel model2 = null;
		
		for (Match<BagOfWords> match : matches) {
			model1 = this.checkBag(model1, match.getObject1().getProcessModel());
			model2 = this.checkBag(model2, match.getObject2().getProcessModel());
						
			List<HashSet<BagOfWords>> matchBags = new ArrayList<HashSet<BagOfWords>>();
			for (HashSet<BagOfWords> set : bags) {
				if (set.contains(match.getObject1()) || set.contains(match.getObject2())) {
					matchBags.add(set);
				}
			}			
			// new 1:1
			if (matchBags.size() == 0) {
				HashSet<BagOfWords> s = new HashSet<BagOfWords>();
				s.add(match.getObject1());
				s.add(match.getObject2());
				bags.add(s);
			}			
			// new 1:m or n:1
			else if (matchBags.size() == 1) {
				matchBags.get(0).add(match.getObject1());
				matchBags.get(0).add(match.getObject2());
			}			
			// new n:m
			else {
				HashSet<BagOfWords> s = new HashSet<BagOfWords>();
				s.add(match.getObject1());
				s.add(match.getObject2());
				for (HashSet<BagOfWords> mbs : matchBags) {
					s.addAll(mbs);
					bags.remove(mbs);
				}
				bags.add(s);
				summarized = true;
			}
		}	
		
		if (summarized) {
			bagMatches.clear();
						
			for (HashSet<BagOfWords> bs : bags) {
				BagOfWords bag1 = new BagOfWords(model1);
				BagOfWords bag2 = new BagOfWords(model2);
				
				for (BagOfWords b : bs) {
					if (bags1.contains(b)) {
						bag1.addBagOfWords(b);						
					}
					else {
						bag2.addBagOfWords(b);
					}
				}
				
				bagMatches.put(bag1, bag2);
			}
			
			bags1.clear();
			bags2.clear();
			
			for (BagOfWords b1 : bagMatches.keySet()) {
				bags1.add(b1);
				bags2.add(bagMatches.get(b1));
			}
		}
				
		return summarized;
	}

	private ProcessModel checkBag(ProcessModel model1, ProcessModel processModel) {
		if (model1 == null) {			
			return processModel;
		}
		else if (processModel != model1) {
			// TODO: throw exception
		}
		
		return model1;
	}
}
