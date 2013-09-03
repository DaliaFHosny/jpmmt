package de.lsem.process.matching.algorithm.fragmentSummary;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import de.lsem.matrix.Match;
import de.lsem.process.model.BagOfWords;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public abstract class FragmentSummary {
	public abstract boolean summarize(Set<Match<BagOfWords>> matches, HashMap<BagOfWords, BagOfWords> bagMatches, Collection<BagOfWords> bags1, Collection<BagOfWords> bags2);
}
