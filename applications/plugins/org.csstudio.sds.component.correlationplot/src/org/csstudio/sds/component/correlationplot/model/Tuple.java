package org.csstudio.sds.component.correlationplot.model;

public class Tuple<T1, T2> {
	private T1 p1;
	private T2 p2;

	public Tuple(T1 p1, T2 p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public T1 getP1() {
		return p1;
	}
	
	public T2 getP2() {
		return p2;
	}
	
	@Override
	public String toString() {
		return "(" + p1 + ", " + p2 + ")";
	}
}