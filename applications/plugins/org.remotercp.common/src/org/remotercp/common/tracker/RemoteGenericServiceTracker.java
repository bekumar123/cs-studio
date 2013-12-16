package org.remotercp.common.tracker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;
import org.remotercp.service.connection.session.ISessionService;

public class RemoteGenericServiceTracker<ServiceInterface> extends
		ServiceTracker implements IGenericServiceListener<ISessionService> {

	private final Class<ServiceInterface> clazz;
	private ISessionService sessionService;
	private GenericServiceTracker<ISessionService> sessionServiceTracker;
	private Map<ID, ServiceInterface> remoteServices;

	public RemoteGenericServiceTracker(BundleContext context,
			Class<ServiceInterface> clazz) {

		super(context, clazz.getName(), null);
		this.clazz = clazz;
		this.remoteServices = Collections
				.synchronizedMap(new HashMap<ID, ServiceInterface>());

		initSessionServiceTracker(context);
	}

	/* ##### START internal Tracker */
	protected void initSessionServiceTracker(BundleContext context) {
		sessionServiceTracker = new GenericServiceTracker<ISessionService>(
				context, ISessionService.class);
		sessionServiceTracker.open();
		sessionServiceTracker.addServiceListener(this);
	}

	public void bindService(ISessionService service) {
		this.sessionService = service;
	}

	public void unbindService(ISessionService service) {
		this.sessionService = null;
	}

	/* #### END internal Tracker */

	public void addServiceListener(
			IGenericServiceListener<ServiceInterface> serviceListener,
			ID[] userIDs) {
		for (ID userId : userIDs) {
			// 1. get the remote service if it's not already available in local list
			if (!this.remoteServices.containsKey(userId)) {
				// 2. get the remote service
				ServiceInterface remoteService = getRemoteService(userId);
				if (remoteService != null) {
					this.remoteServices.put(userId, remoteService);
				}
			}

			// 2. inform listener
			ServiceInterface foundService = this.remoteServices.get(userId);
			if (foundService != null) {
				serviceListener.bindService(foundService);
			}
		}
	}

	private ServiceInterface getRemoteService(ID userID) {
		assert sessionService != null : "parameter sessionService != null";
		// try to retrieve the remote Service
		try {
			ServiceInterface remoteServiceForClient = sessionService
					.getRemoteServiceForClient(clazz, userID, null);
			return remoteServiceForClient;

		} catch (ECFException e) {
			e.printStackTrace();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void close() {
		super.close();
		sessionServiceTracker.close();
	}
}
