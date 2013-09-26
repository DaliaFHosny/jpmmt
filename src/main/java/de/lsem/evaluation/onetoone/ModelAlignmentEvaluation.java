package de.lsem.evaluation.onetoone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.lsem.evaluation.EvaluationMeasures;
import de.lsem.process.matching.FragmentMatch;
import de.lsem.process.matching.ProcessMapping;
import de.lsem.process.model.ProcessNode;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

/**
 * 
 * @author Christopher Klinkmüller
 *
 */
class ModelAlignmentEvaluation {
	private Map<String, Map<String, ModelAlignment>> standard;
	
	public ModelAlignmentEvaluation(Map<String, Map<String, ModelAlignment>> standard) {
		this.standard = standard;	
	}
	
	public EvaluationMeasures evaluate(Collection<ProcessMapping> mappings) {
		List<ModelAlignment> alignments = this.transform(mappings);
		EvaluationMeasures metrics = this.evaluateAlignments(alignments);	
		return metrics;
	}

	private EvaluationMeasures evaluateAlignments(Collection<ModelAlignment> alignments) {
		EvaluationMeasures measures = new EvaluationMeasures();
		
		for (ModelAlignment determinedAlignment : alignments) {
			ModelAlignment standardAlignment = this.standard.get(determinedAlignment.getFirstModel()).get(determinedAlignment.getSecondModel());
			this.evaluateAlignment(determinedAlignment, standardAlignment, measures);
		}
		
		measures.calculate();
		return measures;
	}

	private void evaluateAlignment(ModelAlignment determinedAlignment, ModelAlignment standardAlignment, EvaluationMeasures measures) {
		int tp = 0;
		int fp = 0;
		int fn = 0;
		
		for (ActivityMatch deterMatch : determinedAlignment.getActivityMatches()) {
			for (ActivityMatch standMatch : standardAlignment.getActivityMatches()) {
				if (deterMatch.getFirstLabel().equals(standMatch.getFirstLabel()) && standMatch.getSecondLabel().equals(deterMatch.getSecondLabel())) {
					tp += 1;
					break;
				}
			}
		}
		
		fp = determinedAlignment.getActivityMatches().size() - tp;
		fn = standardAlignment.getActivityMatches().size() - tp;	
		measures.add(tp, fp, fn);
	}

	private List<ModelAlignment> transform(Collection<ProcessMapping> mappings) {
		List<ModelAlignment> alignments = new ArrayList<ModelAlignment>();
		
		for (ProcessMapping mapping : mappings) {
			alignments.add(this.transform(mapping));
		}
		
		return alignments;
	}

	private ModelAlignment transform(ProcessMapping mapping) {
		ModelAlignment alignment = new ModelAlignment(mapping.getModel1().getName(), mapping.getModel2().getName());
		
		for (FragmentMatch match : mapping.getFragmentMatch()) {
			for (ProcessNode node1 : match.getFragment1().getProcessNodes()) {
				for (ProcessNode node2 : match.getFragment2().getProcessNodes()) {
					alignment.addActivityMatch(new ActivityMatch(node1.getLabel(), node2.getLabel()));
				}
			}
		}
		
		return alignment;
	}
}
