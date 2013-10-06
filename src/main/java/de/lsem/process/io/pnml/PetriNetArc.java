package de.lsem.process.io.pnml;

import de.lsem.process.model.ProcessEdge;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

class PetriNetArc extends ProcessEdge {
	public PetriNetArc(PetriNetNode source, PetriNetNode target) {
		super(source, target);
	}
	
	public PetriNetArc(PetriNet net, String id, String label) {
		super(net, id, label);
	}
	
	public PetriNetArc(PetriNet net, String id, String label, PetriNetNode source, PetriNetNode target) {
		super(net, id, label, source, target);
	}
}
