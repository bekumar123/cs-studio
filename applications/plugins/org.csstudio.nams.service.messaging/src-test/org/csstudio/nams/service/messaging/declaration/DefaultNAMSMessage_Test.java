package org.csstudio.nams.service.messaging.declaration;

import junit.framework.Assert;

import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.common.material.SynchronisationsAufforderungsSystemNachchricht;
import org.csstudio.nams.common.material.SynchronisationsBestaetigungSystemNachricht;
import org.csstudio.nams.common.material.SystemMessage;
import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.csstudio.nams.service.messaging.declaration.DefaultNAMSMessage.AcknowledgeHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultNAMSMessage_Test extends
		AbstractTestObject<NAMSMessage> {

	protected boolean acknowledged;

	@Override
	@Before
	public void setUp() throws Exception {
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStdImplementation() {
		// Bestaetigungsnachricht erstellen
		NAMSMessage msg = new DefaultNAMSMessage(
				new SynchronisationsBestaetigungSystemNachricht(),
				new AcknowledgeHandler() {
					public void acknowledge() throws Throwable {
						DefaultNAMSMessage_Test.this.acknowledged = true;
					}
				});

		Assert.assertTrue(msg.enthaeltSystemnachricht());
		Assert.assertFalse(msg.enthaeltAlarmnachricht());
		SystemMessage systemNachricht = msg.alsSystemachricht();
		Assert.assertTrue(systemNachricht.isSynchronizationConfirmation());
		Assert.assertFalse(systemNachricht.isSynchronizationRequest());

		// Aufforderungsnachricht erstellen
		msg = new DefaultNAMSMessage(
				new SynchronisationsAufforderungsSystemNachchricht(),
				new AcknowledgeHandler() {
					public void acknowledge() throws Throwable {
						DefaultNAMSMessage_Test.this.acknowledged = true;
					}
				});

		Assert.assertTrue(msg.enthaeltSystemnachricht());
		Assert.assertFalse(msg.enthaeltAlarmnachricht());
		systemNachricht = msg.alsSystemachricht();
		Assert.assertFalse(systemNachricht.isSynchronizationConfirmation());
		Assert.assertTrue(systemNachricht.isSynchronizationRequest());
	}

	@Override
	protected NAMSMessage getNewInstanceOfClassUnderTest() {
		return new DefaultNAMSMessage(
				new DefaultNAMSMessage.AcknowledgeHandler() {
					public void acknowledge() throws Throwable {
					}
				});
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected NAMSMessage[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		final NAMSMessage[] result = new NAMSMessage[3];

		result[0] = new DefaultNAMSMessage(
				new DefaultNAMSMessage.AcknowledgeHandler() {
					public void acknowledge() throws Throwable {
					}
				});
		result[1] = new DefaultNAMSMessage(
				new SynchronisationsAufforderungsSystemNachchricht(),
				new DefaultNAMSMessage.AcknowledgeHandler() {
					public void acknowledge() throws Throwable {
					}
				});
		result[2] = new DefaultNAMSMessage(new AlarmMessage("Test"),
				new DefaultNAMSMessage.AcknowledgeHandler() {
					public void acknowledge() throws Throwable {
					}
				});

		return result;
	}
}
