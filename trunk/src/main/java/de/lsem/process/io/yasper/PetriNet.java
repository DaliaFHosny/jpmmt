package de.lsem.process.io.yasper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

class PetriNet {
	private String name;
	private Set<NetNode> nodes;
	private Set<Arc> arcs;
	private HashMap<NetNode, Set<Arc>> outgoingArcs;
	private HashMap<NetNode, Set<Arc>> incomingArcs;
	
	public PetriNet() {
		this.nodes = new HashSet<NetNode>();
		this.arcs = new HashSet<Arc>();
		this.incomingArcs = new HashMap<NetNode, Set<Arc>>();
		this.outgoingArcs = new HashMap<NetNode, Set<Arc>>();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public NetNode addPlace(String id, String label) {
		return this.addNode(id, label, true);
	}
	
	public NetNode addTransition(String id, String label) { 
		return this.addNode(id, label, false);
	}	
	
	public Set<NetNode> getNodes() {
		return this.nodes;
	}

	private NetNode addNode(String id, String label, boolean isPlace) {
		if (id == null) {
			return null;
			// TODO: hier Exception werfen
		}
		
		NetNode node = new NetNode(id, label, isPlace);
		this.nodes.add(node);
		return node;
	}

	public Arc addArc(String id, NetNode source, NetNode target) {
		if (id == null) {
			return null;
			// TODO: hier Exception werfen
		}
		
		if (source == null || target == null || source.isPlace() == target.isPlace()) {
			return null;
			// TODO: hier Exception werfen
		}
		
		Arc arc = new Arc(id, source, target);
		this.arcs.add(arc);
		
		if (!this.outgoingArcs.containsKey(source)) {
			this.outgoingArcs.put(source, new HashSet<Arc>());
		}
		this.outgoingArcs.get(source).add(arc);
		
		if (!this.incomingArcs.containsKey(target)) {
			this.incomingArcs.put(target, new HashSet<Arc>());
		}
		this.incomingArcs.get(target).add(arc);
		
		return arc;
	}
	
	public Set<Arc> getArcs() {
		return this.arcs;
	}

	public Set<Arc> getIncomingArcs(NetNode node) {
		if (!this.incomingArcs.containsKey(node)) {
			return new HashSet<Arc>();
		}
		return this.incomingArcs.get(node);
	}
	
	public Set<Arc> getOutgoingArcs(NetNode node) {
		if (!this.outgoingArcs.containsKey(node)) {
			return new HashSet<Arc>();
		}
		return this.outgoingArcs.get(node);
	}
	
	abstract class NetElement {
		private String id;
		
		public String getId() {
			return this.id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
	}
	
	class NetNode extends NetElement {
		private String label;
		private boolean isPlace;
		
		public NetNode(String id, String label, boolean isPlace) {
			this.setId(id);
			this.setLabel(label);
			this.isPlace = isPlace;
		}
		
		public boolean isPlace() {
			return this.isPlace;
		}
		
		public boolean isTransition() {
			return !this.isPlace;
		}
		
		public void setPlace() {
			this.isPlace = true;
		}
		
		public void setTransition() {
			this.isPlace = false;
		}
		
		public String getLabel() {
			return this.label;
		}
		
		public void setLabel(String label) {
			this.label = label;
		}
	}
	
	public class Arc extends NetElement {
		private NetNode source;
		private NetNode target;
		
		public Arc(String id, NetNode source, NetNode target) {
			this.setId(id);
			this.source = source;
			this.target = target;
		}
		
		public NetNode getSource() {
			return this.source;
		}
		
		public void setSource(NetNode source) {
			this.source = source;
		}
		
		public NetNode getTarget() {
			return this.target;
		}
		
		public void setTarget(NetNode target) {
			this.target = target;
		}
	}
}
