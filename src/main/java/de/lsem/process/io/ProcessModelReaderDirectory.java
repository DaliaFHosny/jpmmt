package de.lsem.process.io;

import de.lsem.process.io.cpf.CpfReader;
import de.lsem.process.io.epml.EpmlReader;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class ProcessModelReaderDirectory {
	public static ProcessModelReader getCpfReader() {
		return new CpfReader();
	}
	
	public static ProcessModelReader getEpmlReader() {
		return new EpmlReader();
	}
}
