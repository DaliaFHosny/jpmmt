package de.lsem.process.io.epml;

import de.lsem.process.io.ProcessModelReader;
import de.lsem.process.model.ProcessModel;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class EpmlReader extends ProcessModelReader {
	private EpmlImporter importer = new EpmlImporter();
	private EpmlTransformer transformer = new EpmlTransformer();
	
	@Override
	public ProcessModel read(String filename) {
		EventDrivenProcessChain epc = this.importer.importEpc(filename);
		ProcessModel process = this.transformer.transform(epc);
		this.checkForMultipleEntries(process);
		this.checkForMultipleExits(process);
		return process;
	}

}
