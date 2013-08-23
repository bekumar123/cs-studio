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
package org.csstudio.dal2.epics.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import gov.aps.jca.Context;

import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.junit.Test;

/**
 * Test for the epics-based implementation of dal
 * 
 * @author jpenning
 * @since 07.09.2012
 */
public class EpicsPvAccessFactoryTest {
    
	@Test
	public void testFactory() throws DalException {
		
		Context jcaContext = mock(Context.class);
		PvAddress pvAddress = PvAddress.getValue("TestDal:ConstantPV");
		
		EpicsPvAccessFactory service = new EpicsPvAccessFactory(jcaContext);
		EpicsPvAccess<Long> pvAccess1 = (EpicsPvAccess<Long>) service.createPVAccess(pvAddress, Type.LONG);
		
		assertEquals(pvAddress, pvAccess1.getPvAddress());
		assertEquals(jcaContext, pvAccess1.getJcaContext());
		
		EpicsPvAccess<Long> pvAccess2 = (EpicsPvAccess<Long>) service.createPVAccess(pvAddress, Type.LONG);
		assertNotSame(pvAccess1, pvAccess2);
	}
	
}
