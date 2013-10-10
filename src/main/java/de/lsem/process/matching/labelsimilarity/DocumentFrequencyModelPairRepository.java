package de.lsem.process.matching.labelsimilarity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.lsem.process.model.ProcessModel;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class DocumentFrequencyModelPairRepository {
	private HashMap<String, HashMap<String, DocumentFrequencyCollectionRepository>> repositories = new HashMap<String, HashMap<String, DocumentFrequencyCollectionRepository>>();
	
	public DocumentFrequencyModelPairRepository(Collection<ProcessModel> models) {
		for (ProcessModel model1 : models) {
			for (ProcessModel model2 : models) {
				if (model1 != model2 || !this.repositories.containsKey(model1) || !this.repositories.get(model1).containsKey(model2)) {
					List<ProcessModel> nodes = new ArrayList<ProcessModel>();
					nodes.add(model1);
					nodes.add(model2);
					DocumentFrequencyCollectionRepository coll = new DocumentFrequencyCollectionRepository(nodes);
					this.addDocumentFrequencyCollectionRepository(model1, model2, coll);
					this.addDocumentFrequencyCollectionRepository(model2, model1, coll);
				}
			}
		}
	}
	
	private void addDocumentFrequencyCollectionRepository(ProcessModel model1, ProcessModel model2, DocumentFrequencyCollectionRepository coll) {
		if (!this.repositories.containsKey(model1)) {
			HashMap<String, DocumentFrequencyCollectionRepository> repo = new HashMap<String, DocumentFrequencyCollectionRepository>();
			repo.put(model2.getId(), coll);
			this.repositories.put(model1.getId(), repo);					
		}
		else {
			this.repositories.get(model1.getId()).put(model2.getId(), coll);
		}
	}
	
	public double getDocumentFrequency(ProcessModel model1, ProcessModel model2, String term) {
		return this.repositories.containsKey(model1.getId()) ? 
				(this.repositories.get(model1.getId()).containsKey(model2.getId()) ?
						this.repositories.get(model1.getId()).get(model2.getId()).getDocumentFrequency(term) :
						0) :
				0;
	}

}
