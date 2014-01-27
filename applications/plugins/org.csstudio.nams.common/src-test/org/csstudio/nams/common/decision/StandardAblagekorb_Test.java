package org.csstudio.nams.common.decision;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;

public class StandardAblagekorb_Test
		extends
		AbstractTestAblagekorb<AblagefaehigesObject, DefaultDocumentBox<AblagefaehigesObject>> {

	volatile int fertigeConsumer = 0;
	volatile private DefaultDocumentBox<Document> korb;

	@Test
	public void testIterator() throws InterruptedException {
		final DefaultDocumentBox<Document> korb = new DefaultDocumentBox<Document>();

		korb.put(new AblagefaehigesObject());
		korb.put(new AblagefaehigesObject());
		korb.put(new AblagefaehigesObject());

		final Iterator<Document> iterator = korb.iterator();
		int anzahl = 0;
		while (iterator.hasNext()) {
			final Document ablagefaehig = iterator.next();
			Assert.assertNotNull(ablagefaehig);
			iterator.remove();
			anzahl++;
		}
		Assert.assertEquals(3, anzahl);
		anzahl = 0;
		while (iterator.hasNext()) {
			final Document ablagefaehig = iterator.next();
			Assert.assertNotNull(ablagefaehig);
			anzahl++;
		}
		Assert.assertEquals(0, anzahl);
	}

	@Test
	public void testIteratorNebenlaeufig() throws InterruptedException {
		final DefaultDocumentBox<Document> korb = new DefaultDocumentBox<Document>();

		class Producer implements Runnable {
			public void run() {
				int i = 0;
				while (i < 1000) {
					try {
						korb.put(new AblagefaehigesObject());
					} catch (final InterruptedException ex) {
						Assert.fail();
					}
					i++;
					Thread.yield();
				}
			}
		}

		class IteratorConsumer implements Runnable {
			public void run() {
				try {
					Thread.sleep(100);
				} catch (final InterruptedException e) {
					Assert.fail(e.getMessage());
				}
				final Iterator<Document> iterator = korb.iterator();
				int anzahl = 0;
				while (iterator.hasNext()) {
					final Document ablagefaehig = iterator.next();
					Assert.assertNotNull(ablagefaehig);
					iterator.remove();
					anzahl++;
				}
				Assert.assertTrue(anzahl > 0);
			}
		}
		;

		new Thread(new IteratorConsumer()).start();
		new Thread(new Producer()).start();
	}

	@Test(timeout = 4000)
	public void testMassigAblegenUndEntnehmen() {
		this.korb = new DefaultDocumentBox<Document>();
		final DefaultDocumentBox<Document> korb2 = new DefaultDocumentBox<Document>();

		class Producer implements Runnable {
			public void run() {
				int i = 0;
				while (i < 100) {
					try {
						StandardAblagekorb_Test.this.korb
								.put(new AblagefaehigesObject());
						// System.out.println("Producer.run()");
					} catch (final InterruptedException ex) {
						Assert.fail();
					}
					i++;
					Thread.yield();
				}
			}
		}
		class Consumer1 implements Runnable {
			// private final String name;

			public Consumer1(final String name) {
				// this.name = name;
			}

			public void run() {
				int i = 0;
				while (i < 100) {
					Document eingang = null;
					try {
						eingang = StandardAblagekorb_Test.this.korb
								.takeDocument();
						// System.out.println("Consumer1.run()" + name);
					} catch (final InterruptedException ex) {
					}

					Assert.assertNotNull(eingang);

					try {
						korb2.put(eingang);
					} catch (final InterruptedException ex) {
					}

					i++;
					Thread.yield();
				}
				StandardAblagekorb_Test.this.fertigeConsumer++;
			}
		}
		class Consumer2 implements Runnable {
			// private final String name;

			public Consumer2(final String name) {
				// this.name = name;
			}

			public void run() {
				try {
					int i = 0;
					while (i < 100) {
						Document eingang = null;
						eingang = korb2.takeDocument();
						Assert.assertNotNull(eingang);
						// System.out.println("Consumer2.run(): " + name);

						i++;
						Thread.yield();
					}
					StandardAblagekorb_Test.this.fertigeConsumer++;
				} catch (final InterruptedException ex) {
				}
			}
		}

		final Producer p = new Producer();
		final Consumer2 c2 = new Consumer2("B");
		final Consumer1 c1 = new Consumer1("A");
		final Thread ct2 = new Thread(c2);
		final Thread ct1 = new Thread(c1);

		// try {
		// Thread.sleep(100);
		// } catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		Thread.yield();

		ct2.start();
		ct1.start();

		new Thread(p).start();
		while (this.fertigeConsumer < 2) {
			Thread.yield();
		}
	}

	@Override
	protected DefaultDocumentBox<AblagefaehigesObject> getNewInstanceOfClassUnderTest() {
		return new DefaultDocumentBox<AblagefaehigesObject>();
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected DefaultDocumentBox<AblagefaehigesObject>[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		// TODO Auto-generated method stub
		return new DefaultDocumentBox[] {
				new DefaultDocumentBox<AblagefaehigesObject>(),
				new DefaultDocumentBox<AblagefaehigesObject>(),
				new DefaultDocumentBox<AblagefaehigesObject>() };
	}

	@Override
	protected AblagefaehigesObject gibNeuesAblagefaehigesExemplar() {
		return new AblagefaehigesObject();
	}

	@Override
	protected Box<AblagefaehigesObject> gibNeuesExemplar() {
		return new DefaultDocumentBox<AblagefaehigesObject>();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean pruefeObEnthalten(
			final Box<AblagefaehigesObject> korb,
			final AblagefaehigesObject element) {
		return ((DefaultDocumentBox) korb).istEnthalten(element);
	}

}
