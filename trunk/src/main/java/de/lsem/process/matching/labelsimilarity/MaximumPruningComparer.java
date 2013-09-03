package de.lsem.process.matching.labelsimilarity;

import de.lsem.matrix.ObjectComparer;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class MaximumPruningComparer extends PruningComparer  {
	public MaximumPruningComparer(ObjectComparer<String> wordComparer) {
		super(wordComparer);
	}
	
	public int compare(String s1, String s2) {
		return this.values.get(s1) > this.values.get(s2) ? -1 : (this.values.get(s1) == this.values.get(s2) ? 0 : 1);
	}
}
