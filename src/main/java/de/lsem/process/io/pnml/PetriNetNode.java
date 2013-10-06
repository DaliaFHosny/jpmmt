package de.lsem.process.io.pnml;

import de.lsem.process.model.ProcessNode;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

class PetriNetNode extends ProcessNode {
	public static final String PLACE = "place";
	public static final String TRANSITION = "transition";
	
	public PetriNetNode(PetriNet net, String id, String label, String type) {
		super(net, id, label, type);
	}
	
	public boolean isTransition() {
		return this.getType().equals(TRANSITION);
	}
	
	public boolean isPlace() {
		return this.getType().equals(PLACE);
	}
}
