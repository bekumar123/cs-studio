package org.csstudio.nams.application.department.decision.office.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Outbox;
import org.csstudio.nams.common.decision.ObservableInbox;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.InboxObserver;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.WatchDogRegelwerk;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;

public class WatchDogFilterWorker implements FilterWorker {

	private WatchDogRegelwerk regelwerk;
	private ObservableInbox<MessageCasefile> inbox;
	private Outbox<MessageCasefile> outbox;
	private boolean isWorking;
	private Timer timer;
	private TimerTask timerTask;

	public WatchDogFilterWorker(ObservableInbox<MessageCasefile> inbox, Outbox<MessageCasefile> outbox, WatchDogRegelwerk regelwerk, Timer timer) {
		this.inbox = inbox;
		this.outbox = outbox;
		this.regelwerk = regelwerk;
		this.timer = timer;
		this.timerTask = createTimerTask();
	}
	
	@Override
	public void stopWorking() {
		isWorking = false;
		inbox.setObserver(null);
		timerTask.cancel();
	}

	@Override
	public void startWorking() {
		isWorking = true;
		
		inbox.setObserver(new InboxObserver() {
			@Override
			public void onNewDocument() {
				handleNewDocument();
			}
		});
		
		resetTimer();
	}

	@Override
	public boolean isWorking() {
		return isWorking;
	}
	
	private void handleNewDocument() {
		try {
			MessageCasefile casefile = inbox.takeDocument();
			
			// Trifft Regel zu?
			if (regelwerk.getRegel().pruefeNachricht(casefile.getAlarmNachricht())) {
				resetTimer();
			}
		} 
		catch (InterruptedException e) {
			// TODO: Log properly
			e.printStackTrace();
		}
	}

	private void resetTimer() {
		timerTask.cancel();
		timerTask = createTimerTask();
		timer.schedule(timerTask, regelwerk.getDelay().getMilliseconds());
	}

	private TimerTask createTimerTask() {
		return new TimerTask() {
			@Override
			public void run() {
				try {
					sendAlarmMessage();
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			private void sendAlarmMessage() throws UnknownHostException, InterruptedException {
				Map<MessageKeyEnum, String> map = new HashMap<MessageKeyEnum, String>();
				Map<String, String> unknownMap = new HashMap<String, String>();
				
				map.put(MessageKeyEnum.NAME, "WATCHDOG");

				AlarmMessage alarmMessage = new AlarmMessage(map, unknownMap);
				MessageCasefile casefile;
				casefile = new MessageCasefile(CasefileId.createNew(InetAddress.getLocalHost(), new Date()), alarmMessage);
				casefile.setBearbeitetMitRegelWerk(regelwerk.getRegelwerksKennung());
				casefile.setWeiteresVersandVorgehen(WeiteresVersandVorgehen.VERSENDEN);
				casefile.pruefungAbgeschlossenDurch(casefile.gibMappenkennung());
				outbox.put(casefile);
			}
		};
	}
	
	@Override
	public Regelwerk getRegelwerk() {
		return regelwerk;
	}
	
	public void setRegelwerk(WatchDogRegelwerk regelwerk) {
		this.regelwerk = regelwerk;
		resetTimer();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Inbox<Document> getInbox() {
		return (Inbox)inbox;
	}
}
