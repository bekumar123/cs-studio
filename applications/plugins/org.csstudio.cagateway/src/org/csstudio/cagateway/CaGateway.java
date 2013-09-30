
package org.csstudio.cagateway;

import org.csstudio.cagateway.management.InfoCmd;
import org.csstudio.cagateway.preferences.CAGatewayPreference;
import org.csstudio.headless.common.signal.HeadlessSignalHandler;
import org.csstudio.headless.common.signal.ISignalReceiver;
import org.csstudio.headless.common.signal.SignalException;
import org.csstudio.headless.common.util.ApplicationInfo;
import org.csstudio.headless.common.util.StandardStreams;
import org.csstudio.headless.common.xmpp.XmppCredentials;
import org.csstudio.headless.common.xmpp.XmppSessionException;
import org.csstudio.headless.common.xmpp.XmppSessionHandler;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaGateway implements IApplication, RemotelyAccessible, ISignalReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(CaGateway.class);

	private static CaServer caGatewayInstance = null;

    private XmppSessionHandler xmppSessionHandler;

	private ApplicationInfo appInfo;

	public CaGateway() {
	    String xmppServer = CAGatewayPreference.XMPP_SERVER_NAME.getValue();
        String xmppUser = CAGatewayPreference.XMPP_USER_NAME.getValue();
        String xmppPassword = CAGatewayPreference.XMPP_PASSWORD.getValue();
        XmppCredentials credentials = new XmppCredentials(xmppServer, xmppUser, xmppPassword);
        xmppSessionHandler = new XmppSessionHandler(Activator.getBundleContext(), credentials, true);
        appInfo = new ApplicationInfo("DoocsCAServer", "DOOCS TO EPICS CA-Gateway");
	}

	@Override
    public Object start(final IApplicationContext context) throws Exception {

	    LOG.info("Starting caGateway");

	    StandardStreams stdStreams = new StandardStreams("./log");
	    stdStreams.redirectStreams();

	    try {
	        HeadlessSignalHandler signalHandler = new HeadlessSignalHandler(this);
	        signalHandler.activateIntSignal();
	        signalHandler.activateTermSignal();
	    } catch (SignalException e) {
	        LOG.warn("CANNOT create the signal handler. Any signal will be ignored.");
	    }

		InfoCmd.staticInject(this);
		try {
		    xmppSessionHandler.connect();
		} catch (XmppSessionException e) {
		    LOG.warn("Cannot connect to the XMPP server.");
		}

		context.applicationRunning();

		caGatewayInstance = CaServer.getGatewayInstance();
		caGatewayInstance.execute();

		xmppSessionHandler.disconnect();

		LOG.info("Leaving caGateway application.");
		return IApplication.EXIT_OK;
	}

	@Override
    public void stop() {
		// Auto-generated method stub
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInfo() {
        return appInfo.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminate() {
        CaServer.getGatewayInstance().stop();
    }
}
