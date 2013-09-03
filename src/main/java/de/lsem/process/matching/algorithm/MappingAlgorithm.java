package de.lsem.process.matching.algorithm;

import de.lsem.process.matching.ProcessMapping;
import de.lsem.process.model.ProcessModel;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public abstract class MappingAlgorithm {
	public abstract ProcessMapping map(ProcessModel model1, ProcessModel model2);
}
