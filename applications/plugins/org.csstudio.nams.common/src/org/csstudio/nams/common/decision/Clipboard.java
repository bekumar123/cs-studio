
package org.csstudio.nams.common.decision;

public interface Clipboard<T extends Document> extends
		Outbox<T>, Iterable<T> {
    // Nothing here
}
