package de.lsem.word;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import de.lsem.config.Configuration;
import de.lsem.word.similarity.LevenshteinComparer;
import edu.cmu.lti.ws4j.util.PorterStemmer;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
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
	private static IPointer antonym;
	private static IPointer derived;
	private static IPointer adjDerived;
	
	static {
		wordnetStemmer = new WordnetStemmer(Configuration.INSTANCE.getWordNetDictionary());		
		porterStemmer = new PorterStemmer();
		levenshteinComparer = new LevenshteinComparer();
		antonym = Pointer.ANTONYM;
		derived = Pointer.DERIVATIONALLY_RELATED;
		adjDerived = Pointer.DERIVED_FROM_ADJ;
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
	
	public static List<String> stemWordNet(String word) {
		List<String> stems = new ArrayList<String>();
		stems.addAll(stemWordNetAdjective(word));
		stems.addAll(stemWordNetAdverb(word));
		stems.addAll(stemWordNetNoun(word));
		stems.addAll(stemWordNetVerb(word));
		return stems;
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
			tokens.add(tokenizer.nextToken().toLowerCase());
		}				
		return tokens;
	}	
	
	public static List<String> tokenizeAndFilter(String text) {
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
			if (!token.equals("") && !Configuration.INSTANCE.getStopWords().contains(token)) {
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
		double s = levenshteinComparer.compare(stem1, stem2);
		if (s >= 0.5) {
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
	
	public static boolean areWordsRelated(String word1, String word2) {
		if (word1.equals(word2)) {
			return true;
		}
		else {
			Collection<String> words1 = getRelatedWords(word1);
			Collection<String> words2 = getRelatedWords(word2);
			return contains(words1, words2) || contains(words2, words1);
		}
	}
	
	public static Collection<String> getRelatedWords(String str) {
		List<IWordID> words = getIndexWords(str);
		Collection<String> w = getRelated(words, derived, true);
		w.addAll(getRelated(words, adjDerived, true));
		return w;
	}
	
	private static Collection<String> getRelated(List<IWordID> words, IPointer pointer, boolean first) {
		Set<String> related = new HashSet<String>();
		for (IWordID indexWord : words) {
			IWord word = Configuration.INSTANCE.getWordNetDictionary().getWord(indexWord);
			related.add(word.getLemma());
			List<IWordID> relatedIds = word.getRelatedMap().get(pointer);
			if (relatedIds != null) {
				for (IWordID r : relatedIds) {
					IWord rel = Configuration.INSTANCE.getWordNetDictionary().getWord(r);
					related.add(rel.getLemma());
				}
				if (first) {
					related.addAll(getRelated(relatedIds, pointer, false));
				}
			}			
		}				
		return related;
	}
	
	private static List<IWordID> getIndexWords(String word) {
		List<IWordID> words = new ArrayList<IWordID>();
		addIndexWord(word, words, POS.VERB);
		addIndexWord(word, words, POS.NOUN);
		addIndexWord(word, words, POS.ADVERB);
		addIndexWord(word, words, POS.ADJECTIVE);
		return words;
	}
	
	private static void addIndexWord(String word, List<IWordID> words, POS pos) {
		IIndexWord indexWord = Configuration.INSTANCE.getWordNetDictionary().getIndexWord(word, pos);
		if (indexWord != null && indexWord.getWordIDs().size() > 0) {
			words.addAll(indexWord.getWordIDs());
		}
	}
	
	public static boolean areAntonyms(String str1, String str2) {
		List<IWordID> words1 = getIndexWords(str1);
		List<IWordID> words2 = getIndexWords(str2);
		Collection<String> antonyms1 = getRelated(words1, antonym, true);
		Collection<String> antonyms2 = getRelated(words2, antonym, true);
		Collection<String> derived1 = getRelated(words1, derived, true);
		Collection<String> derived2 = getRelated(words2, derived, true);
		Collection<String> adjDerived1 = getRelated(words1, adjDerived, true);
		Collection<String> adjDerived2 = getRelated(words2, adjDerived, true);
		
		return contains(antonyms1, derived2) || contains(antonyms2, derived1) || 
			   contains(antonyms1, adjDerived2) || contains(antonyms2, adjDerived1);
	}	

	private static boolean contains(Collection<String> antonyms, Collection<String> derived) {
		for (String a : antonyms) {
			if (derived.contains(a)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isActivityLabel(String label) {
		if (label == null || label.equals("")) {
			return false;
		}
		
		if (label.startsWith("t") || label.startsWith("p")) {
			CharSequence seq = label.subSequence(1, label.length());
			boolean isNotDigit = true;
			for (int a = 0; a < seq.length(); a++) {
				char letter = seq.charAt(a);
				if (!(letter >= '0' && letter <= '9')) {
					isNotDigit = false;
					break;
				}
			}
			if (isNotDigit) {
				return false;
			}
		}
		
		return true;
	}
}
