package de.lsem.process.matching.labelsimilarity;

import java.util.HashMap;

import de.lsem.matrix.ObjectComparer;
import de.lsem.process.model.BagOfWords;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class BasicBagOfWordsComparer implements ObjectComparer<BagOfWords>  {
	private ObjectComparer<String> wordComparer;	
	
	public BasicBagOfWordsComparer(ObjectComparer<String> wordComparer) {
		this.wordComparer = wordComparer;
	}
	
	protected ObjectComparer<String> getObjectComparer() {
		return this.wordComparer;
	}
	
	protected double average(BagOfWords bag1, BagOfWords bag2, HashMap<String, Double> values) {
		double sim = 0;
		
		for (String word : bag1.getWords()) {
			sim += values.get(word);
		}
		
		for (String word : bag2.getWords()) {
			sim += values.get(word);
		}
		return sim / (bag1.wordsSize() + bag2.wordsSize());
	}
	
	protected HashMap<String, Double> calculateSimilarities(BagOfWords bag1, BagOfWords bag2) {
		HashMap<String, Double> maxSim = new HashMap<String, Double>();
		for (String word1 : bag1.getWords()) {
			for (String word2 : bag2.getWords()) {
				double sim = this.wordComparer.compare(word1, word2);
				if (!maxSim.containsKey(word1) || maxSim.get(word1) <= sim) {
					maxSim.put(word1, sim);
				}
				if (!maxSim.containsKey(word2) || maxSim.get(word2) <= sim) {
					maxSim.put(word2, sim);
				}
			}
		}
		return maxSim;
	}
	
	@Override
	public double compare(BagOfWords bag1, BagOfWords bag2) {
		HashMap<String, Double> maxSim = this.calculateSimilarities(bag1, bag2);		
		return this.average(bag1, bag2, maxSim);
	}	
}
