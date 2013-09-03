package de.lsem.word.similarity;

import org.apache.commons.lang3.StringUtils;

import de.lsem.matrix.ObjectComparer;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of thea
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */


/**
 *
 * @author Christopher Klinkmüller
 *
 * This class is used to compare Strings based on the levenshtein distance (Levenshtein, 1995).
 * 
 */
public class LevenshteinComparer implements ObjectComparer<String> {

	@Override
	public double compare(String word1, String word2) {
		double distance = StringUtils.getLevenshteinDistance(word1, word2);
		distance /= word1.length() > word2.length() ? word1.length() : word2.length();
		return 1 - distance;
	}

}
