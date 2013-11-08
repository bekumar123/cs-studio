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
package org.csstudio.persister.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.csstudio.persister.declaration.IPersistableService;
import org.csstudio.persister.declaration.IPersistenceService;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

/**
 * Test of the persistence service
 * 
 * @author jpenning
 * @since 30.03.2012
 */
public class PersistenceServiceUnitTest {
	private static final String FILENAME = "test.ser";

	private IPersistableService _persistableService;

	@Before
	public void setUp() throws Exception {
		_persistableService = mock(IPersistableService.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInitNeedsService() throws Exception {
		@SuppressWarnings("unused")
		IPersistenceService serviceUnderTest = new PersistenceService().init(
				null, FILENAME);
	}

	@Test(expected = IllegalStateException.class)
	public void testInitMayNotBeCalledTwice() throws Exception {
		IPersistenceService serviceUnderTest = new PersistenceService().init(
				_persistableService, FILENAME);
		serviceUnderTest.init(_persistableService, FILENAME);
	}

	@Test
	public void testSaveAndRead() throws Exception {
		when(_persistableService.getMemento()).thenReturn("Serializable");

		PersistenceService serviceUnderTest = new PersistenceService();
		serviceUnderTest.init(_persistableService, FILENAME);
		serviceUnderTest.saveMemento();

		serviceUnderTest.restoreMemento();
		verify(_persistableService).restoreMemento("Serializable");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSaveWithException() throws Exception {

		List data = new ArrayList();
		List data2 = new ArrayList();
		for (int i = 0; i < 10; i++) {
			data.add("Object_" + i);
			data2.add("Object2_" + i);
		}

		// add "defect" object to data2 to simulate error or system exit during
		// write operation
		data2.add(new Serializable() {

			private static final long serialVersionUID = 1L;

			private void writeObject(java.io.ObjectOutputStream out)
					throws IOException {

				// ensure file to be written partially
				out.flush();

				// stop writing files
				throw new TestException();
			}
		});

		PersistenceService serviceUnderTest = new PersistenceService();
		serviceUnderTest.init(_persistableService, FILENAME);

		// start with successful write of data
		when(_persistableService.getMemento()).thenReturn((Serializable) data);
		serviceUnderTest.saveMemento();

		// try to restore data
		serviceUnderTest.restoreMemento();
		verify(_persistableService).restoreMemento(data);

		Mockito.reset(_persistableService);

		// switch to new state (data2), perform save and expect exception during
		// save
		when(_persistableService.getMemento()).thenReturn((Serializable) data2);
		try {
			serviceUnderTest.saveMemento();
			Assert.fail("Exception expected");
		} catch (TestException e) {
			// Expected exception
		}

		// restore and expect state of last succesful write (data)
		serviceUnderTest.restoreMemento();
		verify(_persistableService).restoreMemento(data);

		Mockito.reset(_persistableService);

		// replace defect object and try save again
		data2.set(10, "New Object");
		when(_persistableService.getMemento()).thenReturn((Serializable) data2);
		serviceUnderTest.saveMemento();

		// try to restore data
		serviceUnderTest.restoreMemento();
		verify(_persistableService).restoreMemento(data2);
	}

	@SuppressWarnings("serial")
	private class TestException extends RuntimeException {
	}

	@Test
	public void testSaveAndReadMany() throws Exception {
		final int COUNT = 100000; // ca. 800 kByte

		double[] data = new double[COUNT];
		for (int i = 0; i < data.length; i++) {
			data[i] = i;
		}
		when(_persistableService.getMemento()).thenReturn(data);

		PersistenceService serviceUnderTest = new PersistenceService();
		serviceUnderTest.init(_persistableService, FILENAME);
		serviceUnderTest.saveMemento();

		// we only want to get hold of the internal data, there is probably a
		// more suitable method
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				double[] storedData = (double[]) args[0];
				for (int i = 0; i < COUNT; i++) {
					assertEquals(i, storedData[i], 0.01);
				}
				return null;
			}
		}).when(_persistableService).restoreMemento(data);

		serviceUnderTest.restoreMemento();
	}

	@Test
	public void testExecutor() throws Exception {
		PersistenceService serviceUnderTest = new PersistenceService();
		serviceUnderTest.init(_persistableService, FILENAME);
		serviceUnderTest.runPersister(1);

		checkMemento(serviceUnderTest, "Blabla 1");
		checkMemento(serviceUnderTest, "Blabla 2");
		checkMemento(serviceUnderTest, "Blabla 3");
		checkMemento(serviceUnderTest, "Blabla 4");
	}

	private void checkMemento(PersistenceService serviceUnderTest, String value)
			throws InterruptedException, IOException, ClassNotFoundException {
		when(_persistableService.getMemento()).thenReturn(value);
		Thread.sleep(1500);
		serviceUnderTest.restoreMemento();
		verify(_persistableService).restoreMemento(value);
	}

	@AfterClass
	public void tearDown() {
		new File(FILENAME).delete();
		new File(FILENAME + ".old").delete();
	}
	
	@SuppressWarnings("serial")
	public static void main(String[] args) throws IOException,
			ClassNotFoundException {

		System.out.println("Choose mode by entering numer");
		System.out.println(" 1: Save normally");
		System.out
				.println(" 2: Save with System.exit() during save to simulate a crash");
		System.out.println(" 3: Restore");

		IPersistableService persistableService = mock(IPersistableService.class);
		PersistenceService serviceUnderTest = new PersistenceService();
		serviceUnderTest.init(persistableService, FILENAME);

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		String line = reader.readLine().trim();
		if ("1".equals(line)) {
			Serializable[] data = new Serializable[10];
			for (int i = 0; i < data.length; i++) {
				data[i] = "Object_State_A_" + i;
			}
			when(persistableService.getMemento()).thenReturn(data);

			System.out.println("Try to save data: " + Arrays.toString(data));
			serviceUnderTest.saveMemento();
			System.out.println("Done");
		} else if ("2".equals(line)) {
			Serializable[] data = new Serializable[10];
			for (int i = 0; i < data.length; i++) {
				data[i] = "Object_State_B_" + i;
			}
			data[6] = new Serializable() {
				private void writeObject(java.io.ObjectOutputStream out)
						throws IOException {
					// ensure file to be written partially
					out.flush();
					// simulate crash
					System.err.println("Simuate crash ...");
					System.err.flush();
					System.exit(1);
				}

				@Override
				public String toString() {
					return "trigger for crash";
				}
			};
			when(persistableService.getMemento()).thenReturn(data);
			System.out.println("Try to save data with simuation of crash "
					+ Arrays.toString(data));
			serviceUnderTest.saveMemento();
			System.out.println("Done");
		} else if ("3".equals(line)) {
			System.out.println("Try to restore data");
			serviceUnderTest.restoreMemento();

			ArgumentCaptor<Object> captor = ArgumentCaptor
					.forClass(Object.class);
			verify(persistableService).restoreMemento(captor.capture());
			System.out.println("Done: " + Arrays.toString((Serializable[])captor.getValue()));
		}

	}

}
