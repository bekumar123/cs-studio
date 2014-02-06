package org.csstudio.nams.application.department.decision.office.decision;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.InboxObserver;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.decision.ObservableInbox;
import org.csstudio.nams.common.decision.Outbox;
import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.common.material.regelwerk.Filter;
import org.csstudio.nams.common.material.regelwerk.WatchDogFilter;

public class WatchDogFilterWorker implements FilterWorker {

	private WatchDogFilter regelwerk;
	private ObservableInbox<MessageCasefile> inbox;
	private Outbox<MessageCasefile> outbox;
	private boolean isWorking;
	private Timer timer;
	private TimerTask timerTask;

	public WatchDogFilterWorker(ObservableInbox<MessageCasefile> inbox, Outbox<MessageCasefile> outbox, WatchDogFilter regelwerk, Timer timer) {
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
			if (regelwerk.getCondition().pruefeNachricht(casefile.getAlarmMessage())) {
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
		timer.schedule(timerTask, regelwerk.getTimeout().getMilliseconds());
	}

	private TimerTask createTimerTask() {
		return new TimerTask() {
			@Override
			public void run() {
				try {
					sendAlarmMessage();
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			private void sendAlarmMessage() throws InterruptedException {
				Map<MessageKeyEnum, String> map = new HashMap<MessageKeyEnum, String>();
				Map<String, String> unknownMap = new HashMap<String, String>();
				
				map.put(MessageKeyEnum.NAME, "WATCHDOG");

				AlarmMessage alarmMessage = new AlarmMessage(map, unknownMap);
				MessageCasefile casefile;
				casefile = new MessageCasefile(CasefileId.createNew(), alarmMessage);
				casefile.setHandledWithFilter(regelwerk.getFilterId());
				casefile.pruefungAbgeschlossenDurch(casefile.getCasefileId());
				outbox.put(casefile);
			}
		};
	}
	
	@Override
	public Filter getFilter() {
		return regelwerk;
	}
	
	public void setRegelwerk(WatchDogFilter regelwerk) {
		this.regelwerk = regelwerk;
		resetTimer();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Inbox<Document> getInbox() {
		return (Inbox)inbox;
	}
}
