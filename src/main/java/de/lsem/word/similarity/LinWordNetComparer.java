package de.lsem.word.similarity;

import de.lsem.matrix.ObjectComparer;
import edu.cmu.lti.ws4j.WS4J;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
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
public class LinWordNetComparer implements ObjectComparer<String> {

	@Override
	public double compare(String word1, String word2) {
		return Math.abs(WS4J.calcSimilarityByLin(word1, word2));
	}

}
