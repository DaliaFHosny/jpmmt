package de.lsem.process.io.yasper;

import de.lsem.process.io.ProcessModelReader;
import de.lsem.process.model.ProcessModel;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class YasperReader extends ProcessModelReader {
	private YasperImporter importer;
	private PetriNetTransformation transformer;
	
	public YasperReader() {
		this.importer = new YasperImporter();
		this.transformer = new PetriNetTransformation();
	}
	
	@Override
	public ProcessModel read(String filename) {
		PetriNet net = this.importer.importFile(filename);
		ProcessModel process = this.transformer.transform(net);
		this.checkForMultipleEntries(process);
		this.checkForMultipleExits(process);
		return process;
	}

}
