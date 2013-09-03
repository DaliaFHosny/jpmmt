package de.lsem.process.matching.labelsimilarity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.lsem.process.model.ProcessModel;
import de.lsem.process.model.ProcessNode;
import de.lsem.word.Utils;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

class DocumentFrequencyCollectionRepository {
	private HashMap<String, Double> documentFrequencies = new HashMap<String, Double>();
	
	public DocumentFrequencyCollectionRepository(Collection<ProcessModel> models) {
		 int documents = this.initializeDocumentFrequencies(models);
		 List<Set<String>> termGroups = Utils.groupTerms(this.documentFrequencies.keySet());
		 this.updateDocumentFrequencies(termGroups, documents);
	}

	private void updateDocumentFrequencies(List<Set<String>> termGroups, int documents) {
		for (Set<String> group : termGroups) {
			double docs = 0;
			for (String term : group) {
				docs += this.getDocumentFrequency(term);
			}
			docs /= documents;
			
			for (String term : group) {
				this.documentFrequencies.put(term, docs);
			}
		}
		
	}

	private int initializeDocumentFrequencies(Collection<ProcessModel> models) {
		int documents = 0;
		for (ProcessModel model : models) {
			for (ProcessNode node : model.getActivities()) {
				documents++;
				for (String term : Utils.tokenizeAndRemoveStopWords(node.getLabel())) {
					this.documentFrequencies.put(term, this.getDocumentFrequency(term) + 1);
				}
			}
		}
		return documents;
	}

	public double getDocumentFrequency(String term) {
		return this.documentFrequencies.containsKey(term) ? this.documentFrequencies.get(term) : 0d;
	}
}
