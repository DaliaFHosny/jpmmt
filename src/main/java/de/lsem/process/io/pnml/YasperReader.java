package de.lsem.process.io.pnml;

import de.lsem.process.io.ProcessModelReader;
import de.lsem.process.model.ProcessModel;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class YasperReader extends ProcessModelReader {
	private YasperImporter importer;
	private PetriNetTransformation transformer;
	
	public YasperReader() {
		this(false, false);
	}
	
	public YasperReader(boolean removeMultipleEntries, boolean removeMultipleExits) {
		super(removeMultipleEntries, removeMultipleExits);
		this.importer = new YasperImporter();
		this.transformer = new PetriNetTransformation();
	}
	
	@Override
	protected ProcessModel readModel(String filename) {
		PetriNet net = this.importer.importFile(filename);
		ProcessModel process = this.transformer.transform(net);
		return process;
	}

}
