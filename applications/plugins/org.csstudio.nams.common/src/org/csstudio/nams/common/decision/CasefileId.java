
package org.csstudio.nams.common.decision;

import java.util.concurrent.atomic.AtomicLong;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.common.wam.Fachwert;

@Fachwert
public final class CasefileId {

	private static AtomicLong idCounter = new AtomicLong(0);

	public static CasefileId createNew() {
		long id = idCounter.incrementAndGet();
		return new CasefileId(id, null);
	}

	public static CasefileId valueOf(
			final CasefileId caseFileId, final String extension) {
		Contract.require(caseFileId != null, "caseFileId != null");
		Contract.require(!caseFileId.hasExtension(), "!caseFileId.hasExtension()");
		
		return new CasefileId(caseFileId.id, extension);
	}

	private final long id;
	private final String extension;
	
	private CasefileId(final long id, final String extension) {
		this.id = id;
		this.extension = extension;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CasefileId)) {
			return false;
		}
		final CasefileId other = (CasefileId) obj;
		if (this.id != other.id) {
			return false;
		}
		if (this.extension == null) {
			if (other.extension != null) {
				return false;
			}
		} else if (!this.extension.equals(other.extension)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.extension == null) ? 0 : this.extension.hashCode());
		result = prime * result + (int) (this.id ^ (this.id >>> 32));
		return result;
	}

	public boolean hasExtension() {
		return this.extension != null;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(this.id);
		if (this.extension != null) {
			builder.append('/');
			builder.append(this.extension);
		}
		return builder.toString();
	}
}
