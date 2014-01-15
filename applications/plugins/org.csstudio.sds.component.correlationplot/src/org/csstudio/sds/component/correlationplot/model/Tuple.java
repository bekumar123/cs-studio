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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
		result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple other = (Tuple) obj;
		if (p1 == null) {
			if (other.p1 != null)
				return false;
		} else if (!p1.equals(other.p1))
			return false;
		if (p2 == null) {
			if (other.p2 != null)
				return false;
		} else if (!p2.equals(other.p2))
			return false;
		return true;
	}
}