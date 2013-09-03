package de.lsem.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class MassPreRecFCalculator {
	private double meanPrecision = Double.NaN;
	private double meanRecall = Double.NaN;
	private double meanFOneMeasure = Double.NaN;
	private double stdRecall = Double.NaN;
	private double stdPrecision = Double.NaN;
	private double stdFOneMeasure = Double.NaN;
	private PreRecFCalculator calculator = new PreRecFCalculator();
	private List<Double> precisionValues = new ArrayList<Double>();
	private List<Double> recallValues = new ArrayList<Double>();
	private List<Double> fOneValues = new ArrayList<Double>();
	
	public void calculate(List<Set<IDocument>> baselines, List<Set<IDocument>> documentsSet) {
		this.reset();
		
		if (baselines.size() != documentsSet.size()) {
			throw new IllegalArgumentException("The size of the baselines (" + baselines.size() + 
					") is different from the size of the set of the documents (" + documentsSet.size() + ")");
		}
		
		this.determineValues(baselines, documentsSet);
		this.calculateStatistics();
	}

	private void reset() {
		this.precisionValues.clear();
		this.recallValues.clear();
		this.fOneValues.clear();
		this.meanFOneMeasure = 0;
		this.meanPrecision = 0;
		this.meanRecall = 0;
	}

	private void determineValues(List<Set<IDocument>> baselines, List<Set<IDocument>> documentsSet) {
		for (int a = 0; a < baselines.size(); a++) {
			this.calculator.calculate(baselines.get(a), documentsSet.get(a));
			this.precisionValues.add(this.calculator.getPrecision());
			this.meanPrecision += this.calculator.getPrecision();
			this.recallValues.add(this.calculator.getRecall());
			this.meanRecall += this.calculator.getRecall();
			this.fOneValues.add(this.calculator.getFOneMeasure());
			this.meanFOneMeasure += this.calculator.getFOneMeasure();
		}	
	}

	private void calculateStatistics() {
		this.calculateMeans();
		this.stdFOneMeasure = this.calculateStandardDeviation(this.meanFOneMeasure, this.fOneValues);
		this.stdRecall = this.calculateStandardDeviation(this.meanRecall, this.recallValues);
		this.stdPrecision = this.calculateStandardDeviation(this.meanPrecision, this.precisionValues);
	}

	private void calculateMeans() {
		this.meanFOneMeasure /= this.fOneValues.size();
		this.meanPrecision /= this.precisionValues.size();
		this.meanRecall /= this.recallValues.size();
	}

	private double calculateStandardDeviation(double mean, List<Double> values) {
		double std = 0;
		
		for (Double value : values) {
			std += Math.pow(mean - value, 2);
		}
		
		return Math.sqrt(std / values.size());
	}

	public double getMeanPrecision() {
		return this.meanPrecision;
	}

	public double getMeanRecall() {
		return this.meanRecall;
	}

	public double getMeanFOneMeasure() {
		return this.meanFOneMeasure;
	}

	public double getStandardDeviationRecall() {
		return this.stdRecall;
	}

	public double getStandardDeviationPrecision() {
		return this.stdPrecision;
	}

	public double getStandardDeviationFOneMeasure() {
		return this.stdFOneMeasure;
	}	
}
