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
				BagOfWords bag = new BagOfWords(node.getModel());
				bag.addNode(node);
				
				for (String word : Utils.tokenizeAndRemoveStopWords(node.getLabel())) {
					bag.addWord(word);
				}
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
}
