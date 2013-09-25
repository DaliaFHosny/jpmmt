package de.lsem.word;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class UtilsTest {
	private List<String> words;	
	//private String text;
	
	@Before
	public void setup() {
		//this.text = "A business process or business method is a collection of related, structured activities.";
		
		this.words = new ArrayList<String>();
		this.words.add("A");
	}
	
	@Test
	public void testWordEquality() {
		assertTrue(Utils.areWordsEqual("document", "documents"));
		assertTrue(Utils.areWordsEqual("able", "abel"));
		assertFalse(Utils.areWordsEqual("do", "documents"));
		assertFalse(Utils.areWordsEqual("herold", "thomas"));
	}
	
	@Test
	public void testGroupTerms() {
		Utils.groupTerms(words);
	}
}
