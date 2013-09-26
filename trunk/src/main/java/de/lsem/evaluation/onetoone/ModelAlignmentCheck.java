package de.lsem.evaluation.onetoone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.lsem.process.model.ProcessModel;
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
class ModelAlignmentCheck {
	public List<String> checkEvaluationMapping(Collection<ProcessModel> models, Map<String, Map<String, ModelAlignment>> standard) {
		Map<String, Collection<String>> modelActivities = this.getModelActivities(models);
		
		List<String> errors = new ArrayList<String>();
		
		for (String name1 : standard.keySet()) {
			for (String name2 : standard.get(name1).keySet()) {
				ModelAlignment alignment = standard.get(name1).get(name2);
				Collection<String> activities1 = modelActivities.get(name1);
				Collection<String> activities2 = modelActivities.get(name2);
				
				if (activities1 == null || activities2 == null) {
					if (activities1 == null) {
						errors.add(this.getErrorForMissingModel(name1));
					}
					if (activities2 == null) {
						errors.add(this.getErrorForMissingModel(name2));
					}
				}
				else {				
					for (ActivityMatch match : alignment.getActivityMatches()) {
						if (!activities1.contains(match.getFirstLabel())) {
							errors.add(this.getErrorForMissingActivity(name1, name2, match.getFirstLabel(), name1));
						}
						if (!activities2.contains(match.getSecondLabel())) {
							errors.add(this.getErrorForMissingActivity(name1, name2, match.getSecondLabel(), name2));
						}
					}
				}
			}
		}
		
		return errors;
	}

	private String getErrorForMissingModel(String name) {
		return "The model \"" + name + "\" does not exist!";
	}

	private String getErrorForMissingActivity(String modelName1, String modelName2, String label, String model) {
		return "Error in: \"" + modelName1 + "\"" + " vs. \"" + modelName2 + "\": \"" + label + "\" does not exist in " + "\"" + model + "\"";
	}
	
	private Map<String, Collection<String>> getModelActivities(Collection<ProcessModel> models) {
		Map<String, Collection<String>> map = new HashMap<String, Collection<String>>();
		
		for (ProcessModel model : models) {
			HashSet<String> set = new HashSet<String>();
			for (ProcessNode ac : model.getActivities()) {
				set.add(ac.getLabel());
			}			
			map.put(model.getName(), set);
		}
		
		return map;
	}
}
