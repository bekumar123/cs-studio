package org.csstudio.nams.configurator.beans;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.csstudio.nams.configurator.beans.messages"; //$NON-NLS-1$
	public static String MessageExtensionBean_no_name;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
