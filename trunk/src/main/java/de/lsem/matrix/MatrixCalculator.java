package de.lsem.matrix;


/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class MatrixCalculator<T> {
	private ObjectComparer<T> comparer;
	
	public MatrixCalculator(ObjectComparer<T> comparer) {
		this.comparer = comparer;
	}
	
	public Matrix<T> calculateMatrix(Iterable<T> objects1, Iterable<T> objects2) {
		Matrix<T> simMatrix = new Matrix<T>(objects1, objects2);
		
		for (T obj1 : objects1) {
			for (T obj2 : objects2) {
				double sim = this.comparer.compare(obj1, obj2);
				simMatrix.setValue(obj1, obj2, sim);
			}
		}
				
		return simMatrix;
	}
	
	public Matrix<T> calculateSymetricMatrix(Iterable<T> objects) {
		Matrix<T> simMatrix = new Matrix<T>(objects, objects);
		
		for (T obj1 : objects) {
			for (T obj2 : objects) {
				if (obj1 != obj2) {
					if (simMatrix.getValue(obj1, obj2) == -1) {
						double sim = comparer.compare(obj1, obj2);
						simMatrix.setValue(obj1, obj2, sim);
						simMatrix.setValue(obj2, obj1, sim);
					}
				}
				else {
					simMatrix.setValue(obj1, obj2, 1);
				}
			}
		}
		
		return simMatrix;
	}
}
