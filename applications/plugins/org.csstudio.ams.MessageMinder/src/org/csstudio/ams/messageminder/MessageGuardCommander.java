/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
 */
/*
 * $Id: MessageGuardCommander.java,v 1.26 2010/07/29 12:21:16 bknerr Exp $
 */

package org.csstudio.ams.messageminder;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.AmsConnectionFactory;
import org.csstudio.ams.dbAccess.configdb.FilterActionDAO;
import org.csstudio.ams.dbAccess.configdb.FilterActionTObject;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.ams.messageminder.preference.MessageMinderPreferenceKey;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.domain.common.statistic.Collector;
import org.csstudio.platform.utility.jms.JmsRedundantProducer;
import org.csstudio.platform.utility.jms.JmsRedundantProducer.ProducerId;
import org.csstudio.platform.utility.jms.JmsRedundantReceiver;
import org.csstudio.utility.jms.JmsTool;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IPreferencesService;

//import org.osgi.service.prefs.BackingStoreException;

/**
 * @author hrickens
 * @author $Author: bknerr $
 * @version $Revision: 1.26 $
 * @since 30.10.2007
 */
public class MessageGuardCommander extends Job {

    private final class ThreadUpdateTopicMessageMap extends Job {

        protected ThreadUpdateTopicMessageMap(final String name) {
            super(name);
        }

		@Override
		protected IStatus run(final IProgressMonitor monitor) {

		    while (isRunningUpdateTopicMessageMap()) {
                try {
                    // update ones per hour
                    Thread.sleep(3600000);
                } catch(final InterruptedException e) {
                    Log.log(this, Log.WARN, "I've been interrupted.");
                }

                Connection conDb = null;

                try {
                    conDb = AmsConnectionFactory.getApplicationDB();
                    if (conDb == null) {
                        Log.log(this, Log.WARN, "Could not init application database");
                    } else {
                        final Set<String> keySet = getTopicMessageMap().keySet();
                        Boolean newValue;

                        for (final String filterID : keySet) {
                            newValue = null;

                            final FilterActionTObject[] actionTObject = FilterActionDAO.selectByFilter(conDb,
                                    Integer.parseInt(filterID));

                            int counter = actionTObject.length > 0 ? actionTObject.length : -1;

                            // Check all filter actions
                            for (FilterActionTObject o : actionTObject) {
                                if(o.getFilterActionTypeRef() != AmsConstants.FILTERACTIONTYPE_TO_JMS) {
                                    break;
                                }
                                counter--;
                            }

                            newValue = counter == 0 ? true : false;

                            Boolean oldValue = getTopicMessageMap().get(filterID);
                            if(newValue != oldValue) {
                                getTopicMessageMap().replace(filterID, newValue);
                            }
                        }
                    }
                } catch(final SQLException e) {
                    Log.log(this, Log.WARN, "[*** SQLException ***]: " + e.getMessage());
                } finally {
                    if(conDb!=null) {
                        try {
                            conDb.close();
                        } catch(final SQLException e) {
                            Log.log(this, Log.WARN, "[*** SQLException ***]: Closing connection: " + e.getMessage());
                        }
                    }
                }
            }

            return Status.CANCEL_STATUS;
        }
    }

    /**
     *
     */
    private static final String AMS_COMMAND_KEY_NAME = "COMMAND";

    private final static String MSGVALUE_TCMD_RELOAD = "AMS_RELOAD_CFG";

    private final static String MSGVALUE_TCMD_RELOAD_CFG_START = MessageGuardCommander.MSGVALUE_TCMD_RELOAD
            + "_START";
    private final static String MSGVALUE_TCMD_RELOAD_CFG_END = MessageGuardCommander.MSGVALUE_TCMD_RELOAD
            + "_END";

    /**
     * The (AMS) JMS Redundant Receiver.
     */
    private JmsRedundantReceiver _amsReceiver;
    /**
     * The (AMS) JMS Redundant Producer.
     */
    private JmsRedundantProducer _amsProducer;
    /**
     * A Map with the the Messages time stamp that no older then _toOldTime.
     */
    private final HashMap<MessageKey, MessageTimeList> _messageMap;
    /**
     * The id of the Producer.
     */
    private ProducerId _producerID;
    /**
     * The time stamp white the time who the massage map was last clean up.
     */
    private ITimestamp _lastClean;
    /**
     * The time in second that wait to next clean.
     */
    private final long _time2Clean;
    /**
     * The time in second there are old the message to old an new message was can send.
     */
    private final long _toOldTime;
    /**
     * A list whit the fields they are use as key.
     */
    private final String[] _keyWords;
    private final Collector _messageControlTimeCollector;
    private final Collector _messageDeleteTimeCollector;

    private boolean useCacheDb;

    private boolean _run;

    private final ConcurrentHashMap<String, Boolean> _topicMessageMap;

    private boolean _runUpdateTopicMessageMap;

    /**
     * @param name
     *            The name of this Job.
     */
    public MessageGuardCommander(String name, boolean useCacheDb) {
        super(name);
        _run = true;
        _runUpdateTopicMessageMap = true;
        _topicMessageMap = new ConcurrentHashMap<String, Boolean>();
        IPreferencesService pref = Platform.getPreferencesService();
        connect();
        _time2Clean = pref.getLong(MessageMinderActivator.PLUGIN_ID,
                                   MessageMinderPreferenceKey.P_LONG_TIME2CLEAN,
                                   20,
                                   null); // sec
        _toOldTime = pref.getLong(MessageMinderActivator.PLUGIN_ID,
                                  MessageMinderPreferenceKey.P_LONG_TO_OLD_TIME,
                                  60,
                                  null);
        String temp = pref.getString(MessageMinderActivator.PLUGIN_ID,
                                     MessageMinderPreferenceKey.P_STRING_KEY_WORDS,
                                     "HOST,FACILITY,AMS-FILTERID",
                                     null);
        _keyWords = temp.split(",");
        _lastClean = TimestampFactory.now();
        _messageMap = new HashMap<MessageKey, MessageTimeList>();

        /*
         * initialize statistic
         */
        // delete
        _messageDeleteTimeCollector = new Collector();
        _messageDeleteTimeCollector.setApplication(name);
        _messageDeleteTimeCollector.setDescriptor("Time for a clean up run [ns]");
        _messageDeleteTimeCollector.setContinuousPrint(true);

        _messageControlTimeCollector = new Collector();
        _messageControlTimeCollector.setApplication(name);
        _messageControlTimeCollector.setDescriptor("Time to Control a Message [ns]");
        _messageControlTimeCollector.setContinuousPrint(true);

        this.useCacheDb = useCacheDb;
    }

    private void connect() {

        final IPreferencesService storeAct = Platform.getPreferencesService();
        final boolean durable = storeAct.getBoolean(AmsActivator.PLUGIN_ID,
                                                   AmsPreferenceKey.P_JMS_AMS_CREATE_DURABLE,
                                                   false, null);

        /**
         * Nur fuer debug zwecke wird die P_JMS_AMS_PROVIDER_URL_2 geaendert. Der Code kann
         * spaeter wieder entfernt werden. TODO: delete debug code.
         *
         * storeAct.put(org.csstudio.ams.internal.SampleService. P_JMS_AMS_PROVIDER_URL_1,
         * "failover:(tcp://kryksrvjmsa.desy.de:50000)"); storeAct.put(org.csstudio
         * .ams.internal.SampleService.P_JMS_AMS_PROVIDER_URL_2,
         * "failover:(tcp://kryksrvjmsa.desy.de:50001)"); storeAct.put(org.csstudio
         * .ams.internal.SampleService.P_JMS_AMS_SENDER_PROVIDER_URL,
         * "failover:(tcp://kryksrvjmsa.desy.de:50000,tcp://kryksrvjmsa.desy.de:50001)" );
         *
         * try { storeAct.flush(); } catch (BackingStoreException e) { // TODO Auto-generated catch
         * block e.printStackTrace(); }
         *
         * /** bis hier
         */

        // --- JMS Receiver Connect---
        // _amsReceiver = new
        // JmsRedundantReceiver("AmsMessageMinderWorkReceiverInternal",
        // storeAct.get(org.csstudio.ams.internal.SampleService.P_JMS_AMS_PROVIDER_URL_1,""),
        // storeAct.get(SampleService.P_JMS_AMS_PROVIDER_URL_2,""));
        String url1 =  storeAct.getString(AmsActivator.PLUGIN_ID,
                                          AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1,
                                          "tcp://localhost:61616",
                                          null);
        String url2 = storeAct.getString(AmsActivator.PLUGIN_ID,
                                         AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2,
                                         "tcp://localhost:64616",
                                         null);
        _amsReceiver = new JmsRedundantReceiver(JmsTool.createUniqueClientId("AmsMessageMinderWorkReceiverInternal"),
                                                url1,
                                                url2);
        if (!_amsReceiver.isConnected()) {
            Log.log(this, Log.FATAL, "Could not create amsReceiver");
        }

        final boolean result = _amsReceiver.createRedundantSubscriber("amsSubscriberMessageMinder",
                storeAct.getString(AmsActivator.PLUGIN_ID, AmsPreferenceKey.P_JMS_AMS_TOPIC_MESSAGEMINDER, "NONE", null), storeAct
                        .getString(AmsActivator.PLUGIN_ID, AmsPreferenceKey.P_JMS_AMS_TSUB_MESSAGEMINDER, "NONE", null), durable);

        if (!result) {
            Log.log(this, Log.FATAL, "Could not create amsSubscriberMessageMinder");
        }

        // --- JMS Producer Connect ---
        final String[] urls = new String[] { storeAct
                .getString(AmsActivator.PLUGIN_ID, AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL, "NONE", null) };
        _amsProducer = new JmsRedundantProducer("AmsMessageMinderWorkProducerInternal", urls);
        // TODO: remove debug settings
        _producerID = _amsProducer.createProducer(storeAct.getString(AmsActivator.PLUGIN_ID,
                                                                     AmsPreferenceKey.P_JMS_AMS_TOPIC_DISTRIBUTOR,
                                                                     "NONE",
                                                                     null));

        // --- Derby DB Connect ---
        // initApplicationDb();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        final ThreadUpdateTopicMessageMap updateTopicMessageMap
                                   = new ThreadUpdateTopicMessageMap("UpdateTopicMessageMap");
        updateTopicMessageMap.schedule();
//        updateTopicMessageMap.run(monitor);
		patrol();
        return Status.CANCEL_STATUS;
    }

    /**
     * The main method, it run permanent. First step check for new message. Second step check if
     * time to clean. Third step sleep.
     */
    private void patrol() {
        Message message = null;
        ITimestamp now;
        Log.log(this, Log.INFO, "StartTime: " + TimestampFactory.now());
        // int counter =0;
        while (_run) {
            now = TimestampFactory.now();

            while (null != (message = _amsReceiver.receive("amsSubscriberMessageMinder"))) {
                // has a bug
                // with
                // acknowledging
                // in openjms 3
                // ADDED BY Markus Moeller, 2008-08-14
                this.acknowledge(message);
                final ITimestamp before = TimestampFactory.now();
                checkMsg(message, now);
                final ITimestamp after = TimestampFactory.now();
                final double nsec = after.nanoseconds() - before.nanoseconds();
                _messageControlTimeCollector.setInfo("MessageMinder in Nanosecond");
                _messageControlTimeCollector.setValue(nsec);
            }
            if (now.seconds() - _lastClean.seconds() > _time2Clean) {
                final ITimestamp before = TimestampFactory.now();
                cleanUp(now);
                final ITimestamp after = TimestampFactory.now();
                final double nsec = after.nanoseconds() - before.nanoseconds();
                _messageDeleteTimeCollector.setInfo("Clean Up in Nanosecond");
                _messageDeleteTimeCollector.setValue(nsec);
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Log.log(this, Log.WARN, "I've been interrupted.");
            }
        }
    }

    /**
     * @param message
     *            was check if is in nearly time sent and how many times.
     * @param now
     *            a time stamp with the actual time.
     */
    private void checkMsg(final Message message, final ITimestamp now) {
        if (message instanceof MapMessage) {
            final MapMessage mapMessage = (MapMessage) message;
            try {
                Log.log(this, Log.INFO, "Name: " + mapMessage.getString("NAME"));
                final String command = mapMessage.getString(AMS_COMMAND_KEY_NAME);
                if (command != null
                        && (command.equals(MSGVALUE_TCMD_RELOAD_CFG_START) || command
                                .equals(MSGVALUE_TCMD_RELOAD_CFG_END))) {
                    send(message);
                    return;
                }
                // Is Action id related to topic.
                final String filterID = mapMessage.getString(AmsConstants.MSGPROP_FILTERID);
                if (hasOnlyTopicAction(filterID)) {
                    send(message);
                    return;
                }
                final String[] keys = new String[_keyWords.length];
                for (int i = 0; i < keys.length; i++) {
                    keys[i] = mapMessage.getString(_keyWords[i]);
                    if (keys[i] == null) {
                        keys[i] = "";
                    }
                }
                final MessageKey key = new MessageKey(keys);
                MessageTimeList value = _messageMap.get(key);
                if (value == null) {
                    value = new MessageTimeList();
                    _messageMap.put(key, value);
                }
                if (value.add(now)) {
                    send(message);
                    return;
                }
            } catch (final JMSException e) {
                Log.log(this, Log.ERROR, "[*** JMSException ***]: " + e.getMessage());
            }
        }
    }

    private boolean hasOnlyTopicAction(String filterID) {
        if (filterID == null) {
            return false;
        }
        boolean hasOnlyTopicAction = false;
        if (!filterID.trim().isEmpty()) {
            Connection conDb = null;
            try {
                Boolean topicMsg = _topicMessageMap.get(filterID);
                if (topicMsg == null) {
                    if (useCacheDb) {
                        conDb = AmsConnectionFactory.getMemoryCacheDB();
                    } else {
                        conDb = AmsConnectionFactory.getApplicationDB();
                    }
                    if (conDb == null) {
                        Log.log(this, Log.FATAL, "Could not init application database");
                        return false;
                    }

                    final FilterActionTObject[] actionTObject = FilterActionDAO.selectByFilter(conDb, Integer
                            .parseInt(filterID));

                    int counter = actionTObject.length > 0 ? actionTObject.length : -1;

                    // Check all filter actions
                    for (FilterActionTObject o : actionTObject) {
                        if(o.getFilterActionTypeRef() != AmsConstants.FILTERACTIONTYPE_TO_JMS) {
                            break;
                        }
                        counter--;
                    }

                    topicMsg = counter == 0 ? true : false;
                    _topicMessageMap.putIfAbsent(filterID, topicMsg);
                }

                hasOnlyTopicAction = topicMsg.booleanValue();
            } catch(final Exception e) {
                Log.log(this, Log.FATAL, "Could not init application database");
            } finally {
                if (conDb != null) {
                    try {
                        conDb.close();
                    } catch(final SQLException e) {
                        Log.log(this, Log.WARN, e);
                    }
                }
            }
        }
        return hasOnlyTopicAction;
    }

    @SuppressWarnings("unused")
    private boolean isTopicAction(final String filterID) {

        if (filterID == null) {
            return false;
        }

        boolean isTopicAction = false;
        if (!filterID.trim().isEmpty()) {
            Connection conDb = null;
            try {
                Boolean topicMsg = _topicMessageMap.get(filterID);
                if (topicMsg == null) {
                    if (useCacheDb) {
                        conDb = AmsConnectionFactory.getMemoryCacheDB();
                    } else {
                        conDb = AmsConnectionFactory.getApplicationDB();
                    }
                    if (conDb == null) {
                        Log.log(this, Log.FATAL, "Could not init application database");
                        return false;
                    }

                    final FilterActionTObject[] actionTObject = FilterActionDAO.selectByFilter(conDb, Integer
                            .parseInt(filterID));

                    // Check all filter actions
                    for(final FilterActionTObject o : actionTObject) {
                        if(o.getFilterActionTypeRef() == AmsConstants.FILTERACTIONTYPE_TO_JMS) {
                            topicMsg = true;
                            break;
                        }
                    }

                    topicMsg = topicMsg == null ? false : topicMsg;

                    _topicMessageMap.putIfAbsent(filterID, topicMsg);
                }

                isTopicAction = topicMsg.booleanValue();
            } catch(final Exception e) {
                Log.log(this, Log.FATAL, "Could not init application database");
            } finally {
                if (conDb != null) {
                    try {
                        conDb.close();
                    } catch(final SQLException e) {
                        Log.log(this, Log.WARN, e);
                    }
                }
            }
        }

        return isTopicAction;
    }

    /**
     * Delete all time stamp that older as the _toOldTime. Are all time stamp from one list older
     * delete the list from the map.
     *
     * @param now
     *            the actual time
     */
    private void cleanUp(final ITimestamp now) {
        for (final Iterator<MessageKey> ite = _messageMap.keySet().iterator(); ite.hasNext();) {
            final MessageKey key = ite.next();
            final MessageTimeList value = _messageMap.get(key);
            if (now.seconds() - value.getLastDate().seconds() > _toOldTime) {
                sendCleanUpMessage(key, value.getLastDate(), value.getUnsentsgCount());
                value.resetUnsentMsgCount();
                value.clear();
                ite.remove();
            } else {
                for (int i = 0; i < value.size(); i++) {
                    final ITimestamp timestamp = value.get(i);
                    if (now.seconds() - timestamp.seconds() > _toOldTime) {
                        value.remove(i);
                    }
                }
            }
        }
        _lastClean = now;
    }

    /**
     * @param key
     *            of the massage.
     * @param lastDate
     *            the last Date when the massage are <b>not</b> send.
     * @param number
     *            the number oft don't send massages.
     */
    private void sendCleanUpMessage(final MessageKey key, final ITimestamp lastDate,
            final int number) {
        Log.log(this, Log.INFO, key.toString() + "\tlast unsend msg: " + lastDate.toString() + "\t and "
                + number + " unsent msg.");
        // TODO write the sendCleanUpMessage.
        // Soll eine Nachricht versenden die enthaelt welche und wieviele
        // nachrchten zurueck gehalten wurden.
    }

    /**
     * @param sendMessage
     *            Message to send.
     */
    private void send(final Message sendMessage) {
        if (!_amsProducer.isClosed()) {
            _amsProducer.send(_producerID, sendMessage);
        }
    }

    /**
     * Acknowledges the current message.
     *
     * @param msg
     *            Message object that should be acknowledged
     *
     * @return
     */
    private boolean acknowledge(final Message msg) {
        boolean success = false;
        try {
            msg.acknowledge();
            success = true;
        } catch (final Exception e) {
            Log.log(this, Log.FATAL, "Could not acknowledge JMS message: " + e.getMessage());
        }
        return success;
    }

    public synchronized void setRun(final boolean run) {
        _run = run;
    }

    public synchronized boolean isRunningUpdateTopicMessageMap() {
        return _runUpdateTopicMessageMap;
    }

    public synchronized ConcurrentHashMap<String, Boolean> getTopicMessageMap() {
        return _topicMessageMap;
    }

    @Override
    protected void canceling() {
        super.canceling();
        _runUpdateTopicMessageMap = false;
    }
}
