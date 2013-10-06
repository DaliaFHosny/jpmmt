package de.lsem.process.io.epml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

class EpmlImporter {
	private DocumentBuilder documentBuilder;
	private static HashMap<String, String> types;
	
	static {
		types = new HashMap<String, String>();
		types.put(EventDrivenProcessChainNode.EVENT, "event");
		types.put(EventDrivenProcessChainNode.FUNCTION, "function");
		types.put(EventDrivenProcessChainNode.AND_CONNECTOR, "and");
		types.put(EventDrivenProcessChainNode.OR_CONNECTOR, "or");
		types.put(EventDrivenProcessChainNode.XOR_CONNECTOR, "xor");
	}
	
	public EpmlImporter() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			this.documentBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO: hier Exception werfen
			e.printStackTrace();			
		}
	}

	public EventDrivenProcessChain importEpc (String filename) {
		Element root = this.readDocument(filename);
		HashMap<String, EventDrivenProcessChainNode> epcNodes = new HashMap<String, EventDrivenProcessChainNode>();
		
		EventDrivenProcessChain epc = this.readEpc(root, filename);
		for (String type : types.keySet()) {
			this.readNodes(type, root, epc, epcNodes);
		}
		this.readArcs(root, epc, epcNodes);
		
		return epc;
	}
	
	private void readArcs(Element root, EventDrivenProcessChain epc, HashMap<String, EventDrivenProcessChainNode> epcNodes) {
		NodeList list = root.getElementsByTagName("arc");
		for (int a = 0; a < list.getLength(); a++) {
			Element arcElement = (Element)list.item(a);
			String id = arcElement.getAttribute("id");
			Element flowElement = (Element)arcElement.getElementsByTagName("flow").item(0);
			epc.addEventDrivenProcessChainArc(id, "", 
					epcNodes.get(flowElement.getAttribute("source")), 
					epcNodes.get(flowElement.getAttribute("target")));
		}
	}

	private void readNodes(String type, Element root, EventDrivenProcessChain epc, HashMap<String, EventDrivenProcessChainNode> epcNodes) {
		NodeList list = root.getElementsByTagName(types.get(type));
		for (int a = 0; a < list.getLength(); a++) {
			Element element = (Element)list.item(a);
			String id = element.getAttribute("id");
			String label = "";
			
			Element labelElement = (Element)element.getElementsByTagName("name").item(0);
			if (labelElement != null) {
				label = labelElement.getTextContent().toLowerCase();
			}
			
			EventDrivenProcessChainNode node = epc.addEventDrivenProcessChainNode(id, label, type);
			epcNodes.put(id, node);
			
			Element graphicsElement = (Element)element.getElementsByTagName("graphics").item(0);
			if (graphicsElement != null) {
				Element positionElement = (Element)graphicsElement.getElementsByTagName("position").item(0);
				double x = Double.parseDouble(positionElement.getAttribute("x"));
				double y = Double.parseDouble(positionElement.getAttribute("y"));
				double width = Double.parseDouble(positionElement.getAttribute("width"));
				double height = Double.parseDouble(positionElement.getAttribute("height"));
				node.setGraphicalInformation(new GraphicalInformation(x, y, height, width));
			}
			
		}
	}

	private EventDrivenProcessChain readEpc(Element root, String filename) {
		int index1 = filename.lastIndexOf('\\');
		int index2 = filename.lastIndexOf('/');
		int begin = (index1 > index2 ? index1 : index2) + 1;
		String name = filename.substring(begin, begin + (filename.length() - begin));
		
		EventDrivenProcessChain chain = new EventDrivenProcessChain(name, name);
		return chain;
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
