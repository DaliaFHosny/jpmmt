package de.lsem.process.io.bpmn;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.lsem.process.io.ProcessModelReader;
import de.lsem.process.model.ProcessModel;
import de.lsem.process.model.ProcessNode;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class JsonBpmnReader extends ProcessModelReader {
	private static Set<String> flowTypes;
	private ObjectMapper mapper;
	private Map<String, ProcessNode> activities;
	private Map<String, JsonObject> objects;
	
	static {
		flowTypes = new HashSet<String>();
		flowTypes.add("Association_Bidirectional");
		flowTypes.add("Association_Undirected");
		flowTypes.add("Association_Unidirectional");
		flowTypes.add("Association_Undirected");
		flowTypes.add("MessageFlow");
		flowTypes.add("SequenceFlow");
	}
	
	public JsonBpmnReader() {
		this.mapper = new ObjectMapper();
		this.activities = new HashMap<String, ProcessNode>();
		this.objects = new HashMap<String, JsonObject>();
	}

	@Override
	protected ProcessModel readModel(String filename) {
		try {
			JsonObject object = this.readFile(filename);
			return this.convert(object);
		}
		catch (Exception ex) {
			ex.printStackTrace(); 
		}
		return null;
	}
	
	public ProcessModel convert(JsonObject obj) {
		ProcessModel model = new ProcessModel();
		
		this.convertObject(model, obj, null);
		this.addEdges(model, obj);
		
		this.activities.clear();
		this.objects.clear();
		
		return model;
	}

	private void addEdges(ProcessModel model, JsonObject obj) {
		if (this.isProcessNode(obj)) {
			ProcessNode source = this.activities.get(obj.getId());
			Iterator<String> it = obj.getOutgoingConnectionsIterator();
			while (it.hasNext()) {
				String id = it.next();
				JsonObject targetObject = this.objects.get(id);
				
				if (this.isProcessNode(targetObject)) {
					ProcessNode target = this.activities.get(targetObject.getId());
					model.addEdge(Long.toString(System.currentTimeMillis()), "", source, target);					
				}
				else {
					Iterator<String> targetIt = targetObject.getOutgoingConnectionsIterator();
					while (targetIt.hasNext()) {
						String tid = targetIt.next();
						ProcessNode target = this.activities.get(tid);
						model.addEdge(Long.toString(System.currentTimeMillis()), "", source, target);
					}
				}
			}
			
			Iterator<JsonObject> iter = obj.getContainedObjectsIterator();
			while (iter.hasNext()) {
				this.addEdges(model, iter.next());
			}
		}		
	}

	private void convertObject(ProcessModel model, JsonObject obj, ProcessNode parent) {
		this.objects.put(obj.getId(), obj);
		
		if (this.isProcessNode(obj)) {
			String type = this.getType(obj.getType());
			ProcessNode node = model.addNode(obj.getId(), obj.getName(), type);
			this.activities.put(node.getId(), node);
			
			if (parent != null) {
				model.addEdge(Long.toString(System.currentTimeMillis()), "", parent, node);
			}
			
			Iterator<JsonObject> iter = obj.getContainedObjectsIterator();
			while (iter.hasNext()) {
				this.convertObject(model, iter.next(), node);
			}
		}
	}

	private String getType(String type) {
		switch (type) {
			case "Task" : return ProcessNode.ACTIVITY; 
			case "CollapsedEventSubprocess" : return ProcessNode.ACTIVITY; 
			case "CollapsedSubprocess" : return ProcessNode.ACTIVITY; 
			case "ChoreographyTask" : return ProcessNode.ACTIVITY; 
			case "ChoreographySubprocessExpanded" : return ProcessNode.ACTIVITY; 
			case "Subprocess" : return ProcessNode.ACTIVITY; 
			case "ParallelGateway" : return ProcessNode.PARALLEL_GATEWAY;
			case "AND_Gateway" : return ProcessNode.PARALLEL_GATEWAY;
			case "Exclusive_Databased_Gateway" : return ProcessNode.EXCLUSIVE_GATEWAY;
			case "InclusiveGateway" : return ProcessNode.GATEWAY;
			case "EventbasedGateway" : return ProcessNode.GATEWAY;
			case "ComplexGateway" : return ProcessNode.GATEWAY;
			case "OR_Gateway" : return ProcessNode.GATEWAY;
			case "Complex_Gateway" : return ProcessNode.GATEWAY;
			default : return ProcessNode.UNKNOWN;
		}
	}

	private boolean isProcessNode(JsonObject obj) {
		if (flowTypes.contains(obj.getType())) {
			return false;
		}		
		return true;
	}
	
	private JsonObject readFile(String path) throws Exception {
		File file = new File(path);
		JsonNode node = null;
		
		try {
			node = this.mapper.readValue(file, JsonNode.class);
		}
		catch (Exception ex) {
			throw new Exception("File could not be parsed as JSON.");
		}
		
		return this.parseNode(node);
	}

	private JsonObject parseNode(JsonNode node) {
		JsonObject obj = new JsonObject();
		obj.setId(node.path("resourceId").toString().replace("\"", ""));
		String type = node.path("stencil").path("id").toString().replace("\"", "");
		obj.setType(type);
		obj.setName(node.path("properties").path("name").toString().replace("\"", ""));
				
		int ulx = (int)Double.parseDouble(node.path("bounds").path("upperLeft").path("x").toString());
		int uly = (int)Double.parseDouble(node.path("bounds").path("upperLeft").path("x").toString());
		int lrx = (int)Double.parseDouble(node.path("bounds").path("lowerRight").path("x").toString());
		int lry = (int)Double.parseDouble(node.path("bounds").path("lowerRight").path("x").toString());
		
		obj.setWidth(Math.abs(ulx - lrx));
		obj.setHeight(Math.abs(uly - lry));
		obj.setX(ulx);
		obj.setY(uly);
		
		if (flowTypes.contains(type)) {
			String target = node.path("target").path("resourceId").toString().replace("\"", "");
			obj.addOutgoingConnection(target);
		}
		else {
			for (JsonNode outgoing : node.path("outgoing")) {
				String out = outgoing.path("resourceId").toString().replace("\"", "");
				obj.addOutgoingConnection(out);
			}
		}
		
		for (JsonNode child : node.path("childShapes")) {
			obj.addContainedObject(this.parseNode(child));
		}
		
		return obj;
	}
}
