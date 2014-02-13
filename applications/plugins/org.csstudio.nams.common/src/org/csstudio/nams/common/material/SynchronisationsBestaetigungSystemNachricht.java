
package org.csstudio.nams.common.material;

public class SynchronisationsBestaetigungSystemNachricht implements
		SystemMessage {

	@Override
    public boolean isSynchronizationRequest() {
		return false;
	}

	@Override
    public boolean isSynchronizationConfirmation() {
		return true;
	}
}
