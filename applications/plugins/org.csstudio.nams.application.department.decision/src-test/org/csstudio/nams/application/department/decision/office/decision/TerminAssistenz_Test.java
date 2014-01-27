package org.csstudio.nams.application.department.decision.office.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import junit.framework.Assert;

import org.csstudio.nams.common.DefaultExecutionService;
import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.junit.Test;

public class TerminAssistenz_Test extends
		AbstractTestObject<TimebasedFilterNotifier> {

	@Test
	public void testArbeit() throws InterruptedException {
		final TimebasedFilterNotifier terminassistenz = this
				.getNewInstanceOfClassUnderTest();
		Assert.assertFalse("terminassistenz.istAmArbeiten()", terminassistenz
				.isWorking());
		terminassistenz.startWorking();
		Assert.assertTrue("terminassistenz.istAmArbeiten()", terminassistenz
				.isWorking());
		terminassistenz.stopWorking();
		Thread.sleep(100);
		Assert.assertFalse("terminassistenz.istAmArbeiten()", terminassistenz
				.isWorking());
	}

	@Test
	public void testAssistenz() throws UnknownHostException,
			InterruptedException {
		final Inbox<Terminnotiz> assistenzEingangskorb = new DefaultDocumentBox<Terminnotiz>();
		final DefaultDocumentBox<Document> sachbearbeitersKorb = new DefaultDocumentBox<Document>();
		final Map<String, Inbox<Document>> sachbearbeiterKoerbe = new HashMap<String, Inbox<Document>>();
		sachbearbeiterKoerbe.put("test", sachbearbeitersKorb);
		final Timer timer = new Timer("TerminAssistenz");

		final Terminnotiz terminnotiz = Terminnotiz.valueOf(
				CasefileId.valueOf(InetAddress
						.getByAddress(new byte[] { 127, 0, 0, 1 }),
						new Date(42)), Milliseconds.valueOf(42), "test");

		final TimebasedFilterNotifier assistenz = new TimebasedFilterNotifier(
				new DefaultExecutionService(), assistenzEingangskorb,
				sachbearbeiterKoerbe, timer);

		assistenz.startWorking();

		assistenzEingangskorb.put(terminnotiz);

		// Warte bis der Bearbeiter fertig sein müsste...
		for (int zaehler = 0; zaehler < 3000; zaehler += 10) {
			if (sachbearbeitersKorb.istEnthalten(terminnotiz)) {
				break;
			}
			Thread.sleep(10);
		}

		assistenz.stopWorking();

		final Document ausDemKorb = sachbearbeitersKorb
				.takeDocument();
		Assert.assertNotNull(ausDemKorb);
		Assert.assertEquals(terminnotiz, ausDemKorb);
		Assert.assertTrue(terminnotiz == ausDemKorb);

		timer.cancel();
	}

	@Override
	protected TimebasedFilterNotifier getNewInstanceOfClassUnderTest() {
		final Inbox<Terminnotiz> korb = new DefaultDocumentBox<Terminnotiz>();
		final Map<String, Inbox<Document>> sachbearbeiterKoerbe = new HashMap<String, Inbox<Document>>();
		sachbearbeiterKoerbe.put("test", new DefaultDocumentBox<Document>());
		return new TimebasedFilterNotifier(new DefaultExecutionService(), korb,
				sachbearbeiterKoerbe, new Timer("TerminAssistenz"));
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected TimebasedFilterNotifier[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new TimebasedFilterNotifier[] { this.getNewInstanceOfClassUnderTest(),
				this.getNewInstanceOfClassUnderTest(),
				this.getNewInstanceOfClassUnderTest() };
	}

}
