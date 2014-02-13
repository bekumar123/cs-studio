/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.dal2.service;

import java.util.concurrent.TimeUnit;

import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;


/**
 * Generic interface to different control system access implementations 
 * This service is considered to have state, so it may not be used by different clients.
 * To get access to one of the stateful services, use the IDalServiceFactory.
 * 
 * @author jpenning, ascharping
 * @since 06.09.2012
 */
public interface IDalService {
    
	long DEFAULT_TIMEOUT = 5;
	
	/**
	 * Provides a pv access object for the given pv, type and listener type
	 * 
	 * @param address address of the pv
	 * @param type the type to be used for requesting values or create listener
	 * @param listenerType the listener type provided by the pv acccess
	 * 
	 * @return the IPvAccess object
	 * 
	 * @require address != null
	 * @require type != null
	 * @require listenerType != null
	 * @ensure result != null
	 */
	<T> IPvAccess<T> getPVAccess(PvAddress address, Type<T> type, ListenerType listenerType);

	/**
	 * Provides a pv access object for the given pv and type
	 * <p>
	 * {@link ListenerType#VALUE} is used as listener type
	 * 
	 * @param address address of the pv
	 * @param type the type to be used for requesting values or create listener
	 * 
	 * @return the IPvAccess object
	 * 
	 * @require address != null
	 * @require type != null
	 * @ensure result != null
	 */
	<T> IPvAccess<T> getPVAccess(PvAddress address, Type<T> type);
	
	/**
	 * Provides the native type (type configured in the control system) of the given pv.
	 * <p>
	 * Uses the default timeout of {@value #DEFAULT_TIMEOUT}s
	 * <p>
	 * see {@link #getNativeType(PvAddress, long, TimeUnit)}
	 * 
	 * @require address != null
	 */
	Type<?> getNativeType(PvAddress address) throws DalException;

	/**
	 * Provides the native type (type configured in the control system) of the given pv.
	 * 
	 * @param address address of the pv
	 * @param timeout the timeout used for the request
	 * @param timeoutUnit the time unit used with the timeout value
	 * 
	 * @return the native type 
	 * @throws DalException
	 * 
	 * @require address != null
	 * @require timeoutUnit != null
	 * @require timeout > 0
	 */
	Type<?> getNativeType(PvAddress address, long timeout, TimeUnit timeoutUnit) throws DalException;

	/**
	 * Removes all Pv Access objects from local cache and disposes all pc access objects
	 */
	void disposeAll();

	/**
	 * Removes the given PvAccess object from cache and disposes all monitors created by this access object.
	 */
	void dispose(IPvAccess<?> pvAccess);
}
