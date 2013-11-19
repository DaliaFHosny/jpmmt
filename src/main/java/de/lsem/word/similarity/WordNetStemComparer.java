package de.lsem.word.similarity;

import java.util.List;

import de.lsem.matrix.ObjectComparer;
import de.lsem.word.Utils;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public final class WordNetStemComparer implements ObjectComparer<String> {
	private ObjectComparer<String> wordSimilarity;
	
	public WordNetStemComparer(ObjectComparer<String> wordSimilarity) {
		this.wordSimilarity = wordSimilarity;
	}
	
	@Override
	public double compare(String word1, String word2) {
		List<String> stems1 = Utils.stemWordNet(word1);
		List<String> stems2 = Utils.stemWordNet(word2);
		
		double max = 0;		
		for (String stem1 : stems1) {
			if (stem1 != null) {
				for (String stem2 : stems2) {
					if (stem2 != null) {
						double val = this.wordSimilarity.compare(stem1, stem2);
						max = val > max ? val : max;
					}
				}
			}
		}		
		return max;
	}
	
}
