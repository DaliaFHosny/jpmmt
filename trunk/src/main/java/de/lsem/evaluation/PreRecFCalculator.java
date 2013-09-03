package de.lsem.evaluation;

import java.util.Set;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class PreRecFCalculator {
	private double precision = Double.NaN;
	private double recall = Double.NaN;
	private double fOneMeasure = Double.NaN;
	private double tp;
	private double fp;
	private double fn;
	
	public void calculate(Set<IDocument> baseline, Set<IDocument> documents) {
		this.reset();
		this.classify(baseline, documents);	
		this.calculateMeasures();
	}

	private void reset() {
		this.fn = 0;
		this.fp = 0;
		this.tp = 0;
	}

	private void classify(Set<IDocument> baseline, Set<IDocument> documents) {
		for (IDocument document : documents) {
			for (IDocument base : baseline) {
				if (base.isEqual(document)) {
					this.tp += 1;
					break;
				}
			}
		}
		
		this.fp = documents.size() - this.tp;
		this.fn = baseline.size() - this.tp;
	}

	private void calculateMeasures() {
		this.precision = this.tp / (this.tp + this.fp);
		this.recall = this.tp / (this.fn + this.tp);
		this.fOneMeasure = (2 * this.precision * this.recall) / (this.precision + this.recall);
	}

	public double getPrecision() {
		return this.precision;
	}
	
	public double getRecall() {
		return this.recall;
	}
	
	public double getFOneMeasure() {
		return this.fOneMeasure;
	}
}
