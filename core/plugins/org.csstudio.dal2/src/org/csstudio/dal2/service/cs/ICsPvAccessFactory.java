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
package org.csstudio.dal2.service.cs;

import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;

/**
 * This interface defines how an implementation of a dal plugin should provide
 * an control system specific pv access object.
 * 
 * @author jpenning, ascharping
 * @since 07.09.2012
 */
public interface ICsPvAccessFactory {

	/**
	 * Creates the control system specific PV Access for a given process
	 * variable with a specific type
	 * 
	 * @param pv
	 *            the process variable address
	 * @param type
	 *            the type to be used for the connection
	 * @return the pv access object
	 * 
	 * @require pv != null
	 * @require type != null
	 * @ensure result != null
	 */
	<T> ICsPvAccess<T> createPVAccess(PvAddress pv, Type<T> type);

	/**
	 * Asynchronous request of the native type of the given pv
	 * <p>
	 * This method does not provide any timeout detection. So the client is
	 * responsible for canceling the request using the provides operation
	 * handle.
	 * 
	 * @param pv
	 *            the process variable address
	 * @param callback
	 *            the callback to provide the result
	 * @return an operation handle that allows to cancel the request
	 * @throws DalException
	 * 
	 * @require pv != null
	 * @require callback != null
	 * @ensure result != null
	 */
	ICsOperationHandle requestNativeType(PvAddress pv,
			ICsResponseListener<Type<?>> callback) throws DalException;
}
