package de.lsem.process.io;

import de.lsem.process.io.cpf.CpfReader;
import de.lsem.process.io.epml.EpmlReader;
import de.lsem.process.io.pnml.YasperReader;

/*
 * Copyright (c) 2013 Christopher Klinkmüller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

/**
 * 
 * @author Christopher Klinkmüller
 *
 */
public class ProcessModelReaderDirectory {
	public static ProcessModelReader getCpfReader() {
		return new CpfReader();
	}
	
	public static ProcessModelReader getEpmlReader() {
		return new EpmlReader();
	}
	
	public static ProcessModelReader getEpmlReader(boolean removeMultipleEntries, boolean removeMultipleExits, boolean removeSilentTransititions) {
		return new EpmlReader(removeMultipleEntries, removeMultipleExits, removeSilentTransititions);
	}
	
	public static ProcessModelReader getYasperPnmlReader() {
		return new YasperReader();
	}
	
	public static ProcessModelReader getYasperPnmlReader(boolean removeMultipleEntries, boolean removeMultipleExits, boolean removeSilentTransititions) {
		return new YasperReader(removeMultipleEntries, removeMultipleExits, removeSilentTransititions);
	}
}
