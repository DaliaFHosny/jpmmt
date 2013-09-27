package de.lsem.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.io.Resources;

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
public final class Configuration {
	private List<String> stopWords;
	private String wordNetFolder;
	private static String configurationFile = Resources.getResource("config.xml").getFile();	
	
	public static final Configuration INSTANCE = new Configuration();	
	
	private Configuration() {
		this.stopWords = new ArrayList<String>();
		try {
			File configFile = new File(configurationFile);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(configFile);
			document.getDocumentElement().normalize();
			
			NodeList stopWordNodes = document.getElementsByTagName("StopWord");
			for (int a = 0; a < stopWordNodes.getLength(); a++) {
				Element stopWord = (Element) stopWordNodes.item(a);
				this.stopWords.add(stopWord.getTextContent());
			}
			
			NodeList wordNetNode = document.getElementsByTagName("WordNet");
			this.wordNetFolder = ((Element)wordNetNode.item(0)).getAttribute("dictionaryFolder");			
	    } catch (Exception e) {
	    	e.printStackTrace();
		}
	}
	
	public Collection<String> getStopWords() {
		return this.stopWords;
	}
	
	public String getWordNetFolder() {
		return this.wordNetFolder;
	}
}
