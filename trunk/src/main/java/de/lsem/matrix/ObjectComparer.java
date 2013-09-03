package de.lsem.matrix;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

/**
 * 
 * This interface provides a method for comparing to objects of the same class and returning a double value indicating their similarity.
 * 
 * @author Christopher Klinkmüller
 *
 * @param <T> 	Determines the o 
 * 
 */
public interface ObjectComparer<T> {
	double compare(T obj1, T obj2);
}
