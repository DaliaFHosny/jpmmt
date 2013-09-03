package de.lsem.process.matching.labelsimilarity;

import java.util.Collection;

import de.lsem.matrix.ObjectComparer;
import de.lsem.process.model.ProcessModel;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class DocumentFrequencyCollectionPruningComparer extends MaximumPruningComparer {
	private DocumentFrequencyCollectionRepository repository;
	
	public DocumentFrequencyCollectionPruningComparer(ObjectComparer<String> wordComparer, Collection<ProcessModel> models) {
		super(wordComparer);
		this.repository = new DocumentFrequencyCollectionRepository(models);	
	}

	@Override
	public int compare(String s1, String s2) {
		double df1 = this.repository.getDocumentFrequency(s1);
		double df2 = this.repository.getDocumentFrequency(s2);
		return df1 > df2 ? -1 : (df1 == df2 ? super.compare(s1, s2) : 1);
	}

}
