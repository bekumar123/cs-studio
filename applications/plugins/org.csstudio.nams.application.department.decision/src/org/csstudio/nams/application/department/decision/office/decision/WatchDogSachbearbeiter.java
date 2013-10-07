package org.csstudio.nams.application.department.decision.office.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.nams.common.decision.Arbeitsfaehig;
import org.csstudio.nams.common.decision.Ausgangskorb;
import org.csstudio.nams.common.decision.BeobachtbarerEingangskorb;
import org.csstudio.nams.common.decision.EingangskorbBeobachter;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung;
import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.common.material.regelwerk.yaams.WatchDogRegelwerk;

public class WatchDogSachbearbeiter implements Arbeitsfaehig {

	private final String name;
	private BeobachtbarerEingangskorb<Vorgangsmappe> eingangskorb;
	private Ausgangskorb<Vorgangsmappe> ausgangskorb;
	private WatchDogRegelwerk regelwerk;
	private boolean istAmArbeiten;
	private Timer timer;
	private TimerTask timerTask;

	public WatchDogSachbearbeiter(String name, BeobachtbarerEingangskorb<Vorgangsmappe> eingangskorb, Ausgangskorb<Vorgangsmappe> ausgangskorb, WatchDogRegelwerk regelwerk, Timer timer) {
		this.name = name;
		this.eingangskorb = eingangskorb;
		this.ausgangskorb = ausgangskorb;
		this.regelwerk = regelwerk;
		this.timer = timer;
		this.timerTask = createTimerTask();
	}
	
	@Override
	public void beendeArbeit() {
		istAmArbeiten = false;
		eingangskorb.setBeobachter(null);
		timerTask.cancel();
	}

	@Override
	public void beginneArbeit() {
		istAmArbeiten = true;
		
		eingangskorb.setBeobachter(new EingangskorbBeobachter() {
			@Override
			public void neuerEingang() {
				handleNeuerEingang();
			}

		});
		
		timerTask = createTimerTask();
		timer.schedule(timerTask, regelwerk.getDelay());
	}

	@Override
	public boolean istAmArbeiten() {
		return istAmArbeiten;
	}
	
	public String getName() {
		return name;
	}

	private void handleNeuerEingang() {
		try {
			Vorgangsmappe vorgangsMappe = eingangskorb.entnehmeAeltestenEingang();
			vorgangsMappe.setBearbeitetMitRegelWerk(regelwerk.getRegelwerksKennung());
			boolean trifftRegelZu = regelwerk.getRegel().pruefeNachricht(vorgangsMappe.getAlarmNachricht());

			if (trifftRegelZu) {
				timerTask.cancel();
				timerTask = createTimerTask();
				timer.schedule(timerTask, regelwerk.getDelay());
			}
			
			vorgangsMappe.setWeiteresVersandVorgehen(WeiteresVersandVorgehen.NICHT_VERSENDEN);
			vorgangsMappe.pruefungAbgeschlossenDurch(vorgangsMappe.gibMappenkennung());
			ausgangskorb.ablegen(vorgangsMappe);
		} 
		catch (InterruptedException e) {
			// TODO: Log properly
			e.printStackTrace();
		}
	}

	private TimerTask createTimerTask() {
		return new TimerTask() {
			@Override
			public void run() {
				try {
					System.out.println("WatchDog Timer Task");

					Map<MessageKeyEnum, String> map = new HashMap<MessageKeyEnum, String>();
					Map<String, String> unknownMap = new HashMap<String, String>();
					
					// TODO maps befüllen
					map.put(MessageKeyEnum.NAME, "WATCHDOG");

					AlarmNachricht nachricht = new AlarmNachricht(map, unknownMap);
					Vorgangsmappe vorgangsMappe;
					vorgangsMappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(InetAddress.getLocalHost(), new Date()), nachricht);
					vorgangsMappe.setBearbeitetMitRegelWerk(regelwerk.getRegelwerksKennung());
					vorgangsMappe.setWeiteresVersandVorgehen(WeiteresVersandVorgehen.VERSENDEN);
					vorgangsMappe.pruefungAbgeschlossenDurch(vorgangsMappe.gibMappenkennung());
					ausgangskorb.ablegen(vorgangsMappe);
					
					// TODO neuen Timer starten??
					// (gs) mir ist nicht ganz klar wie sich der watch dog 
					// verhalten soll nachdem er einen alarm verschickt hat
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
	}
}
