package de.lsem.process.io.pnml;

import de.lsem.process.model.ProcessModel;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

class PetriNet extends ProcessModel {
	public PetriNet() {
	}
	
	public PetriNet(String id, String name) {
		super(id, name);
	}
	
	public PetriNetNode addPlace(String id, String label) {
		return this.addPetriNetNode(id, label, PetriNetNode.PLACE);
	}
	
	public PetriNetNode addTransition(String id, String label) {
		return this.addPetriNetNode(id, label, PetriNetNode.TRANSITION);
	}
	
	private PetriNetNode addPetriNetNode(String id, String label, String type) {
		PetriNetNode node = new PetriNetNode(this, id, label, type);
		this.addNode(node);
		return node;
	}
	
	public PetriNetArc addPetriNetArc(String id, String name, PetriNetNode source, PetriNetNode target) {
		PetriNetArc arc = new PetriNetArc(this, id, name, source, target);
		this.addEdge(arc);
		return arc;
	}
}
