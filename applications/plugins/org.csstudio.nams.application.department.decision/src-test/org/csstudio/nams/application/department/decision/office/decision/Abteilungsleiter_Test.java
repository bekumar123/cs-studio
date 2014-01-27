/* 
 * Copyright (c) 2008 C1 WPS mbH, 
 * HAMBURG, GERMANY.
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
package org.csstudio.nams.application.department.decision.office.decision;

import java.net.InetAddress;
import java.util.Date;

import junit.framework.Assert;

import org.csstudio.nams.common.DefaultExecutionService;
import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Test;

public class Abteilungsleiter_Test extends
		AbstractTestObject<ThreadedMessageDispatcher> {

	protected volatile int anzahlDerSachbearbeiterDieEineMappeErhaltenHaben;
	protected Throwable testFailedError;

	@SuppressWarnings("unchecked")
	@Test(timeout = 4000)
	public void testAbteilungsleiter() throws Throwable {
		final MessageCasefile vorgangsmappe = new MessageCasefile(
				CasefileId.valueOf(InetAddress
						.getByAddress(new byte[] { 127, 0, 0, 1 }), new Date(
						123)), new AlarmMessage("Test-Nachricht"));
		this.anzahlDerSachbearbeiterDieEineMappeErhaltenHaben = 0;

		final Inbox<MessageCasefile> eingangskorb = EasyMock
				.createMock(Inbox.class);
		EasyMock.expect(eingangskorb.takeDocument()).andReturn(
				vorgangsmappe).times(1).andStubAnswer(
				new IAnswer<MessageCasefile>() {
					public MessageCasefile answer() throws Throwable {
						Thread.sleep(Integer.MAX_VALUE);
						Assert.fail();
						return null;
					}
				});
		
		
		Inbox<Document> korbMock = EasyMock.createMock(Inbox.class);
		Capture<Document> ablegenArguments = new Capture<Document>();
		korbMock.put(EasyMock.<Document> capture(ablegenArguments));
		EasyMock.expectLastCall().once();
		EasyMock.replay(korbMock);
		
		final Inbox<Document> sachbearbeiterkorb1 = createEingangskorbMock(vorgangsmappe);
		// Da die Exemplare nicht unterschieden werden, ist dieses derzeit möglich!
		final Inbox<Document> sachbearbeiterkorb2 = createEingangskorbMock(vorgangsmappe); 

		EasyMock.replay(eingangskorb);
		this.testFailedError = null;

		final ThreadedMessageDispatcher abteilungsleiter = new ThreadedMessageDispatcher(
				new DefaultExecutionService(), eingangskorb);
		abteilungsleiter.addWorkerInbox(korbMock);

		abteilungsleiter.startWorking();

		// Der Mock simuliert jetzt folgendes:
		// eingangskorb.ablegen(vorgangsmappe);

		// Warte bis der Bearbeiter fertig sein müsste...
		for (int wartezeit = 0; wartezeit < 3000; wartezeit += 10) {
			if (this.anzahlDerSachbearbeiterDieEineMappeErhaltenHaben > 1) {
				break;
			}
			Thread.sleep(10);
		}

		abteilungsleiter.stopWorking();

		if (this.testFailedError != null) {
			throw this.testFailedError;
		}
		EasyMock.verify(eingangskorb);
		
		EasyMock.verify(korbMock);
		MessageCasefile dokument = (MessageCasefile) ablegenArguments.getValue();
		Assert.assertNotSame("Vorgangsmappen nicht identisch",
				vorgangsmappe, dokument);
		Assert.assertFalse("Vorgangsmappen nicht gleich",
				vorgangsmappe.equals(dokument));

		Assert.assertNotSame("Vorgangsmappen.Alarmnachricht nicht identisch",
						vorgangsmappe.getAlarmNachricht(),
						dokument.getAlarmNachricht());
		Assert.assertEquals("Vorgangsmappen.Alarmnachrichten bleiben in diesem Büro gleich",
						vorgangsmappe.getAlarmNachricht(),
						dokument.getAlarmNachricht());
		
		

	}

	private Inbox<Document> createEingangskorbMock(final MessageCasefile vorgangsmappe) {
		return new Inbox<Document>() {
			public void put(Document ablagefaehig) {
				try {
					MessageCasefile dokument = (MessageCasefile) ablagefaehig;
					Assert.assertNotSame("Vorgangsmappen nicht identisch",
							vorgangsmappe, dokument);
					Assert.assertFalse("Vorgangsmappen nicht gleich",
							vorgangsmappe.equals(dokument));

					Assert.assertNotSame("Vorgangsmappen.Alarmnachricht nicht identisch",
									vorgangsmappe.getAlarmNachricht(),
									dokument.getAlarmNachricht());
					Assert.assertEquals("Vorgangsmappen.Alarmnachrichten bleiben in diesem Büro gleich",
									vorgangsmappe.getAlarmNachricht(),
									dokument.getAlarmNachricht());

					Abteilungsleiter_Test.this.anzahlDerSachbearbeiterDieEineMappeErhaltenHaben++;
				} catch (Throwable throwable) {
					Abteilungsleiter_Test.this.testFailedError = throwable;
				}
			}

			public MessageCasefile take()
					throws InterruptedException {
				Assert.fail("not to be called!");
				return null;
			}
		};
	}

	@Test
	public void testArbeit() throws InterruptedException {
		final ThreadedMessageDispatcher abteilungsleiter = this
				.getNewInstanceOfClassUnderTest();
		Assert.assertFalse("abteilungsleiter.istAmArbeiten()", abteilungsleiter
				.isWorking());
		abteilungsleiter.startWorking();
		Assert.assertTrue("abteilungsleiter.istAmArbeiten()", abteilungsleiter
				.isWorking());
		abteilungsleiter.stopWorking();
		Thread.sleep(100);
		Assert.assertFalse("abteilungsleiter.istAmArbeiten()", abteilungsleiter
				.isWorking());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ThreadedMessageDispatcher getNewInstanceOfClassUnderTest() {
		return new ThreadedMessageDispatcher(new DefaultExecutionService(),
				new DefaultDocumentBox<MessageCasefile>());
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ThreadedMessageDispatcher[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new ThreadedMessageDispatcher[] {
				new ThreadedMessageDispatcher(new DefaultExecutionService(),
						new DefaultDocumentBox<MessageCasefile>()),
				new ThreadedMessageDispatcher(new DefaultExecutionService(),
						new DefaultDocumentBox<MessageCasefile>()),
				new ThreadedMessageDispatcher(new DefaultExecutionService(),
						new DefaultDocumentBox<MessageCasefile>()) };
	}

}
