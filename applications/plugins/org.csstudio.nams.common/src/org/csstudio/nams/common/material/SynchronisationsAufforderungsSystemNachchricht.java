
package org.csstudio.nams.common.material;

public class SynchronisationsAufforderungsSystemNachchricht implements
		SystemNachricht {

	@Override
    public boolean istSynchronisationsAufforderung() {
		return true;
	}

	@Override
    public boolean istSynchronisationsBestaetigung() {
		return false;
	}
}
