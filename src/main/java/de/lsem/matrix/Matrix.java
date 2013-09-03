package de.lsem.matrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Copyright (c) 2013 Christopher Klinkm�ller
 * 
 * This software is released under the terms of the
 * MIT license. See http://opensource.org/licenses/MIT
 * for more information.
 */

public class Matrix<T> {
	private Map<T, Integer> objectIds1;
	private Map<T, Integer> objectIds2;
	private double[][] similarities;
	private double threshold = -1;
	private double thresholdEquivalent;
	private List<T> objects1;
	private List<T> objects2;
	
	public Matrix(Iterable<T> objects1, Iterable<T> objects2, double initialValue) {
		this.thresholdEquivalent = initialValue;
		this.objectIds1 = new HashMap<T, Integer>();
		this.objectIds2 = new HashMap<T, Integer>();
		
		this.objects1 = new ArrayList<T>();
		this.objects2 = new ArrayList<T>();
		
		for (T obj : objects1) {
			this.objectIds1.put(obj, this.objectIds1.size());
			this.objects1.add(obj);
		}
		
		for (T obj : objects2) {
			this.objectIds2.put(obj, this.objectIds2.size());
			this.objects2.add(obj);
		}
		
		this.similarities = new double[this.objectIds1.size()][this.objectIds2.size()];
		for (int a = 0; a < this.objectIds1.size(); a++) {
			for (int b = 0; b < this.objectIds2.size(); b++) {
				this.similarities[a][b] = this.thresholdEquivalent;
			}
		}
	}
	
	public Matrix(Iterable<T> objects1, Iterable<T> objects2) {
		this(objects1, objects2, 0);
	}
	
	public double getThresholdEquivalent() {
		return this.thresholdEquivalent;
	}
	
	public void setThresholdEquivalent(double thresholdEquivalent) {
		this.thresholdEquivalent = thresholdEquivalent;
	}
	
	public double getThreshold() {
		return this.threshold;
	}
	
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	public Collection<T> getObjects1() {
		return this.objects1;
	}	
	
	public Collection<T> getObjects2() {
		return this.objects2;
	}
	
	public void setValue(T obj1, T obj2, double sim) {
		this.similarities[this.objectIds1.get(obj1)][this.objectIds2.get(obj2)] = sim;
	}
	
	public double getValue(T obj1, T obj2) {
		if (obj1 == null) {
			System.out.println("obj1 == null");
		}
		if (obj2 == null) {
			System.out.println("obj2 == null");
		}
		if (this.similarities == null) {
			System.out.println("sims == null");
		}
		if (this.objectIds1 == null) {
			System.out.println("objIds1 == null");
		}
		if (this.objectIds2 == null) {
			System.out.println("objIds2 == null");
		}
		if (this.objectIds1.get(obj1) == null) {
			System.out.println("get(obj1) == null");
		}
		if (this.objectIds2.get(obj2) == null) {
			System.out.println("get(obj2) == null");
		}
		
		double sim = this.similarities[this.objectIds1.get(obj1)][this.objectIds2.get(obj2)];
		return sim >= this.threshold ? sim : this.thresholdEquivalent;
	}
	
	public List<Double> getValuesForObject1 (T obj1, Set<T> objs2) {
		List<Double> values = new ArrayList<Double>();
		
		for (T obj2 : objs2) {
			values.add(this.getValue(obj1, obj2));
		}
		
		return values;
	}
	
	public List<Double> getValuesForObject2 (T obj2, Set<T> objs1) {
		List<Double> values = new ArrayList<Double>();
		
		for (T obj1 : objs1) {
			values.add(this.getValue(obj1, obj2));
		}
		
		return values;
	}
	
	public List<Match<T>> toMatchList() {
		List<Match<T>> matches = new ArrayList<Match<T>>();
		
		for (T obj1 : this.objects1) {
			for (T obj2 : this.objects2) {
				matches.add(new Match<T>(obj1, obj2));
			}
		}
		
		return matches;
	}
	
	public List<Match<T>> toDescendingSortedMatchList() {
		return this.toSortedMatchList(new Comparator<Match<T>>() {
			public int compare(Match<T> match1, Match<T> match2) {
				double sim1 = getValue(match1.getObject1(), match2.getObject2());
				double sim2 = getValue(match2.getObject1(), match2.getObject2());
				return sim1 > sim2 ? -1 : (Math.abs(sim1 - sim2) < 0.0001 ? 0 : 1); 
			}
			
		});
	}
	
	public List<Match<T>> toAscendingSortedMatchList() {
		return this.toSortedMatchList(new Comparator<Match<T>>() {
			public int compare(Match<T> match1, Match<T> match2) {
				double sim1 = getValue(match1.getObject1(), match2.getObject2());
				double sim2 = getValue(match2.getObject1(), match2.getObject2());
				return sim1 > sim2 ? 1 : (Math.abs(sim1 - sim2) < 0.0001 ? 0 : -1); 
			}
			
		});
	}
	
	private List<Match<T>> toSortedMatchList(Comparator<Match<T>> comparator) {
		List<Match<T>> matches = this.toMatchList();
		
		Collections.sort(matches, comparator);
		
		return matches;
	}
}
