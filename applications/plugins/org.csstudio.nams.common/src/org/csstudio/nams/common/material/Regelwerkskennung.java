package org.csstudio.nams.common.material;

import org.csstudio.nams.common.wam.Fachwert;

@Fachwert
public final class Regelwerkskennung {

	public static Regelwerkskennung valueOf(final int filterId) {
		return new Regelwerkskennung(filterId);
	}

	private final int id;

	private Regelwerkskennung(final int filterId) {
		this.id = filterId;
	}

	public int getRegelwerksId() {
		return this.id;
	}


	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Regelwerkskennung other = (Regelwerkskennung) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.valueOf(this.id);
	}
}
