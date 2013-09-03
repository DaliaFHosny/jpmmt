package de.lsem.matrix;

import java.util.HashMap;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public final class StoreObjectComparer<T> implements ObjectComparer<T> {
	private ObjectComparer<T> comparer;
	private HashMap<T, HashMap<T, Double>> similarities;	
	
	public StoreObjectComparer(ObjectComparer<T> comparer) {
		if (comparer instanceof StoreObjectComparer) {
			//TODO: Exception
		}
		else {
			this.comparer = comparer;
			this.similarities = new HashMap<T, HashMap<T, Double>>();
		}
	}	
	
	@Override
	public double compare(T obj1, T obj2) {
		if (this.similarities.containsKey(obj1)) {
			if (this.similarities.get(obj1).containsKey(obj2)) {
				return this.similarities.get(obj1).get(obj2);
			}
		}
		else {
			this.similarities.put(obj1, new HashMap<T, Double>());
		}
		
		if (!this.similarities.containsKey(obj2)) {
			this.similarities.put(obj2, new HashMap<T, Double>());
		}
		
		double sim = this.comparer.compare(obj1, obj2);
		this.similarities.get(obj1).put(obj2, sim);
		this.similarities.get(obj2).put(obj1, sim);
		return sim;
	}

}
