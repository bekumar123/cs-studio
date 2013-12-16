package org.remotercp.service.connection.session.impl;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.provider.xmpp.identity.XMPPID;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.remotercp.service.connection.ConnectionActivator;
import org.remotercp.service.connection.session.ISessionService;

public class SessionServiceImpl implements ISessionService {

	private IContainer container;

	private IRemoteServiceContainerAdapter adapter;

	private static final Logger logger = Logger
			.getLogger(SessionServiceImpl.class.getName());

	private String userName;

	private XMPPID xmppid;

	private final String CONTAINER_NAME = "ecf.xmpps.smack";

	private ServiceTracker remoteServiceTracker;

	public SessionServiceImpl() {
		try {
			container = ContainerFactory.getDefault().createContainer(
					CONTAINER_NAME);

			// due to S.Lewis this order is very important, the adapter must be
			// retrieved before the container connects!!!!
			adapter = (IRemoteServiceContainerAdapter) container
					.getAdapter(IRemoteServiceContainerAdapter.class);
			assert adapter != null : "parameter adapter != null";

		} catch (ContainerCreateException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Connects to an XMPP-Server with the provided credentials
	 * 
	 * @throws ECFException
	 */
	public void connect(String userName, String password, String server)
			throws URISyntaxException, ECFException {
		this.userName = userName;

		xmppid = new XMPPID(container.getConnectNamespace(), userName + "@"
				+ server);
//		xmppid = new XMPPID(container.getConnectNamespace(), userName + "X"
//				+ server);
		xmppid.setResourceName("" + System.currentTimeMillis());

		IConnectContext connectContext = ConnectContextFactory
				.createUsernamePasswordConnectContext(userName, password);
		container.connect(xmppid, connectContext);

		logger.info("************************* \n User: " + userName
				+ " connected to server: " + server
				+ " connected to: " + server
				+ "\n ******************************");

		initRemoteServiceTracker();
	}

	/*
	 * Start to listen for remote services as soon as the connection is
	 * established
	 */
	private void initRemoteServiceTracker() {
		try {
			Filter filter = ConnectionActivator
					.getBundleContext()
					.createFilter(
							" (| (objectClass=org.remotercp*) (objectClass=org.csstudio*))");

			remoteServiceTracker = new ServiceTracker(
					ConnectionActivator.getBundleContext(), filter, null) {

				public Object addingService(ServiceReference reference) {
					String property = (String) reference
							.getProperty("osgi.remote.interfaces");

					Object remoteService = super.addingService(reference);
					if (property != null && property.equals("*")) {
						/*
						 * remote service property found, register the service
						 * as remote service within ECF
						 */
						String[] serviceInterface = (String[]) reference
								.getProperty(org.osgi.framework.Constants.OBJECTCLASS);

						registerRemoteService(serviceInterface[0],
								remoteService, null);
					}
					return remoteService;
				}

			};
			remoteServiceTracker.open();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

	}

	private IPresenceContainerAdapter getPresenceContainerAdapter() {
		IPresenceContainerAdapter adapter = (IPresenceContainerAdapter) this.container
				.getAdapter(IPresenceContainerAdapter.class);
		assert adapter != null : "adapter != null";
		return adapter;
	}

	public synchronized IRemoteServiceContainerAdapter getRemoteServiceContainerAdapter() {
		assert adapter != null : "parameter adapter != null";
		return adapter;
	}

	/**
	 * Returns a list of remote service proxies for a given service. The given
	 * service might be provided by several users though there might be more
	 * than one service available. Use filterIDs and filter to delimit the
	 * amount of services.
	 * 
	 * @param <T>
	 *            The service type
	 * @param service
	 *            The needed remote service name. (Use yourinterface.class)
	 * @param filterIDs
	 *            User IDs work as a filter though remote services will be
	 *            limited to the given user. May be null if the service should
	 *            be get for all users.
	 * @param filter
	 *            Additional filter which checks if the service properties do
	 *            match the given filter. May be null if all services should be
	 *            found
	 * @return A list of remote service proxies.
	 * @throws ECFException
	 * @throws InvalidSyntaxException
	 */
	public synchronized <T> List<T> getRemoteService(Class<T> service,
			ID[] filterIDs, String filter) throws ECFException,
			InvalidSyntaxException {
		assert service != null : "assert  service != null";

		IRemoteServiceContainerAdapter remoteServiceContainerAdapter = getRemoteServiceContainerAdapter();

		/* 1. get available services */
		IRemoteServiceReference[] refs = remoteServiceContainerAdapter
				.getRemoteServiceReferences(filterIDs, service.getName(),
						filter);

		/* 2. get the proxies for found service references */
		List<T> remoteServices = null;
		if (refs != null) {
			remoteServices = getServiceProxies(service, refs);
		}

		return remoteServices;
	}

	protected <T> List<T> getServiceProxies(Class<T> service,
			IRemoteServiceReference[] refs) throws ECFException {
		List<T> remoteServices = new ArrayList<T>();

		for (int serviceNumber = 0; serviceNumber < refs.length; serviceNumber++) {

			IRemoteService remoteService = getRemoteServiceContainerAdapter()
					.getRemoteService(refs[serviceNumber]);

			T castedService = service.cast(remoteService.getProxy());
			remoteServices.add(castedService);
		}
		return remoteServices;
	}

	public <T> T getRemoteServiceForClient(Class<T> service, ID filterID,
			String filter) throws ECFException, InvalidSyntaxException {

		T result = null;
		List<T> remoteServices = getRemoteService(service,
				new ID[] { filterID }, filter);

		if (remoteServices != null && remoteServices.size() == 1) {
			result = remoteServices.get(0);
		}

		return result;

	}

	public IRosterManager getRosterManager() {
		IRosterManager rosterManager = this.getPresenceContainerAdapter()
				.getRosterManager();
		assert rosterManager != null : "rosterManager != null";
		return rosterManager;
	}

	public IRoster getRoster() {
		IRoster roster = getRosterManager().getRoster();
		assert roster != null : "roster != null";
		return roster;
	}

	private IContainer getContainer() {
		assert container != null : "container != null";
		return this.container;
	}

	/**
	 * Registers a service as remote service over ECF (Distributed OSGi). Rather
	 * than using this method directly one can also set the Declarative Service
	 * property "org.osgi.interfaces=*". The RemoteServiceTracker within the
	 * SessionServiceImpl class will listen for all services starting with
	 * "org.remotercp*" containing this property and register them as remote ECF
	 * services.
	 * 
	 * @param serviceName
	 *            The service name
	 * @param impl
	 *            The service implementation
	 * @param filterIDs
	 *            Buddies, who are to receive service registration. If
	 *            <code>null</code> all buddies in roster will be taken
	 */
	public synchronized void registerRemoteService(String serviceName,
			Object impl, ID[] filterIDs) {

		Dictionary<String, ID[]> props = new Hashtable<String, ID[]>();
		if (filterIDs == null) {
			filterIDs = new ID[0];
		}
		props.put(Constants.SERVICE_REGISTRATION_TARGETS, filterIDs);

		// register ECF remote service
		getRemoteServiceContainerAdapter().registerRemoteService(
				new String[] { serviceName }, impl, props);

		logger.info("Remote serivice registered: " + serviceName);
	}

	public synchronized void registerRemoteService(String serviceNames[],
			Object impl, ID[] filterIDs) {

		Dictionary<String, ID[]> props = new Hashtable<String, ID[]>();
		if (filterIDs == null) {
			filterIDs = new ID[0];
		}
		props.put(Constants.SERVICE_REGISTRATION_TARGETS, filterIDs);

		// register ECF remote service
		getRemoteServiceContainerAdapter().registerRemoteService(serviceNames,
				impl, props);

		logger.info("Remote serivice registered: " + serviceNames);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Class<T> adapter) {
		assert adapter != null : "adapter != null";
		return (T) getContainer().getAdapter(adapter);

	}

	public ID getContainerID() {
		return container.getID();
	}

	public ID getConnectedID() {
		assert container.getConnectedID() != null : "containter.getConnectedID() != null";
		return container.getConnectedID();
	}

	public String getUserName() {
		return this.userName;
	}

	public ID getUserID() {
		return xmppid;
	}

	public void disconnect() {
		getContainer().disconnect();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public <T> List<T> getRemoteServiceProxies(Class<T> clazz, ID[] filterIDs) {
		try {
			return getRemoteServiceProxies(clazz, filterIDs, null);
		} catch (InvalidSyntaxException e) {
			// This cannot happen because this method didn't specify a filter.
			throw new AssertionError();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public <T> List<T> getRemoteServiceProxies(Class<T> clazz, ID[] filterIDs,
			String filter) throws InvalidSyntaxException {
		List<T> proxies = new ArrayList<T>();
		List<IRemoteService> remoteServices =
			getRemoteServices(clazz, filterIDs, filter);
		for (IRemoteService remoteService : remoteServices) {
			try {
				T serviceProxy = clazz.cast(remoteService.getProxy());
				proxies.add(serviceProxy);
			} catch (ECFException e) {
				/* This exception is thrown if the proxy cannot be created
				 * because there is no connection to the remote service. In that
				 * case, simply do nothing. We want to return a list of only
				 * those services which are currently connected.
				 */
			}
		}
		return proxies;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<IRemoteService> getRemoteServices(Class<?> clazz,
			ID[] filterIDs, String filter) throws InvalidSyntaxException {
		List<IRemoteService> remoteServices = new ArrayList<IRemoteService>();
		IRemoteServiceContainerAdapter container =
			getRemoteServiceContainerAdapter();
		
		IRemoteServiceReference[] refs = container
				.getRemoteServiceReferences(filterIDs, clazz.getName(), filter);
		
		// If no service references are found, return an empty list.
		if (refs == null) {
			return remoteServices;
		}
		
		// For each service reference, try to get the IRemoteService interface
		// to the service and add it to the result list.
		for (IRemoteServiceReference ref : refs) {
			IRemoteService service = container.getRemoteService(ref);
			if (service != null) {
				remoteServices.add(service);
			}
		}
		return remoteServices;
	}
}
