package de.lsem.matrix;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class Match<T> {
	private T object1;
	private T object2;
	
	public Match() {		
	}
	
	public Match(T object1, T object2) {
		this.object1 = object1;
		this.object2 = object2;		
	}
	
	public T getObject1() {
		return this.object1;
	}
	
	public void setObject1(T object1) {
		this.object1 = object1;
	}
	
	public T getObject2() {
		return this.object2;
	}
	
	public void setObject2(T object2) {
		this.object2 = object2;
	}
}
