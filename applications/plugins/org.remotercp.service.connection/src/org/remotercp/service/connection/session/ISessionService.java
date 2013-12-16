package org.remotercp.service.connection.session;

import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.osgi.framework.InvalidSyntaxException;

public interface ISessionService {

	/**
	 * Connects to an XMPP-Server with the provided credentials
	 * 
	 * @throws ECFException
	 */
	public void connect(String userName, String password, String server)
			throws URISyntaxException, ECFException;

	public IRosterManager getRosterManager();

	public IRoster getRoster();

	public ID getContainerID();

	public ID getConnectedID();

	public <T> T getAdapter(Class<T> adapter);

	/**
	 * Registers a service as remote service for OSGi over ECF
	 * 
	 * @param classType
	 *            The service name
	 * @param impl
	 * @param targetIDs
	 *            Buddies, who are to receive service registration. If
	 *            <code>null</code> all buddies in roster will be taken
	 */
	public void registerRemoteService(String serviceName, Object impl,
			ID[] targetIDs);

	public void registerRemoteService(String serviceNames[], Object impl,
			ID[] filterIDs);

	/**
	 * Returns a list of remote service references for a given service. The
	 * given service might me provided by several user though there might be
	 * more than one service available
	 * 
	 * @param <T>
	 *            The service type
	 * @param service
	 *            The needed remote service name
	 * @param filterIDs
	 *            User IDs work as a filter though remote services will be
	 *            limited to the given user. May be null if the service should
	 *            be get for all users.
	 * @param filter
	 *            Additional filter which checks if the service properties do
	 *            match the given filer. May be null if all services should be
	 *            found
	 * @return A list of remote service proxies
	 * @throws ECFException
	 * @throws InvalidSyntaxException
	 */
	public <T> List<T> getRemoteService(Class<T> service, ID[] filterIDs,
			String filter) throws ECFException, InvalidSyntaxException;

	public <T> T getRemoteServiceForClient(Class<T> service, ID filterID,
			String filter) throws ECFException, InvalidSyntaxException;

	/**
	 * Returns the currently logged-in user.
	 * 
	 * @return
	 */
	public String getUserName();

	public ID getUserID();

	// only for tests needed
	public IRemoteServiceContainerAdapter getRemoteServiceContainerAdapter();

	/**
	 * Disconnects a client from the XMPP server
	 */
	public void disconnect();
	
	/**
	 * <p>
	 * Returns a list of remote service proxies for services of the specified
	 * class. Use <code>filterIDs</code> to specify the remote containers from
	 * which to get the services.
	 * </p>
	 * <p>
	 * Calling this method will return the same result as calling
	 * <code>getRemoteServiceProxies(clazz, filterIDs, null)</code>, but this
	 * method does not throw an <code>InvalidSyntaxException</code>, so it is
	 * more convenient to use by callers that don't need the additional filter
	 * string.
	 * </p>
	 * 
	 * @param <T>
	 *            The service type.
	 * @param clazz
	 *            The class of the remote service.
	 * @param filterIDs
	 *            The IDs of the containers from which to get the remote
	 *            services. If this is <code>null</code>, get services from all
	 *            remote containers.
	 * @return A list of remote service proxies.
	 * @see #getRemoteServiceProxies(Class, ID[], String)
	 */
	public <T> List<T> getRemoteServiceProxies(Class<T> clazz, ID[] filterIDs);
}
