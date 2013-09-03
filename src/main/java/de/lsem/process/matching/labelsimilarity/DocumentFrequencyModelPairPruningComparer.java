package de.lsem.process.matching.labelsimilarity;

import java.util.Collection;
import java.util.HashMap;

import de.lsem.matrix.ObjectComparer;
import de.lsem.process.model.BagOfWords;
import de.lsem.process.model.ProcessModel;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class DocumentFrequencyModelPairPruningComparer extends MaximumPruningComparer {
	private DocumentFrequencyModelPairRepository repository;
	private ProcessModel model1;
	private ProcessModel model2;
	
	public DocumentFrequencyModelPairPruningComparer(ObjectComparer<String> wordComparer, Collection<ProcessModel> models) {
		super(wordComparer);
		this.repository = new DocumentFrequencyModelPairRepository(models);
	}
	
	@Override
	protected double average(BagOfWords bag1, BagOfWords bag2, HashMap<String, Double> values) {
		this.model1 = bag1.getProcessModel();
		this.model2 = bag2.getProcessModel();		
		return super.average(bag1, bag2, values);
	}

	@Override
	public int compare(String s1, String s2) {
		double df1 = this.repository.getDocumentFrequency(this.model1, this.model2, s1);
		double df2 = this.repository.getDocumentFrequency(this.model1, this.model2, s2);
		return df1 > df2 ? -1 : (df1 == df2 ? super.compare(s1, s2) : 1);
	}
	
	

}
