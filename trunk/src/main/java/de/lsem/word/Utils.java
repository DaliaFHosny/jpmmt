package de.lsem.word;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import de.lsem.config.Configuration;
import de.lsem.word.similarity.LevenshteinComparer;
import edu.cmu.lti.ws4j.util.PorterStemmer;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

/**
 * This calls represents a collection of methods to manipulate strings.
 * 
 * @author Christopher Klinkmüller
 *
 */
public class Utils {
	private static WordnetStemmer wordnetStemmer;
	private static PorterStemmer porterStemmer;
	private static LevenshteinComparer levenshteinComparer;
	
	static {
		try {
			URL url = new URL("file", null, Configuration.INSTANCE.getWordNetFolder());
			IDictionary dic = new Dictionary(url);
			dic.open();
			wordnetStemmer = new WordnetStemmer(dic);
		} catch (MalformedURLException e) {
			// TODO: Exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: Exception
			e.printStackTrace();
		}		
		
		porterStemmer = new PorterStemmer();
	}
	
	/**
	 * This method stems a word using the stemming algorithm by Porter.
	 * 
	 * @param word 	a word
	 * @return 		the stem of the word
	 */
	public static String stemPorter(String word) {
		return porterStemmer.stemWord(word);
	}
	
	/**
	 * This method returns all stems for a word that can be found in WordNet and belong to the part-of-speech category 'noun'.
	 * @param word	a word
	 * @return 		the stems of the word
	 */
	public static List<String> stemWordNetNoun(String word) {
		return wordnetStemmer.findStems(word, POS.NOUN);
	}
	
	/**
	 * This method returns all stems for a word that can be found in WordNet and belong to the part-of-speech category 'verb'.
	 * @param word	a word
	 * @return 		the stems of the word 
	 */
	public static List<String> stemWordNetVerb(String word) {
		return wordnetStemmer.findStems(word, POS.VERB);
	}
	
	/**
	 * This method returns all stems for a word that can be found in WordNet and belong to the part-of-speech category 'adjective'.
	 * @param word	a word
	 * @return 		the stems of the word
	 */
	public static List<String> stemWordNetAdjective(String word) {
		return wordnetStemmer.findStems(word, POS.ADJECTIVE);
	}
	
	/**
	 * This method returns all stems for a word that can be found in WordNet and belong to the part-of-speech category 'adverb'.
	 * @param word	a word
	 * @return 		the stems of the word
	 */
	public static List<String> stemWordNetAdverb(String word) {
		return wordnetStemmer.findStems(word, POS.ADVERB);
	}
	
	public static List<String> tokenize(String text) {
		StringTokenizer tokenizer = new StringTokenizer(text);
		List<String> tokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			tokens.add(tokenizer.nextToken().replaceAll("[^A-Za-z_]", "").toLowerCase());
		}				
		return tokens;
	}	
	
	/**
	 * This method selects all words which are not a stop words from a given list of words. The list of stop words can be configured in the config.xml.
	 * @param words 	a list of words
	 * @return 			a new list containing all non-stop words from the given list
	 */
	public static List<String> removeStopWords(List<String> words) {
		List<String> filteredWords = new ArrayList<String>();
		for (String word : words) {
			if (!Configuration.INSTANCE.getStopWords().contains(word)) {
				filteredWords.add(word);
			}
		}
				
		return filteredWords;
	}
	
	/**
	 * This method tokenizes a given text using {@code java.util.StringTokenizer}. It also removes all stop words contained in the text. The list of stop words can be configured in the config.xml.
	 * @param text	a text
	 * @return 		a list of all tokens in the text which are no stop words
	 */
	public static List<String> tokenizeAndRemoveStopWords(String text) {
		StringTokenizer tokenizer = new StringTokenizer(text);
		List<String> tokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().replaceAll("[^A-Za-z_]", "").toLowerCase();
			if (!Configuration.INSTANCE.getStopWords().contains(token)) {
				tokens.add(token);
			}
		}				
		return tokens;
	}
	
	/**
	 * This method checks whether to words are equal or not. Two words are equal if their Levenshtein-Distance is not bigger than 0.15.
	 * @param word1		the first word to compare
	 * @param word2		the second word to compare
	 * @return			true, if the words are equal. false, otherwise.
	 */
	public static boolean areWordsEqual(String word1, String word2) {
		String stem1 = stemPorter(word1);
		String stem2 = stemPorter(word2);
		if (levenshteinComparer.compare(stem1, stem2) >= 0.85) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * This method takes a list of words and groups them. Grouping is done by checking whether two words are equal are not using {@link #areWordsEqual(String, String) areWordsEqual}.  
	 * @param words		the list of words
	 * @return 			a list of sets of words, where each set contains equal words 
	 */
	public static List<Set<String>> groupTerms(Collection<String> words) {
		List<Set<String>> termGroups = new ArrayList<Set<String>>(); 
		
		for (String term : words) {
			List<Set<String>> groups = new ArrayList<Set<String>>();
			for (Set<String> termGroup : termGroups) {
				for (String t : termGroup) {
					if (areWordsEqual(term, t)) {
						groups.add(termGroup);
						break;
					}
				}
			}
			
			if (groups.size() == 0) {
				Set<String> set = new HashSet<String>();
				set.add(term);
				termGroups.add(set);
			}
			else if (groups.size() == 1) {
				groups.get(0).add(term);
			}
			else {
				groups.get(0).add(term);
				for (int a = 1; a < groups.size(); a++) {
					groups.get(0).addAll(groups.get(a));
					termGroups.remove(groups.get(a));
				}
			}
		}
		
		return termGroups;
	}
}
