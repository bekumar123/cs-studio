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
import org.csstudio.nams.common.material.regelwerk.WatchDogRegelwerk;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;

public class WatchDogSachbearbeiter implements Arbeitsfaehig {

	private BeobachtbarerEingangskorb<Vorgangsmappe> eingangskorb;
	private Ausgangskorb<Vorgangsmappe> ausgangskorb;
	private WatchDogRegelwerk regelwerk;
	private boolean istAmArbeiten;
	private Timer timer;
	private TimerTask timerTask;

	public WatchDogSachbearbeiter(BeobachtbarerEingangskorb<Vorgangsmappe> eingangskorb, Ausgangskorb<Vorgangsmappe> ausgangskorb, WatchDogRegelwerk regelwerk, Timer timer) {
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
		timer.schedule(timerTask, regelwerk.getDelay().alsLongVonMillisekunden());
	}

	@Override
	public boolean istAmArbeiten() {
		return istAmArbeiten;
	}
	
	private void handleNeuerEingang() {
		try {
			Vorgangsmappe vorgangsMappe = eingangskorb.entnehmeAeltestenEingang();
			vorgangsMappe.setBearbeitetMitRegelWerk(regelwerk.getRegelwerksKennung());
			boolean trifftRegelZu = regelwerk.getRegel().pruefeNachricht(vorgangsMappe.getAlarmNachricht());

			if (trifftRegelZu) {
				timerTask.cancel();
				timerTask = createTimerTask();
				timer.schedule(timerTask, regelwerk.getDelay().alsLongVonMillisekunden());
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
					Map<MessageKeyEnum, String> map = new HashMap<MessageKeyEnum, String>();
					Map<String, String> unknownMap = new HashMap<String, String>();
					
					map.put(MessageKeyEnum.NAME, "WATCHDOG");

					AlarmNachricht nachricht = new AlarmNachricht(map, unknownMap);
					Vorgangsmappe vorgangsMappe;
					vorgangsMappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(InetAddress.getLocalHost(), new Date()), nachricht);
					vorgangsMappe.setBearbeitetMitRegelWerk(regelwerk.getRegelwerksKennung());
					vorgangsMappe.setWeiteresVersandVorgehen(WeiteresVersandVorgehen.VERSENDEN);
					vorgangsMappe.pruefungAbgeschlossenDurch(vorgangsMappe.gibMappenkennung());
					ausgangskorb.ablegen(vorgangsMappe);
					
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
