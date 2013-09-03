package de.lsem.word.similarity;

import java.util.ArrayList;
import java.util.List;

import de.lsem.matrix.ObjectComparer;
import de.lsem.word.Utils;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public final class WordNetStemComparer implements ObjectComparer<String> {
	private ObjectComparer<String> wordSimilarity;
	
	public WordNetStemComparer(ObjectComparer<String> wordSimilarity) {
		if (wordSimilarity instanceof WordNetStemComparer) {
			//TODO: Exception
		}
		else {
			this.wordSimilarity = wordSimilarity;
		}
	}
	
	@Override
	public double compare(String word1, String word2) {
		List<String> stems1 = this.getStems(word1);
		List<String> stems2 = this.getStems(word2);
		
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

	private List<String> getStems(String word) {
		ArrayList<String> stems = new ArrayList<String>();
		
		stems.addAll(Utils.stemWordNetAdjective(word));
		stems.addAll(Utils.stemWordNetAdverb(word));
		stems.addAll(Utils.stemWordNetVerb(word));
		stems.addAll(Utils.stemWordNetNoun(word));
		
		return stems;
	}
}
