/*
 * Copyright (c) C1 WPS mbH, HAMBURG, GERMANY. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER.
 * C1 WPS HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
 * MAY FIND A COPY AT
 * {@link http://www.eclipse.org/org/documents/epl-v10.html}.
 */

package org.csstudio.nams.application.department.decision;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import org.csstudio.domain.common.statistic.Collector;
import org.csstudio.headless.common.util.ApplicationInfo;
import org.csstudio.headless.common.util.StandardStreams;
import org.csstudio.headless.common.xmpp.XmppCredentials;
import org.csstudio.headless.common.xmpp.XmppSessionException;
import org.csstudio.headless.common.xmpp.XmppSessionHandler;
import org.csstudio.nams.application.department.decision.management.InfoCmd;
import org.csstudio.nams.application.department.decision.management.Restart;
import org.csstudio.nams.application.department.decision.management.Stop;
import org.csstudio.nams.application.department.decision.office.decision.DecisionDepartment;
import org.csstudio.nams.application.department.decision.remote.RemotelyStoppable;
import org.csstudio.nams.common.AMS;
import org.csstudio.nams.common.IRemotelyAccesible;
import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiBundleDeactivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.material.regelwerk.Filter;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.service.StepByStepProcessor;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.history.declaration.HistoryStorageException;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.csstudio.nams.service.messaging.declaration.Consumer;
import org.csstudio.nams.service.messaging.declaration.MessagingService;
import org.csstudio.nams.service.messaging.declaration.MessagingSession;
import org.csstudio.nams.service.messaging.declaration.MultiConsumersConsumer;
import org.csstudio.nams.service.messaging.declaration.NAMSMessage;
import org.csstudio.nams.service.messaging.declaration.PostfachArt;
import org.csstudio.nams.service.messaging.declaration.Producer;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceConfigurationKeys;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceDatabaseKeys;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceJMSKeys;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceServiceManagementKeys;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleException;

/**
 * <p>
 * The decision department or more precise the activator and application class
 * to controls their life cycle.
 * </p>
 *
 * <p>
 * <strong>Pay attention:</strong> There are always exactly two instances of
 * this class present: The <emph>bundle activator instance</emph> and the
 * <emph>bundle application instance</emph>. The communication of both is hidden
 * in this class to hide the dirty static singleton communication. This is
 * required during the instantation of extensions (like {@link IApplication}) is
 * done in the framework and not by the plug in itself like it should be. Cause
 * of this all service field filled by the <emph>bundles activator</emph> start
 * operation are static to be accessible from the <emph>bundles
 * application</emph> start.
 * </p>
 *
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @author <a href="mailto:gs@c1-wps.de">Goesta Steen</a>
 *
 * @version 0.1-2008-04-25: Created.
 * @version 0.1.1-2008-04-28 (MZ): Change to use
 *          org.csstudio.nams.common.activatorUtils.BundleActivatorUtils.
 * @version 0.2.0-2008-06-10 (MZ): Change to use {@link AbstractBundleActivator}
 *          .
 */
public class DecisionDepartmentActivator extends AbstractBundleActivator implements IApplication, RemotelyStoppable, IRemotelyAccesible {

	private static final int DEFAULT_THREAD_COUNT = 10;

	class OutboxProcessor extends StepByStepProcessor {

		private final Inbox<MessageCasefile> vorgangskorb;

		public OutboxProcessor(final Inbox<MessageCasefile> vorgangskorb) {
			this.vorgangskorb = vorgangskorb;
		}

		@Override
		protected void doRunOneSingleStep() throws Throwable {

			try {
				final MessageCasefile vorgangsmappe = this.vorgangskorb.takeDocument();
				if (vorgangsmappe.istAbgeschlossenDurchTimeOut()) {
					DecisionDepartmentActivator.historyService.logTimeOutForTimeBased(vorgangsmappe);
				}

				// Nachricht nicht anreichern. Wird im JMSProducer gemacht
				// Versenden
				DecisionDepartmentActivator.logger.logInfoMessage(this, "decission office ordered message to be send: \""
						+ vorgangsmappe.getAlarmMessage().toString() + "\" [internal process id: " + vorgangsmappe.getCasefileId().toString() + "]");

				DecisionDepartmentActivator.this.amsAusgangsProducer.sendeVorgangsmappe(vorgangsmappe);

			} catch (final InterruptedException e) {
				// wird zum stoppen benötigt. hier muss nichts unternommen
				// werden
			}
		}
	}

	/**
	 * The plug-in ID of this bundle.
	 */
	public static final String PLUGIN_ID = "org.csstudio.nams.application.department.decision";

	/**
	 * Gemeinsames Attribut des Activators und der Application: Der Logger.
	 */
	protected static ILogger logger;

	/**
	 * Gemeinsames Attribut des Activators und der Application: Fatory for
	 * creating Consumers
	 */
	private static MessagingService messagingService;

	/**
	 * Service für das Entscheidungsbüro um das Starten der asynchronen
	 * Ausführung von Einzelaufgaben (Threads) zu kapseln.
	 */
	private static ExecutionService executionService;

	private static PreferenceService preferenceService;

	private static RegelwerkBuilderService regelwerkBuilderService;

	protected static HistoryService historyService;

	/**
	 * Service to receive configuration-data. Used by
	 * {@link RegelwerkBuilderService}.
	 */
	private static LocalStoreConfigurationService localStoreConfigurationService;

	private static String managementPassword;

	/**
	 * Indicates if the application instance should continue working. Unused in
	 * the activator instance.
	 *
	 * This field may be set by another thread to indicate that application
	 * should shut down.
	 */
	protected volatile boolean _continueWorking;

	private boolean restart;

	/**
	 * Referenz auf den Thread, welcher die JMS Nachrichten anfragt. Wird
	 * genutzt um den Thread zu "interrupten". Wird nur von der Application
	 * benutzt.
	 */
	private Thread commandMessagesReceiverThread;

	private Thread alarmReceiverThread;

	protected MultiConsumersConsumer alarmConsumer;

	private MessagingSession amsMessagingSessionForConsumer;

	/**
	 * Consumer zum Lesen auf Alarmnachrichten-Quelle.
	 */
	// private Consumer extAlarmConsumer;
	private Consumer[] extAlarmConsumer;

	/**
	 * Consumer zum Lesen auf externer-Komando-Quelle.
	 */
	private Consumer extCommandConsumer;

	/**
	 * Consumer zum Lesen auf ams-Komando-Quelle.
	 */
	private Consumer amsCommandConsumer;

	/**
	 * Producer zum Senden auf ams-Zielablage (normally Distributor or
	 * MessageMinder).
	 */
	protected Producer amsAusgangsProducer;

	/**
	 * MessageSession für externe Quellen und Ziele.
	 */
	private MessagingSession extMessagingSessionForConsumer;

	/**
	 * MessageSession für ams interne Quellen und Ziele.
	 */
	private MessagingSession amsMessagingSessionForProducer;

	private DecisionDepartment _alarmEntscheidungsBuero;

	// private AbstractMultiConsumerMessageHandler
	// messageHandlerToRecieveUntilApplicationQuits;

	private StepByStepProcessor _ausgangskorbBearbeiter;

	private Inbox<MessageCasefile> eingangskorbDesDecisionOffice;

	private DefaultDocumentBox<MessageCasefile> ausgangskorbDesDecisionOfficeUndEingangskorbDesPostOffice;

	private Producer extCommandProducer;

	/** Class that collects statistic informations. Query it via XMPP. */
	protected Collector ackMessages = null;

	private XmppSessionHandler xmppService;

	private ApplicationInfo appInfo;

	/**
	 * Starts the bundle activator instance. First Step.
	 *
	 * @see BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@OSGiBundleActivationMethod
	public void startBundle(@OSGiService @Required final ILogger injectedLogger,
			@OSGiService @Required final MessagingService injectedMessagingService,
			@OSGiService @Required final PreferenceService injectedPreferenceService,
			@OSGiService @Required final RegelwerkBuilderService injectedBuilderService,
			@OSGiService @Required final HistoryService injectedHistoryService,
			@OSGiService @Required final ConfigurationServiceFactory injectedConfigurationServiceFactory,
			@OSGiService @Required final ExecutionService injectedExecutionService) throws Exception {


		// uncaught (runtime) exceptions in threads should lead to decision department shutdown
		UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				logger.logFatalMessage(this, "Uncaught exception in decision department.");
				try {
					getBundleContext().getBundle(0).stop();
				} catch (BundleException e1) {
					e1.printStackTrace();
				} finally {
					System.exit(1);
				}
			}
		};
		Thread.setDefaultUncaughtExceptionHandler(handler);

		// ** Services holen...

		// Logging Service
		DecisionDepartmentActivator.logger = injectedLogger;

		DecisionDepartmentActivator.logger.logInfoMessage(this, "plugin " + DecisionDepartmentActivator.PLUGIN_ID + " initializing Services");

		// Messaging Service
		DecisionDepartmentActivator.messagingService = injectedMessagingService;

		// Preference Service (wird als konfiguration verwendet!!)
		DecisionDepartmentActivator.preferenceService = injectedPreferenceService;

		// RegelwerkBuilder Service
		DecisionDepartmentActivator.regelwerkBuilderService = injectedBuilderService;

		// History Service
		DecisionDepartmentActivator.historyService = injectedHistoryService;

		// LocalStoreConfigurationService
		final DatabaseType dbType = DatabaseType.valueOf(preferenceService.getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_TYPE));

		DecisionDepartmentActivator.localStoreConfigurationService = injectedConfigurationServiceFactory.getConfigurationService(
				DecisionDepartmentActivator.preferenceService.getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_CONNECTION), dbType,
				DecisionDepartmentActivator.preferenceService.getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_USER),
				DecisionDepartmentActivator.preferenceService.getString(PreferenceServiceDatabaseKeys.P_APP_DATABASE_PASSWORD));

		DecisionDepartmentActivator.executionService = injectedExecutionService;

		DecisionDepartmentActivator.managementPassword = DecisionDepartmentActivator.preferenceService
				.getString(PreferenceServiceManagementKeys.P_AMS_MANAGEMENT_PASSWORD);
		if (managementPassword == null) {
			managementPassword = "";
		}

		DecisionDepartmentActivator.logger.logInfoMessage(this, "Plugin " + DecisionDepartmentActivator.PLUGIN_ID + " started succesfully.");
	}

	/**
	 * Starts the bundle application instance. Second Step.
	 *
	 * @see IApplication#start(IApplicationContext)
	 */
	@Override
	public Object start(final IApplicationContext context) {

		restart = false;

		// IMPORTANT: The call of the injection methods must be done HERE and not within the activator.
		//            Mixing the Application and Activator is very confusing...
		Stop.staticInject(this);
	    Stop.staticInject(DecisionDepartmentActivator.logger);
	    Restart.staticInject(this);
	    Restart.staticInject(DecisionDepartmentActivator.logger);
	    InfoCmd.staticInject(this);

	    StandardStreams stdStreams = new StandardStreams("./log");
	    stdStreams.redirectStreams();

		ackMessages = new Collector();
		ackMessages.setApplication("AmsDecisionDepartment");
		ackMessages.setDescriptor("NOT acknowleged messages");
		ackMessages.setContinuousPrint(false);
		ackMessages.setContinuousPrintCount(1000.0);

		// Initialize state for normal run

		// just to make it possible to stop while start up (will be reset
		// later):
		this.commandMessagesReceiverThread = Thread.currentThread();
		this._alarmEntscheidungsBuero = null;
		this._ausgangskorbBearbeiter = null;
		this._continueWorking = true;

		DecisionDepartmentActivator.logger.logInfoMessage(this, "Decision department application is going to be initialized...");

		IPreferencesService amsPrefService = Platform.getPreferencesService();
		String applicationDescription = amsPrefService.getString(DecisionDepartmentActivator.PLUGIN_ID, "description", "I am a simple but happy application.", null);
        appInfo = new ApplicationInfo("AMS", AMS.AMS_MAIN_VERSION, "AmsDepartmentDecision", applicationDescription);

		configureXmppConnection(amsPrefService);

		createMessagingConsumer();

		createMessagingProducer();

		createDecisionOffice();

		// Ausgangskoerbe nebenläufig abfragen
		this._ausgangskorbBearbeiter = new OutboxProcessor(this.ausgangskorbDesDecisionOfficeUndEingangskorbDesPostOffice);
		DecisionDepartmentActivator.executionService.executeAsynchronously(ThreadTypesOfDecisionDepartment.AUSGANGSKORBBEARBEITER,
				this._ausgangskorbBearbeiter);

		context.applicationRunning();

		receiveAlarmsAsynchronouslyUntilApplicationQuits(eingangskorbDesDecisionOffice);
		receiveCommandMessagesUntilApplicationQuits(); // blocks while _continueWorking is true

		DecisionDepartmentActivator.logger.logInfoMessage(this, "Decision department has stopped message processing and continue shutting down...");

		closeDecissionOffice();
		closeMessagingConnections();

		xmppService.disconnect();

		DecisionDepartmentActivator.logger.logInfoMessage(this, "Decision department application successfully shuted down.");

		Integer exitCode = IApplication.EXIT_OK;
		if (this.restart) {
			exitCode = IApplication.EXIT_RESTART;
		}
		return exitCode;
	}

	/**
	 * Stops the bundle application instance.Ppenultimate Step.
	 *
	 * Diese Methode darf NICHT von der Anwendung selber aufgerufen
	 * werden. Sie wird vom Framework aufgerufen, wenn z.B. die Anwendung von
	 * Außen beendet werden soll oder das Framework herunterfährt.
	 *
	 * @see IApplication#start(IApplicationContext)
	 */
	@Override
	public void stop() {
		// Nothing to do here
	}

	   /**
     * {@inheritDoc}
     */
    @Override
    public String getInfo() {
        return appInfo.toString();
    }

	/**
	 * Stops the bundle activator instance. Last Step.
	 *
	 * @see BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@OSGiBundleDeactivationMethod
	public void stopBundle(@OSGiService @Required final ILogger log) throws Exception {
		log.logInfoMessage(this, "Plugin " + DecisionDepartmentActivator.PLUGIN_ID + " stopped succesfully.");
	}

	/**
     *
     */
	@Override
	public synchronized void stopRemotely(final ILogger log) {

		DecisionDepartmentActivator.logger.logInfoMessage(this, "Start to shut down decision department application on user request...");
		this._continueWorking = false;
		this.restart = false;

		DecisionDepartmentActivator.logger.logInfoMessage(this, "Interrupting working thread...");

		this.commandMessagesReceiverThread.interrupt();
		this.alarmReceiverThread.interrupt();

		log.logDebugMessage(this, "DecisionDepartmentActivator.stopRemotely(): After this.stop()");
	}

	@Override
	public synchronized void restartRemotly(final ILogger log) {

		DecisionDepartmentActivator.logger.logInfoMessage(this, "Begin to restart decision department application on user request...");
		this._continueWorking = false;
		this.restart = true;

		DecisionDepartmentActivator.logger.logInfoMessage(this, "Interrupting working thread...");

		this.commandMessagesReceiverThread.interrupt();
		this.alarmReceiverThread.interrupt();

		log.logDebugMessage(this, "DecisionDepartmentActivator.stopRemotely(): After this.stop()");
	}

	/**
	 *
	 * @return The management password
	 */
	@Override
	public synchronized String getPassword() {
		return DecisionDepartmentActivator.managementPassword;
	}

	private void createDecisionOffice() {
		try {
			DecisionDepartmentActivator.logger.logInfoMessage(this, "Decision department application is creating decision office...");

			final List<Filter> alleRegelwerke = DecisionDepartmentActivator.regelwerkBuilderService.getAllFilters();

			DecisionDepartmentActivator.logger.logDebugMessage(this, "alleRegelwerke size: " + alleRegelwerke.size());
			for (final Filter regelwerk : alleRegelwerke) {
				DecisionDepartmentActivator.logger.logDebugMessage(this, regelwerk.toString());
			}

			this.eingangskorbDesDecisionOffice = new DefaultDocumentBox<MessageCasefile>();
			this.ausgangskorbDesDecisionOfficeUndEingangskorbDesPostOffice = new DefaultDocumentBox<MessageCasefile>();

			final IPreferencesService pref1 = Platform.getPreferencesService();
			int threadCount = pref1.getInt(DecisionDepartmentActivator.PLUGIN_ID, PreferenceServiceConfigurationKeys.FILTER_THREAD_COUNT.getKey(),
					DEFAULT_THREAD_COUNT, null);
			this._alarmEntscheidungsBuero = new DecisionDepartment(DecisionDepartmentActivator.executionService, alleRegelwerke,
					this.eingangskorbDesDecisionOffice, this.ausgangskorbDesDecisionOfficeUndEingangskorbDesPostOffice, threadCount,
					DecisionDepartmentActivator.logger);
		} catch (final Throwable e) {
			DecisionDepartmentActivator.logger.logFatalMessage(this, "Exception while initializing the alarm decision department.", e);
			this._continueWorking = false;
		}

		DecisionDepartmentActivator.logger.logInfoMessage(this,
				"******* Decision department application successfully initialized, beginning work... *******");
	}

	private void configureXmppConnection(IPreferencesService amsPrefService) {
		String xmppServer = amsPrefService.getString(DecisionDepartmentActivator.PLUGIN_ID, "xmppServer", "krynfs.desy.de", null);
		String xmppUser = amsPrefService.getString(DecisionDepartmentActivator.PLUGIN_ID, "xmppUser", "anonymous", null);
		String xmppPassword = amsPrefService.getString(DecisionDepartmentActivator.PLUGIN_ID, "xmppPassword", "anonymous", null);

		XmppCredentials credentials = new XmppCredentials(xmppServer, xmppUser, xmppPassword);
		xmppService = new XmppSessionHandler(bundleContext, credentials, true);

	    try {
	        xmppService.connect();
	    } catch (XmppSessionException e) {
	        DecisionDepartmentActivator.logger.logWarningMessage(this, e.getMessage());
	    }
	}

	private void closeMessagingConnections() {
		// Alle Verbindungen schließen
		DecisionDepartmentActivator.logger.logInfoMessage(this, "Decision department application is closing opened connections...");
		if (this.amsAusgangsProducer != null && !this.amsAusgangsProducer.isClosed()) {
			this.amsAusgangsProducer.tryToClose();
		}
		if (this.amsCommandConsumer != null && !this.amsCommandConsumer.isClosed()) {
			this.amsCommandConsumer.close();
		}
		if (this.amsMessagingSessionForConsumer != null && !this.amsMessagingSessionForConsumer.isClosed()) {
			this.amsMessagingSessionForConsumer.close();
		}
		if (this.amsMessagingSessionForProducer != null && !this.amsMessagingSessionForProducer.isClosed()) {
			this.amsMessagingSessionForProducer.close();
		}

		for (Consumer c : extAlarmConsumer) {
			if (c != null) {
				if (c.isClosed() == false) {
					c.close();
				}
			}
		}

		if (this.extCommandConsumer != null && !this.extCommandConsumer.isClosed()) {
			this.extCommandConsumer.close();
		}

		if (this.extMessagingSessionForConsumer != null && !this.extMessagingSessionForConsumer.isClosed()) {
			this.extMessagingSessionForConsumer.close();
		}
	}

	private void closeDecissionOffice() {
		if (this._alarmEntscheidungsBuero != null) {
			this._alarmEntscheidungsBuero.beendeArbeitUndSendeSofortAlleOffeneneVorgaenge();
		}

		// Warte auf Thread für Ausgangskorb-Bearbeitung
		if (this._ausgangskorbBearbeiter != null && this._ausgangskorbBearbeiter.isCurrentlyRunning()) {
			// FIXME Warte bis korb leer ist.
			this._ausgangskorbBearbeiter.stopWorking();
		}
	}

	private void createMessagingProducer() {
		try {

			DecisionDepartmentActivator.logger.logInfoMessage(this, "Decision department application is creating producers...");

			// FIXM E(done) clientid!!
			final String amsSenderProviderUrl = DecisionDepartmentActivator.preferenceService
					.getString(PreferenceServiceJMSKeys.P_JMS_AMS_SENDER_PROVIDER_URL);
			DecisionDepartmentActivator.logger.logDebugMessage(this, "PreferenceServiceJMSKeys.P_JMS_AMS_SENDER_PROVIDER_URL = "
					+ amsSenderProviderUrl);
			this.amsMessagingSessionForProducer = DecisionDepartmentActivator.messagingService.createNewMessagingSession(JmsTool.createUniqueClientId(
					preferenceService.getString(PreferenceServiceJMSKeys.P_JMS_AMS_TSUB_DD_OUTBOX)), new String[] { amsSenderProviderUrl });

			final String amsAusgangsTopic = DecisionDepartmentActivator.preferenceService
					.getString(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_DD_OUTBOX);
			DecisionDepartmentActivator.logger.logDebugMessage(this, "PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_DD_OUTBOX(AusgangsTopic) = "
					+ amsAusgangsTopic);
			this.amsAusgangsProducer = this.amsMessagingSessionForProducer.createProducer(amsAusgangsTopic, PostfachArt.TOPIC);

		} catch (final Throwable e) {
			DecisionDepartmentActivator.logger.logFatalMessage(this, "Exception while initializing the alarm decision department.", e);
			this._continueWorking = false;
		}
	}

	private void createMessagingConsumer() {
		try {
			DecisionDepartmentActivator.logger.logInfoMessage(this, "Decision department application is creating consumers...");

			final String amsProvider1 = DecisionDepartmentActivator.preferenceService.getString(PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_1);
			final String amsProvider2 = DecisionDepartmentActivator.preferenceService.getString(PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_2);

			DecisionDepartmentActivator.logger.logDebugMessage(this, "PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_1 = " + amsProvider1);
			DecisionDepartmentActivator.logger.logDebugMessage(this, "PreferenceServiceJMSKeys.P_JMS_AMS_PROVIDER_URL_2 = " + amsProvider2);

			this.amsMessagingSessionForConsumer = DecisionDepartmentActivator.messagingService.createNewMessagingSession(JmsTool.createUniqueClientId(
					preferenceService.getString(PreferenceServiceJMSKeys.P_JMS_AMS_TSUB_COMMAND_DECISSION_DEPARTMENT)), new String[] { amsProvider1,
							amsProvider2 });
			final String extProvider1 = DecisionDepartmentActivator.preferenceService.getString(PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_1);
			final String extProvider2 = DecisionDepartmentActivator.preferenceService.getString(PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_2);
			DecisionDepartmentActivator.logger.logDebugMessage(this, "PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_1 = " + extProvider1);
			DecisionDepartmentActivator.logger.logDebugMessage(this, "PreferenceServiceJMSKeys.P_JMS_EXTERN_PROVIDER_URL_2 = " + extProvider2);
			this.extMessagingSessionForConsumer = DecisionDepartmentActivator.messagingService.createNewMessagingSession(JmsTool.createUniqueClientId(
					preferenceService.getString(PreferenceServiceJMSKeys.P_JMS_EXT_TSUB_ALARM)), new String[] { extProvider1, extProvider2 });

			final String extAlarmTopic = DecisionDepartmentActivator.preferenceService.getString(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_ALARM);
			DecisionDepartmentActivator.logger.logDebugMessage(this, "PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_ALARM = " + extAlarmTopic);

			// extAlarmTopic may contain a comma-seperated list of topics
			String[] topicList = extAlarmTopic.split(",");

			// FIXME gs,mz 2008-09-11 make durable when global alarm server
			// suports durable
			extAlarmConsumer = new Consumer[topicList.length];
			for (int i = 0; i < topicList.length; i++) {
				this.extAlarmConsumer[i] = this.extMessagingSessionForConsumer.createConsumer(topicList[i], PostfachArt.TOPIC);
			}

			// ext wird durch ams Server ersetzt
			this.extCommandConsumer = this.amsMessagingSessionForConsumer.createConsumer(
					DecisionDepartmentActivator.preferenceService.getString(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_COMMAND),
					PostfachArt.TOPIC_DURABLE);

			this.extCommandProducer = this.amsMessagingSessionForConsumer.createProducer(
					DecisionDepartmentActivator.preferenceService.getString(PreferenceServiceJMSKeys.P_JMS_EXT_TOPIC_COMMAND), PostfachArt.TOPIC);

			final String amsCommandTopic = DecisionDepartmentActivator.preferenceService.getString(PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND);
			DecisionDepartmentActivator.logger.logDebugMessage(this, "PreferenceServiceJMSKeys.P_JMS_AMS_TOPIC_COMMAND = " + amsCommandTopic);
			this.amsCommandConsumer = this.amsMessagingSessionForConsumer.createConsumer(amsCommandTopic, PostfachArt.TOPIC_DURABLE);
		} catch (final Throwable e) {
			DecisionDepartmentActivator.logger.logFatalMessage(this, "Exception while initializing the alarm decision department.", e);
			this._continueWorking = false;
		}
	}

	/**
	 * This method is receiving Messages and handle them. It will block until
	 * _continueWorking get false.
	 *
	 * @param eingangskorb
	 *            Der {@link Inbox} to read on.
	 */
	private void receiveCommandMessagesUntilApplicationQuits() {
		this.commandMessagesReceiverThread = Thread.currentThread();

		final Consumer[] commandConsumerArray = new Consumer[] { this.amsCommandConsumer, this.extCommandConsumer };
		final MultiConsumersConsumer commandConsumer = new MultiConsumersConsumer(DecisionDepartmentActivator.logger, commandConsumerArray,
				DecisionDepartmentActivator.executionService);

		Synchronizer synchronizer = new Synchronizer(historyService, logger, alarmConsumer, amsAusgangsProducer, extCommandProducer,
				localStoreConfigurationService, regelwerkBuilderService, _alarmEntscheidungsBuero);

		while (this._continueWorking) {
			try {
				final NAMSMessage message = commandConsumer.receiveMessage();
				ackMessages.incrementValue();
				DecisionDepartmentActivator.logger.logInfoMessage(this, "Decision department recieves a message to handle: " + message.toString());

				if (message.enthaeltSystemnachricht()) {
					handleSystemMessage(synchronizer, message);
				}
			} catch (final MessagingException e) {
				DecisionDepartmentActivator.logger.logErrorMessage(this, "Exception during recieve of message.", e);
			} catch (final InterruptedException ie) {
				DecisionDepartmentActivator.logger.logInfoMessage(this, "Recieving of message has been interrupted", ie);
			}
		}
		commandConsumer.close();
	}

	private void handleSystemMessage(Synchronizer synchronizer, final NAMSMessage message) throws StorageError {
		try {
			if (message.alsSystemachricht().istSynchronisationsAufforderung()) {
				synchronizer.startSynchronization();
			} else if (message.alsSystemachricht().istSynchronisationsBestaetigung()) {
				logReceivedReplicationDoneMessage();
				synchronizer.handleSynchronizationFinishedMessageReceived();
			}
		} catch (Exception e) {
			DecisionDepartmentActivator.logger.logErrorMessage(this,
					"Exception occured while synchronizing decision department configuration: " + e.getLocalizedMessage());
		} finally {
			acknowledgeMessage(message);
		}
	}

	private void receiveAlarmsAsynchronouslyUntilApplicationQuits(final Inbox<MessageCasefile> eingangskorb) {
		final Consumer[] alarmConsumerArray = new Consumer[this.extAlarmConsumer.length];

		for (int i = 0; i < extAlarmConsumer.length; i++) {
			alarmConsumerArray[i] = this.extAlarmConsumer[i];
		}

		alarmConsumer = new MultiConsumersConsumer(DecisionDepartmentActivator.logger, alarmConsumerArray,
				DecisionDepartmentActivator.executionService);

		alarmReceiverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (_continueWorking) {
					try {
						final NAMSMessage message = alarmConsumer.receiveMessage();
						ackMessages.incrementValue();
						DecisionDepartmentActivator.logger.logInfoMessage(this,
								"Decision department recieves a message to handle: " + message.toString());
						if (message.enthaeltAlarmnachricht()) {
							try {
								eingangskorb.put(new MessageCasefile(CasefileId.createNew(),
										message.alsAlarmnachricht()));
							} catch (final InterruptedException e) {
								DecisionDepartmentActivator.logger.logInfoMessage(this, "Message processing interrupted", e);
							} finally {
								acknowledgeMessage(message);
							}
						}
					} catch (final MessagingException e) {
						// TODO was soll hier geschehen?
						DecisionDepartmentActivator.logger.logErrorMessage(this, "Exception during recieve of message.", e);
					} catch (final InterruptedException ie) {
						DecisionDepartmentActivator.logger.logInfoMessage(this, "Recieving of message has been interrupted", ie);
					}
				}

				alarmConsumer.close();
			}
		});

		alarmReceiverThread.start();
	}

	protected void acknowledgeMessage(final NAMSMessage message) {
		try {
			message.acknowledge();
			ackMessages.decrementValue();
		} catch (final MessagingException e) {
			DecisionDepartmentActivator.logger.logWarningMessage(this, "unable to acknowlwedge message: " + message.toString(), e);
		}
	}

	private void logReceivedReplicationDoneMessage() {
		try {
			historyService.logReceivedReplicationDoneMessage();
		} catch (HistoryStorageException hse) {
			DecisionDepartmentActivator.logger.logWarningMessage(this, "Exception occured while logging history entry: " + hse.getLocalizedMessage());
		}
	}
}
