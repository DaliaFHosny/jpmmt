package de.lsem.evaluation.onetoone;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.lsem.evaluation.EvaluationMeasures;
import de.lsem.process.matching.ProcessMapping;
import de.lsem.process.model.ProcessModel;


/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

/**
 * 
 * @author Christopher Klinkm�ller
 *
 */
public class OneToOneMatchEvaluation {
	public static final OneToOneMatchEvaluation INSTANCE;
	
	private OneToOneMatchEvaluation() {
		this.reader = new ModelAlignmentReader();
		this.check = new ModelAlignmentCheck();
	}
	
	static {
		INSTANCE = new OneToOneMatchEvaluation();
	}

	private ModelAlignmentReader reader;
	private ModelAlignmentCheck check;
	
	public List<String> checkStandard(Collection<ProcessModel> models, String standardFolder) {
		Map<String, Map<String, ModelAlignment>> standard = this.reader.bulkRead(standardFolder);		
		return this.check.checkEvaluationMapping(models, standard);
	}
	
	public List<String> checkStandard(Collection<ProcessModel> models, Collection<String> standardFiles) {
		Map<String, Map<String, ModelAlignment>> standard = this.reader.bulkRead(standardFiles);		
		return this.check.checkEvaluationMapping(models, standard);
	}
	
	public EvaluationMeasures evaluate(Collection<ProcessMapping> mappings, String standardFolder) {
		Map<String, Map<String, ModelAlignment>> standard = this.reader.bulkRead(standardFolder);
		ModelAlignmentEvaluation evaluation = new ModelAlignmentEvaluation(standard);
		return evaluation.evaluate(mappings);
	}
	
	public EvaluationMeasures evaluate(Collection<ProcessMapping> mappings, Collection<String> standardFiles) {
		Map<String, Map<String, ModelAlignment>> standard = this.reader.bulkRead(standardFiles);
		ModelAlignmentEvaluation evaluation = new ModelAlignmentEvaluation(standard);
		return evaluation.evaluate(mappings);
	}

	public void setAlignmentSeperator(String seperator) {
		this.reader.setSeperator(seperator);
	}
	
	public String getAlignmentSeperator() {
		return this.reader.getSeperator();
	}
}
