package de.lsem.process.matching.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.lsem.matrix.Match;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class MappingDeterminer<T> {
	private List<Set<Match<T>>> mappings;	
	
	public MappingDeterminer() {
		this.mappings = new ArrayList<Set<Match<T>>>();
	}
	
	public void reset() {
		this.mappings.clear();
	}
	
	public Iterable<Set<Match<T>>> getMappings() {
		return this.mappings;
	}
	
	public void addMatch(Match<T> match) {
		List<Set<Match<T>>> m = new ArrayList<Set<Match<T>>>();
		
		for (Set<Match<T>> matches : mappings) {
			boolean contain = false;
			for (Match<T> c : matches) {
				if (c.getObject1() == match.getObject1() || c.getObject2() == match.getObject2()) {
					contain = true;
					break;
				}
			}
			if (contain) {
				m.add(matches);
			}
		}
		
		if (m.size() == 0) {
			HashSet<Match<T>> set = new HashSet<Match<T>>();
			set.add(match);
			this.mappings.add(set);
		}
		else if (m.size() == 1) {
			m.get(0).add(match);
		}
		else {
			m.get(0).add(match);
			for (int a = 1; a < m.size(); a++) {
				this.mappings.remove(m.get(a));
				for (Match<T> c : m.get(a)) {
					m.get(0).add(c);
				}
			}
		}
	}
}
