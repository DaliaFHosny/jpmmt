package de.lsem.process.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import de.lsem.word.Utils;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class BagOfWords {
	private Multiset<String> words;
	private Set<ProcessNode> nodes;
	private ProcessModel model;

	public BagOfWords(ProcessModel model) {
		this.words = HashMultiset.create();
		this.nodes = new HashSet<ProcessNode>();
		this.model = model;
	}
	
	public BagOfWords(ProcessNode node) {
		this(node.getModel());
		this.addNode(node);
		
		for (String word : Utils.tokenizeAndRemoveStopWords(node.getLabel())) {
			this.addWord(word);
		}	
	}
	
	public ProcessModel getProcessModel() {
		return this.model;
	}
	
	public void addWord(String word) {
		this.words.add(word);
	}
	
	public void addWords(Iterable<String> words) {
		for (String word : words) {
			this.words.add(word);
		}
	}
	
	public void removeWord(String word) {
		this.words.remove(word);
	}
	
	public int wordsSize() {
		return this.words.size();
	}
	
	public Iterable<String> getWords() {
		return this.words;
	}
	
	public void addNode(ProcessNode node) {
		if (node.getModel() != this.model) {
			//TODO: throw Exception
		}
		this.nodes.add(node);
	}
	
	public void addNodes(Iterable<ProcessNode> nodes) {
		for (ProcessNode node : nodes) {
			this.addNode(node);
		}
	}
	
	public void removeNode(ProcessNode node) {
		this.nodes.remove(node);
	}
	
	public int nodesSize() {
		return this.nodes.size();
	}
	
	public Iterable<ProcessNode> getNodes() {
		return this.nodes;
	}
	
	public void addBagsOfWords(Collection<BagOfWords> bagsOfWords) {
		for (BagOfWords bag : bagsOfWords) {
			this.addBagOfWords(bag);
		}
	}
	
	public void addBagOfWords(BagOfWords bagOfWords) {
		this.addNodes(bagOfWords.getNodes());
		
		for (String word : bagOfWords.words) {
			this.words.add(word);
		}
	}
	
	public void addNodeAndWords(ProcessNode node) {
		this.addNode(node);
		this.addWords(Utils.tokenizeAndRemoveStopWords(node.getLabel()));
	}
	
	public static Collection<BagOfWords> getBagsOfWords(Collection<ProcessNode> nodes) {
		ArrayList<BagOfWords> bags = new ArrayList<BagOfWords>();
		
		for (ProcessNode node : nodes) {
			if (node.isActivity() || node.getLabel().equals("")) {
				BagOfWords bag = new BagOfWords(node);
				
				if (bag.wordsSize() != 0) {
					bags.add(bag);
				}				
			}
		}
		
		return bags;
	}
	
	public boolean containsNode(ProcessNode node) {
		return this.nodes.contains(node);
	}	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((model == null) ? 0 : model.getId().hashCode());
		
		if (nodes == null || nodes.size() == 0) {
			result *= prime;
		}
		else {
			for (ProcessNode n : nodes) {
				result = prime * result + ((n == null) ? 0 :  n.getId().hashCode());
			}
		}
		
		if (words == null || words.size() == 0) {
			result *= prime;
		}
		else {
			for (String w : words) {
				result = prime * result + ((w == null) ? 0 : w.hashCode());
			}
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !BagOfWords.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		
		BagOfWords bag = (BagOfWords)obj;
		if (!(nodesContained(bag, this) && nodesContained(this, bag))) {
			return false;
		}
		
		if (!(wordsContained(bag, this) && wordsContained(this, bag))) {
			return false;
		}
		
		return true;
	}
	

	private static boolean wordsContained(BagOfWords bag1, BagOfWords bag2) {
		for (String node : bag1.words) {
			if (!bag2.words.contains(node)) {
				return false;
			}
		}		
		return true;
	}
	
	private static boolean nodesContained(BagOfWords bag1, BagOfWords bag2) {
		for (ProcessNode node : bag1.nodes) {
			if (!bag2.nodes.contains(node)) {
				return false;
			}
		}		
		return true;
	}
}
