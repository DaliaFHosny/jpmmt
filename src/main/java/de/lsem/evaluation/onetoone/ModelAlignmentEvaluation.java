package de.lsem.evaluation.onetoone;

import java.util.Map;

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
class ModelAlignmentEvaluation {
	private Map<String, Map<String, ModelAlignment>> standard;
	
	public ModelAlignmentEvaluation(Map<String, Map<String, ModelAlignment>> standard) {
		this.standard = standard;	
	}	
}
