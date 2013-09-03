package de.lsem.word;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.lsem.matrix.MaximumObjectComparer;
import de.lsem.matrix.ObjectComparer;
import de.lsem.matrix.StoreObjectComparer;
import de.lsem.word.similarity.LevenshteinComparer;
import de.lsem.word.similarity.LinWordNetComparer;
import de.lsem.word.similarity.WordNetStemComparer;

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
public class SimilarityTest {
	private String[][] wordpairs;  	
	
	private LevenshteinComparer levenshteinComparer;
	private WordNetStemComparer levenshteinStemComparer;
	private LinWordNetComparer linComparer;
	private WordNetStemComparer linStemComparer;
	private MaximumObjectComparer<String> maximumComparer;
	private WordNetStemComparer maximumStemComparer;
	private StoreObjectComparer<String> storeLevenshteinComparer;
	
	@Before
	public void setup() {
		this.levenshteinComparer = new LevenshteinComparer();
		this.levenshteinStemComparer = new WordNetStemComparer(this.levenshteinComparer);
		this.linComparer = new LinWordNetComparer();
		this.linStemComparer = new WordNetStemComparer(this.linComparer);
		this.maximumComparer = new MaximumObjectComparer<String>(this.levenshteinComparer, this.linComparer);
		this.maximumStemComparer = new WordNetStemComparer(this.maximumComparer);
		
		this.storeLevenshteinComparer = new StoreObjectComparer<String>(this.levenshteinComparer);
		
		this.wordpairs = new String[][]{{"document", "certificate"}, {"run", "go"}, {"walk", "talk"}, {"university", "universities"}};
	}
	
	@Test
	public void testLevenshteinComparer() {
		this.testObjectComparer(this.levenshteinComparer);
	}
	
	@Test
	public void testLevenshteinStemComparer() {
		this.testObjectComparer(this.levenshteinStemComparer);
	}
	
	@Test
	public void testLinWordNetComparer() {
		this.testObjectComparer(this.linComparer);
	}
	
	@Test
	public void testLinWordNetStemComparer() {
		this.testObjectComparer(this.linStemComparer);
	}
	
	@Test
	public void testMaximumComparer() {
		this.testObjectComparer(this.maximumComparer);
	}
	
	@Test
	public void testMaximumStemComparer() {
		this.testObjectComparer(this.maximumStemComparer);
	}
	
	@Test
	public void testStoreObjectComparer() {
		for (int a = 0; a <= 5; a++) {
			this.testObjectComparer(this.storeLevenshteinComparer);
		}
	}
	
	private void testObjectComparer(ObjectComparer<String> comparer) {
		for (int a = 0; a < wordpairs.length; a++) {
			double value = this.levenshteinComparer.compare(wordpairs[a][0], wordpairs[a][1]);
			boolean test =  value >= 0 && value <= 1;
			assertTrue(test);
		}
	}
}
