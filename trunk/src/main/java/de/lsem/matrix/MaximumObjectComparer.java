package de.lsem.matrix;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class MaximumObjectComparer<T> implements ObjectComparer<T> {
	private ObjectComparer<T> comparer1;
	private ObjectComparer<T> comparer2;
	
	public MaximumObjectComparer(ObjectComparer<T> comparer1, ObjectComparer<T> comparer2) {
		this.comparer1 = comparer1;
		this.comparer2 = comparer2;
	}
	
	@Override
	public double compare(T obj1, T obj2) {
		return Math.max(this.comparer1.compare(obj1, obj2), this.comparer2.compare(obj1, obj2));
	}
}
