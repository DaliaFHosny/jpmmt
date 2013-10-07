package de.lsem.process.io.pnml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.lsem.process.model.GraphicalInformation;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

class YasperImporter {
	private DocumentBuilder documentBuilder;
	
	public YasperImporter() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			this.documentBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO: hier Exception werfen
			e.printStackTrace();			
		}
	}
	
	public PetriNet importFile(String filename) {
		Element rootElement = this.readDocument(filename);		
		
		PetriNet net = this.readNet(rootElement, filename);
		
		HashMap<String, PetriNetNode> nodes = new HashMap<String, PetriNetNode>();
		this.readNodes(net, rootElement.getElementsByTagName("place"), true, nodes);
		this.readNodes(net, rootElement.getElementsByTagName("transition"), false, nodes);
		this.readArcs(net, rootElement.getElementsByTagName("arc"), nodes);
		
		return net;
	}
	
	private void readArcs(PetriNet net, NodeList elements,	HashMap<String, PetriNetNode> nodes) {
		for (int a = 0; a < elements.getLength(); a++) {
			Element element = (Element)elements.item(a);
			String id = element.getAttribute("id");
			String source = element.getAttribute("source");
			String target = element.getAttribute("target");
			net.addPetriNetArc(id, "", nodes.get(source), nodes.get(target));
		}
	}

	private void readNodes(PetriNet net, NodeList elements, boolean isPlace, HashMap<String, PetriNetNode> nodes) {
		for (int a = 0; a < elements.getLength(); a++) {
			Element element = (Element)elements.item(a);
			String id = element.getAttribute("id");
			String name = "";
			
			Element nameElement = (Element)element.getElementsByTagName("name").item(0);
			if (nameElement != null) {
				Element textElement = (Element)nameElement.getElementsByTagName("text").item(0);
				if (textElement != null) {
					name = textElement.getTextContent().toLowerCase();
				}
			}
			
			PetriNetNode node = null;
			if (isPlace) {
				node =  net.addPlace(id, name);
			}
			else {
				node = net.addTransition(id, name);
			}
			nodes.put(id, node);
			
			Element graphicsElement = (Element)element.getElementsByTagName("graphics").item(0);
			if (graphicsElement != null) {
				Element positionElement = (Element)element.getElementsByTagName("position").item(0);
				Element dimensionElement = (Element)element.getElementsByTagName("dimension").item(0);
				GraphicalInformation information = new GraphicalInformation(
						Double.parseDouble(positionElement.getAttribute("x")),
						Double.parseDouble(positionElement.getAttribute("y")),
						Double.parseDouble(dimensionElement.getAttribute("x")),
						Double.parseDouble(dimensionElement.getAttribute("y")));
				node.setGraphicalInformation(information);
			}
		}
	}

	private PetriNet readNet(Element rootElement, String filename) {
		try {
			Element netElement = (Element) rootElement.getElementsByTagName("net").item(0);
			PetriNet net = new PetriNet();
			
			NodeList childNodes = netElement.getChildNodes();
			for (int a = 0; a < childNodes.getLength(); a++) {
				Node node = childNodes.item(a);
				
				if (Element.class.isInstance(node) && node.getNodeName().equals("name")) {
					Element element = (Element)node;
					NodeList texts = element.getElementsByTagName("text");
					if (texts.getLength() == 1) {
						net.setName(texts.item(0).getTextContent());
					}
					else {
						this.setPetriNetName(net, filename);
					}
				}
				else {
					this.setPetriNetName(net, filename);
				}
			}
					
			return net;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			// TODO: hier Exception werfen
		}
		
		return null;
	}

	private void setPetriNetName(PetriNet net, String filename) {
		int i0 = filename.lastIndexOf('\\');
		int i1 = filename.lastIndexOf('/');
		
		if (i0 == -1 && i1 == -1) {
			net.setName(filename.replace(".pnml", ""));
		}
		else if (i0 > i1) {
			net.setName(filename.substring(i0 + 1).replace(".pnml", ""));
		}
		else {
			net.setName(filename.substring(i1 + 1).replace(".pnml", ""));
		}
		net.setId(net.getName());
	}

	private Element readDocument(String filename) {
		File file = new File(filename);
		
		try {
			Document document = this.documentBuilder.parse(file);
			document.getDocumentElement().normalize();
			return document.getDocumentElement();
		} catch (SAXException | IOException e) {
			// TODO: hier Exception werfen
			e.printStackTrace();
		}
		
		return null;
	}
}
