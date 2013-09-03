package de.lsem.process.matching.labelsimilarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.lsem.matrix.ObjectComparer;
import de.lsem.process.model.BagOfWords;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public abstract class PruningComparer extends BasicBagOfWordsComparer implements Comparator<String> {
	protected HashMap<String, Double> values;	
	
	public PruningComparer(ObjectComparer<String> wordComparer) {
		super(wordComparer);
	}
	
	@Override
	protected double average(BagOfWords bag1, BagOfWords bag2, HashMap<String, Double> values) {
		if (bag1.wordsSize() == bag2.wordsSize()) {
			return super.average(bag1, bag2, values);
		}
		else if (bag1.wordsSize() > bag2.wordsSize()) {
			return super.average(this.prune(bag1, bag2.wordsSize(), values), bag2, values);
		}
		else {
			return super.average(bag1, this.prune(bag2, bag1.wordsSize(), values), values);
		}
	}
	
	protected BagOfWords prune(BagOfWords bag, int number, HashMap<String, Double> values) {	
		this.values = values;
		
		List<String> words = new ArrayList<String>();
		for (String w : bag.getWords()) {
			words.add(w);
		}
		
		Collections.sort(words, this);		
		
		BagOfWords newBag = new BagOfWords(bag.getProcessModel());
		newBag.addNodes(bag.getNodes());
		newBag.addWords(words.subList(0, number));
		
		return newBag;
	}
	
	public abstract int compare(String s1, String s2);
}
