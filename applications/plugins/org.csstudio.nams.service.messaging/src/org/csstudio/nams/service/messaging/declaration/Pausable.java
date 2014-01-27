package org.csstudio.nams.service.messaging.declaration;

public interface Pausable {
	void pause();
	void unpause();
	boolean isPaused();
}
