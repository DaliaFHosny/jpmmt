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

import de.lsem.process.io.epml.EventDrivenProcessChain.Node;
import de.lsem.process.io.epml.EventDrivenProcessChain.NodeType;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

class EpmlImporter {
	private DocumentBuilder documentBuilder;
	private static HashMap<EventDrivenProcessChain.NodeType, String> types;
	
	static {
		types = new HashMap<EventDrivenProcessChain.NodeType, String>();
		types.put(NodeType.EVENT, "event");
		types.put(NodeType.FUNCTION, "function");
		types.put(NodeType.OPERATOR_AND, "and");
		types.put(NodeType.OPERATOR_OR, "or");
		types.put(NodeType.OPERATOR_XOR, "xor");
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
		HashMap<String, EventDrivenProcessChain.Node> epcNodes = new HashMap<String, EventDrivenProcessChain.Node>();
		
		EventDrivenProcessChain epc = this.readEpc(root, filename);
		this.readNodes(EventDrivenProcessChain.NodeType.EVENT, root, epc, epcNodes);
		this.readNodes(EventDrivenProcessChain.NodeType.FUNCTION, root, epc, epcNodes);
		this.readNodes(EventDrivenProcessChain.NodeType.OPERATOR_AND, root, epc, epcNodes);
		this.readNodes(EventDrivenProcessChain.NodeType.OPERATOR_OR, root, epc, epcNodes);
		this.readNodes(EventDrivenProcessChain.NodeType.OPERATOR_XOR, root, epc, epcNodes);
		this.readArcs(root, epc, epcNodes);
		
		return epc;
	}
	
	private void readArcs(Element root, EventDrivenProcessChain epc, HashMap<String, EventDrivenProcessChain.Node> epcNodes) {
		NodeList list = root.getElementsByTagName("arc");
		for (int a = 0; a < list.getLength(); a++) {
			Element arcElement = (Element)list.item(a);
			String id = arcElement.getAttribute("id");
			Element flowElement = (Element)arcElement.getElementsByTagName("flow").item(0);
			EventDrivenProcessChain.Arc arc = new EventDrivenProcessChain.Arc(id, "", 
					epcNodes.get(flowElement.getAttribute("source")), 
					epcNodes.get(flowElement.getAttribute("target")));
			epc.addArc(arc);
		}
	}

	private void readNodes(NodeType type, Element root, EventDrivenProcessChain epc, HashMap<String, Node> epcNodes) {
		NodeList list = root.getElementsByTagName(types.get(type));
		for (int a = 0; a < list.getLength(); a++) {
			Element element = (Element)list.item(a);
			String id = element.getAttribute("id");
			String label = "";
			
			Element labelElement = (Element)element.getElementsByTagName("name").item(0);
			if (labelElement != null) {
				label = labelElement.getTextContent();
//				label = label.replace('\n', ' ').replace("\\n", " ");
//				label = label.replace("-", " ");
//				label = label.replace("?", "");
				label = label.replace("\\n", " ").replaceAll("[^A-Za-z0-9_]", " ").toLowerCase();
				System.out.println(label);
			}
			
			EventDrivenProcessChain.Node node = new EventDrivenProcessChain.Node(id, label, type);
			epc.addNode(node);
			epcNodes.put(id, node);
		}
	}

	private EventDrivenProcessChain readEpc(Element root, String filename) {
		int index1 = filename.lastIndexOf('\\');
		int index2 = filename.lastIndexOf('/');
		int begin = (index1 > index2 ? index1 : index2) + 1;
		String name = filename.substring(begin, begin + (filename.length() - begin));
		
		EventDrivenProcessChain chain = new EventDrivenProcessChain(name);		
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
