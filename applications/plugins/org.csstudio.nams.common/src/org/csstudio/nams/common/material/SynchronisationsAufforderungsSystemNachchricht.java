
package org.csstudio.nams.common.material;

public class SynchronisationsAufforderungsSystemNachchricht implements
		SystemMessage {

	@Override
    public boolean isSynchronizationRequest() {
		return true;
	}

	@Override
    public boolean isSynchronizationConfirmation() {
		return false;
	}
}
