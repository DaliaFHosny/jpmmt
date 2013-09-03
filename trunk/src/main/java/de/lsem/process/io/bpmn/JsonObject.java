package de.lsem.process.io.bpmn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

class JsonObject {
	String type;
	String id;
	String name;
	int width;
	int height;
	int x;
	int y;
	List<JsonObject> containedObjects;
	List<String> outgoingConnections;
	
	public JsonObject() {
		this.containedObjects = new ArrayList<JsonObject>();
		this.outgoingConnections = new ArrayList<String>();
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public JsonObject getContainedObject(int i) {
		return this.containedObjects.get(i);
	}
	
	public void addContainedObject(JsonObject obj) {
		this.containedObjects.add(obj);
	}
	
	public void removeContainedObject(JsonObject obj) {
		this.containedObjects.remove(obj);
	}
	
	public Iterator<JsonObject> getContainedObjectsIterator() {
		return this.containedObjects.iterator();
	}
	
	public int getContainedObjectsSize() {
		return this.containedObjects.size();
	}
	
	public String getOutgoingConnection(int i) {
		return this.outgoingConnections.get(i);
	}
	
	public void addOutgoingConnection(String con) {
		this.outgoingConnections.add(con);
	}
	
	public void removeOutgoingConnection(String con) {
		this.outgoingConnections.remove(con);
	}
	
	public Iterator<String> getOutgoingConnectionsIterator() {
		return this.outgoingConnections.iterator();
	}
	
	public int getOutgoingConnectionsSize() {
		return this.outgoingConnections.size();
	}
}
