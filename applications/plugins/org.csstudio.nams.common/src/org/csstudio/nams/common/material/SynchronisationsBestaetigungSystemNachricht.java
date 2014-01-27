
package org.csstudio.nams.common.material;

public class SynchronisationsBestaetigungSystemNachricht implements
		SystemNachricht {

	@Override
    public boolean istSynchronisationsAufforderung() {
		return false;
	}

	@Override
    public boolean istSynchronisationsBestaetigung() {
		return true;
	}
}
