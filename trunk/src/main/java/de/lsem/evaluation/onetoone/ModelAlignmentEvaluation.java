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

	public EvaluationMeasures evaluate(Collection<ModelAlignment> alignment1, Collection<ModelAlignment> alignment2) {
		List<ModelAlignment> alignments = this.union(alignment1, alignment2);
		EvaluationMeasures metrics = this.evaluateAlignments(alignments);	
		return metrics;
	}
	
	private List<ModelAlignment> union(Collection<ModelAlignment> alignment1, Collection<ModelAlignment> alignment2) {
		List<ModelAlignment> alignments = new ArrayList<ModelAlignment>();		
		
		for (ModelAlignment al1 : alignment1) {
			ModelAlignment al2 = this.getAlignment(alignment2, al1.getFirstModel(), al1.getSecondModel());
			
			if (al2 == null) {
				alignments.add(al1);
			}
			else {
				ModelAlignment al = new ModelAlignment(al1.getFirstModel(), al1.getSecondModel());
				alignments.add(al);
				
				for (ActivityMatch match : al1.getActivityMatches()) {
					ActivityMatch ma = new ActivityMatch(match.getFirstLabel(), match.getSecondLabel());
					al.addActivityMatch(ma);
				}
				
				for (ActivityMatch match : al2.getActivityMatches()) {
					if (!this.containsMatch(al1, match)) {
						ActivityMatch ma = new ActivityMatch(match.getFirstLabel(), match.getSecondLabel());
						al.addActivityMatch(ma);
					}
				}
			}
		}	
		
		return alignments;
	}

	private boolean containsMatch(ModelAlignment al, ActivityMatch match) {
		for (ActivityMatch ma : al.getActivityMatches()) {
			if (ma.getFirstLabel().equals(match.getFirstLabel()) && ma.getSecondLabel().equals(match.getSecondLabel())) {
				return true;
			}
		}
		
		return false;
	}

	private ModelAlignment getAlignment(Collection<ModelAlignment> alignments, String firstModel, String secondModel) {
		for (ModelAlignment al : alignments) {
			if (al.getFirstModel().equals(firstModel) && al.getSecondModel().equals(secondModel)) {
				return al;
			}
		}
		return null;
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

	public static List<ModelAlignment> transform(Collection<ProcessMapping> mappings) {
		List<ModelAlignment> alignments = new ArrayList<ModelAlignment>();
		
		for (ProcessMapping mapping : mappings) {
			alignments.add(transform(mapping));
		}
		
		return alignments;
	}

	public static ModelAlignment transform(ProcessMapping mapping) {
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
