package org.csstudio.nams.application.department.decision.office.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.nams.common.DefaultExecutionService;
import org.csstudio.nams.common.decision.Arbeitsfaehig;
import org.csstudio.nams.common.decision.Eingangskorb;
import org.csstudio.nams.common.decision.StandardAblagekorb;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung;
import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.OderVersandRegel;
import org.csstudio.nams.common.material.regelwerk.Pruefliste;
import org.csstudio.nams.common.material.regelwerk.RegelErgebnis;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.StandardRegelwerk;
import org.csstudio.nams.common.material.regelwerk.TimeBasedRegel;
import org.csstudio.nams.common.material.regelwerk.VersandRegel;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.common.material.regelwerk.yaams.DefaultRegelwerk;
import org.csstudio.nams.common.material.regelwerk.yaams.NewRegelwerk;
import org.csstudio.nams.common.material.regelwerk.yaams.Regel;
import org.csstudio.nams.common.material.regelwerk.yaams.TimebasedRegelwerk;
import org.csstudio.nams.common.material.regelwerk.yaams.TimebasedRegelwerk.TimeoutType;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.junit.Test;

public class AlarmEntscheidungsBuero_Test extends TestCase {

	static class SehrSimpleTextRegel implements VersandRegel {

		private final String muster;

		public SehrSimpleTextRegel(final String muster) {
			this.muster = muster;
		}

		public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
				final AlarmNachricht nachricht,
				final Pruefliste bisherigesErgebnis) {
			if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
				this.pruefeNachrichtErstmalig(nachricht, bisherigesErgebnis);
			}
		}

		public Millisekunden pruefeNachrichtAufTimeOuts(
				final Pruefliste bisherigesErgebnis,
				final Millisekunden verstricheneZeitSeitErsterPruefung) {
			return null;
		}

		public Millisekunden pruefeNachrichtErstmalig(
				final AlarmNachricht nachricht,
				final Pruefliste bisherigesErgebnis) {
			if (this.muster.equals(nachricht.gibNachrichtenText())) {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.ZUTREFFEND);
			} else {
				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
						RegelErgebnis.NICHT_ZUTREFFEND);
			}
			return null;
		}

		public void setHistoryService(final HistoryService historyService) {
			// TODO Auto-generated method stub

		}

	}

	public void testConstructor() {
		final int ANZAHL_REGELWERKE = 3;

		final NewRegelwerk[] regelwerke = new NewRegelwerk[ANZAHL_REGELWERKE];
		for (int i = 0; i < ANZAHL_REGELWERKE; i++) {
			regelwerke[i] = new DefaultRegelwerk(Regelwerkskennung.valueOf(),
					new Regel() {
						// Impl hier egal!
						@Override
						public boolean pruefeNachricht(AlarmNachricht nachricht) {
							// TODO Auto-generated method stub
							return false;
						}
						@Override
						public boolean pruefeNachricht(
								AlarmNachricht nachricht,
								AlarmNachricht vergleichsNachricht) {
							// TODO Auto-generated method stub
							return false;
						}
					});
		}

		final AlarmEntscheidungsBuero buero = new AlarmEntscheidungsBuero(
				new DefaultExecutionService(), regelwerke,
				new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb<Vorgangsmappe>(), 10);

		Assert.assertNotNull(buero.gibAbteilungsleiterFuerTest());
		Assert.assertNotNull(buero.gibAssistenzFuerTest());
		final Collection<Arbeitsfaehig> listOfSachbearbeiter = buero
				.gibListeDerSachbearbeiterFuerTest();
		Assert.assertNotNull(listOfSachbearbeiter);
		Assert.assertTrue("listOfSachbearbeiter.size()==" + ANZAHL_REGELWERKE,
				listOfSachbearbeiter.size() == ANZAHL_REGELWERKE);

		Assert.assertTrue("buero.getAbteilungsleiter().istAmArbeiten()", buero
				.gibAbteilungsleiterFuerTest().istAmArbeiten());
		Assert.assertTrue("buero.getAssistenz().istAmArbeiten()", buero
				.gibAssistenzFuerTest().istAmArbeiten());
		for (final Arbeitsfaehig bearbeiter : listOfSachbearbeiter) {
			Assert.assertTrue(
					"buero.getListOfSachbearbeiter().istAmArbeiten()",
					bearbeiter.istAmArbeiten());
		}
	}

	public void testIntegration() throws InterruptedException,
			UnknownHostException {
		final Regel regel = new Regel() {
						@Override
			public boolean pruefeNachricht(AlarmNachricht nachricht) {
				return true;
			}
			@Override
			public boolean pruefeNachricht(AlarmNachricht nachricht,
					AlarmNachricht vergleichsNachricht) {
				Assert.fail();
				return false;
			}
		};
		
		final Regel regel2 = new Regel() {
			@Override
			public boolean pruefeNachricht(AlarmNachricht nachricht) {
				return false;
			}

			@Override
			public boolean pruefeNachricht(AlarmNachricht nachricht,
					AlarmNachricht vergleichsNachricht) {
				Assert.fail();
				return false;
			}
		};
		
		final Regelwerkskennung regelwerkskennung = Regelwerkskennung.valueOf();
		final NewRegelwerk regelwerk = new DefaultRegelwerk(regelwerkskennung,
				regel);

		final Regelwerkskennung regelwerkskennung2 = Regelwerkskennung
				.valueOf();
		final NewRegelwerk regelwerk2 = new DefaultRegelwerk(regelwerkskennung2,
				regel2);

		final AlarmEntscheidungsBuero buero = new AlarmEntscheidungsBuero(
				new DefaultExecutionService(), new NewRegelwerk[] { regelwerk,
						regelwerk2 },
				new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb<Vorgangsmappe>(), 1);
		
		final Eingangskorb<Vorgangsmappe> alarmVorgangEingangskorb = buero
				.gibAlarmVorgangEingangskorb();
		final StandardAblagekorb<Vorgangsmappe> alarmVorgangAusgangskorb = (StandardAblagekorb<Vorgangsmappe>) buero
				.gibAlarmVorgangAusgangskorb();

		final Vorgangsmappenkennung vorgangsmappenkennung = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		final AlarmNachricht alarmNachricht = new AlarmNachricht(
				"test nachricht");
		final Vorgangsmappe vorgangsmappe = new Vorgangsmappe(
				vorgangsmappenkennung, alarmNachricht);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe);

		Vorgangsmappe aelteste = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		Assert.assertEquals(alarmNachricht, aelteste.getAlarmNachricht());
		Assert.assertTrue(aelteste.getWeiteresVersandVorgehen() == WeiteresVersandVorgehen.VERSENDEN);
		Assert.assertTrue(aelteste.getBearbeitetMitRegelWerk() == regelwerk.getRegelwerksKennung());

		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		Assert.assertEquals(alarmNachricht, aelteste.getAlarmNachricht());
		Assert.assertTrue(aelteste.getWeiteresVersandVorgehen() == WeiteresVersandVorgehen.NICHT_VERSENDEN);
		Assert.assertTrue(aelteste.getBearbeitetMitRegelWerk() == regelwerk2.getRegelwerksKennung());		
	}

	@Test(timeout = 4000)
	public void testTimeBasedAufhebenBeiTimeout() throws Throwable {
		Regel startRegel = new Regel() {
			@Override
			public boolean pruefeNachricht(AlarmNachricht nachricht,
					AlarmNachricht vergleichsNachricht) {
				Assert.fail();
				return false;
			}
			@Override
			public boolean pruefeNachricht(AlarmNachricht nachricht) {
				return nachricht.gibNachrichtenText().equals("START");
			}
		};
		Regel stopRegel = new Regel() {
			@Override
			public boolean pruefeNachricht(AlarmNachricht nachricht,
					AlarmNachricht vergleichsNachricht) {
				return pruefeNachricht(nachricht);
			}
			@Override
			public boolean pruefeNachricht(AlarmNachricht nachricht) {
				return nachricht.gibNachrichtenText().equals("STOP");
			}
		};
		TimebasedRegelwerk timebasedRegelwerk = new TimebasedRegelwerk(Regelwerkskennung.valueOf(), startRegel, stopRegel, Millisekunden.valueOf(100), TimeoutType.SENDE_BEI_STOP_REGEL);

		final AlarmEntscheidungsBuero buero = new AlarmEntscheidungsBuero(
				new DefaultExecutionService(), new NewRegelwerk[] { timebasedRegelwerk },
				new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb<Vorgangsmappe>(), 1);
		final Eingangskorb<Vorgangsmappe> alarmVorgangEingangskorb = buero
				.gibAlarmVorgangEingangskorb();
		final StandardAblagekorb<Vorgangsmappe> alarmVorgangAusgangskorb = (StandardAblagekorb<Vorgangsmappe>) buero
				.gibAlarmVorgangAusgangskorb();

		// Un-Passende 1
		Vorgangsmappenkennung vorgangsmappenkennung = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht = new AlarmNachricht("XXO");
		Vorgangsmappe vorgangsmappe = new Vorgangsmappe(
				vorgangsmappenkennung, alarmNachricht);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe);

		// Passende 1
		Vorgangsmappenkennung vorgangsmappenkennung2 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht2 = new AlarmNachricht("START");
		Vorgangsmappe vorgangsmappe2 = new Vorgangsmappe(
				vorgangsmappenkennung2, alarmNachricht2);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe2);

		// Un-Passende 1
		Vorgangsmappenkennung vorgangsmappenkennung3 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht3 = new AlarmNachricht("Baeh!");
		Vorgangsmappe vorgangsmappe3 = new Vorgangsmappe(
				vorgangsmappenkennung3, alarmNachricht3);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe3);

		// Passende Bestaetigung1
		Vorgangsmappenkennung vorgangsmappenkennung4 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht4 = new AlarmNachricht("STOP");
		Vorgangsmappe vorgangsmappe4 = new Vorgangsmappe(
				vorgangsmappenkennung4, alarmNachricht4);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe4);

		// Pruefen 1
		Vorgangsmappe aelteste = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("XXO", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 3
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("Baeh!", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Nachricht 2
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();
		
		Assert.assertNotNull(aelteste);
		Assert.assertEquals("START", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 4
		Vorgangsmappe vorgangsmappe5 = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		Assert.assertNotNull(vorgangsmappe5);
		Assert.assertEquals("STOP", vorgangsmappe5
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, vorgangsmappe5.getWeiteresVersandVorgehen());

		
		
		
		
		
		

		// Un-Passende 1
		vorgangsmappenkennung = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht = new AlarmNachricht("XXO");
		vorgangsmappe = new Vorgangsmappe(
				vorgangsmappenkennung, alarmNachricht);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe);

		// Passende 1
		vorgangsmappenkennung2 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht2 = new AlarmNachricht("START");
		vorgangsmappe2 = new Vorgangsmappe(
				vorgangsmappenkennung2, alarmNachricht2);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe2);

		// Un-Passende 1
		vorgangsmappenkennung3 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht3 = new AlarmNachricht("Baeh!");
		vorgangsmappe3 = new Vorgangsmappe(
				vorgangsmappenkennung3, alarmNachricht3);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe3);

		// Passende Bestaetigung1
		Thread.sleep(150);
		vorgangsmappenkennung4 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht4 = new AlarmNachricht("STOP");
		vorgangsmappe4 = new Vorgangsmappe(
				vorgangsmappenkennung4, alarmNachricht4);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe4);

		// Pruefen 1
		aelteste = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("XXO", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 3
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("Baeh!", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Nachricht 2
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();
		
		Assert.assertNotNull(aelteste);
		Assert.assertEquals("START", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 4
		vorgangsmappe5 = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		Assert.assertNotNull(vorgangsmappe5);
		Assert.assertEquals("STOP", vorgangsmappe5
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, vorgangsmappe5.getWeiteresVersandVorgehen());
		
	}
	
	@Test(timeout = 4000)
	public void testTimeBasedAusloesenBeiTimeout() throws Throwable {
		Regel startRegel = new Regel() {
			@Override
			public boolean pruefeNachricht(AlarmNachricht nachricht,
					AlarmNachricht vergleichsNachricht) {
				Assert.fail();
				return false;
			}
			@Override
			public boolean pruefeNachricht(AlarmNachricht nachricht) {
				return nachricht.gibNachrichtenText().equals("START");
			}
		};
		Regel stopRegel = new Regel() {
			@Override
			public boolean pruefeNachricht(AlarmNachricht nachricht,
					AlarmNachricht vergleichsNachricht) {
				return pruefeNachricht(nachricht);
			}
			@Override
			public boolean pruefeNachricht(AlarmNachricht nachricht) {
				return nachricht.gibNachrichtenText().equals("STOP");
			}
		};
		TimebasedRegelwerk timebasedRegelwerk = new TimebasedRegelwerk(Regelwerkskennung.valueOf(), startRegel, stopRegel, Millisekunden.valueOf(100), TimeoutType.SENDE_BEI_TIMEOUT);

		final AlarmEntscheidungsBuero buero = new AlarmEntscheidungsBuero(
				new DefaultExecutionService(), new NewRegelwerk[] { timebasedRegelwerk },
				new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb<Vorgangsmappe>(), 1);
		final Eingangskorb<Vorgangsmappe> alarmVorgangEingangskorb = buero
				.gibAlarmVorgangEingangskorb();
		final StandardAblagekorb<Vorgangsmappe> alarmVorgangAusgangskorb = (StandardAblagekorb<Vorgangsmappe>) buero
				.gibAlarmVorgangAusgangskorb();

		// Un-Passende 1
		Vorgangsmappenkennung vorgangsmappenkennung = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht = new AlarmNachricht("XXO");
		Vorgangsmappe vorgangsmappe = new Vorgangsmappe(
				vorgangsmappenkennung, alarmNachricht);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe);

		// Passende 1
		Vorgangsmappenkennung vorgangsmappenkennung2 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht2 = new AlarmNachricht("START");
		Vorgangsmappe vorgangsmappe2 = new Vorgangsmappe(
				vorgangsmappenkennung2, alarmNachricht2);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe2);

		// Un-Passende 1
		Vorgangsmappenkennung vorgangsmappenkennung3 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht3 = new AlarmNachricht("Baeh!");
		Vorgangsmappe vorgangsmappe3 = new Vorgangsmappe(
				vorgangsmappenkennung3, alarmNachricht3);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe3);

		// Passende Bestaetigung1
		Vorgangsmappenkennung vorgangsmappenkennung4 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmNachricht alarmNachricht4 = new AlarmNachricht("STOP");
		Vorgangsmappe vorgangsmappe4 = new Vorgangsmappe(
				vorgangsmappenkennung4, alarmNachricht4);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe4);

		// Pruefen 1
		Vorgangsmappe aelteste = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("XXO", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 3
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("Baeh!", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Nachricht 2
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();
		
		Assert.assertNotNull(aelteste);
		Assert.assertEquals("START", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 4
		Vorgangsmappe vorgangsmappe5 = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		Assert.assertNotNull(vorgangsmappe5);
		Assert.assertEquals("STOP", vorgangsmappe5
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, vorgangsmappe5.getWeiteresVersandVorgehen());

		
		
		
		
		
		

		// Un-Passende 1
		vorgangsmappenkennung = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht = new AlarmNachricht("XXO");
		vorgangsmappe = new Vorgangsmappe(
				vorgangsmappenkennung, alarmNachricht);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe);

		// Passende 1
		vorgangsmappenkennung2 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht2 = new AlarmNachricht("START");
		vorgangsmappe2 = new Vorgangsmappe(
				vorgangsmappenkennung2, alarmNachricht2);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe2);

		// Un-Passende 1
		vorgangsmappenkennung3 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht3 = new AlarmNachricht("Baeh!");
		vorgangsmappe3 = new Vorgangsmappe(
				vorgangsmappenkennung3, alarmNachricht3);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe3);

		// Passende Bestaetigung1
		Thread.sleep(150);
		vorgangsmappenkennung4 = Vorgangsmappenkennung
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht4 = new AlarmNachricht("STOP");
		vorgangsmappe4 = new Vorgangsmappe(
				vorgangsmappenkennung4, alarmNachricht4);
		alarmVorgangEingangskorb.ablegen(vorgangsmappe4);

		// Pruefen 1
		aelteste = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("XXO", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 3
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("Baeh!", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Nachricht 2
		aelteste = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();
		
		Assert.assertNotNull(aelteste);
		Assert.assertEquals("START", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 4
		vorgangsmappe5 = alarmVorgangAusgangskorb
				.entnehmeAeltestenEingang();

		Assert.assertNotNull(vorgangsmappe5);
		Assert.assertEquals("STOP", vorgangsmappe5
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, vorgangsmappe5.getWeiteresVersandVorgehen());

	}
}
