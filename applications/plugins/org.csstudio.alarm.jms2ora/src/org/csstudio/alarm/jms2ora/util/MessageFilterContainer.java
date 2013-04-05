
/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.alarm.jms2ora.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.csstudio.alarm.jms2ora.service.ArchiveMessage;

/**
 * @author Markus Moeller
 *
 */
public class MessageFilterContainer {

    /** Hash table containing the stored messages(key as String) and the id's(value as Long) */
    private final Hashtable<String, Long> messages;

    /** Hash table containing the timestamp(value as Long) of the message and the ids(key as Long) */
    private final Hashtable<Long, Long> messageTime;

    /** Hash table containing the number of received messages(value as Integer) and the ids(key as Long) */
    private final Hashtable<Long, Integer> messageCount;

    /** Vector object containing used id's*/
    private final Vector<Long> freeIds;

    /** Number of messages that initiates a collected message */
    private final int sendBound;

    /** Number of messages that initiates a collected message */
    private final int maxSentMessages;

    /** */
    private long nextId;

    /**
     * Standard constructor
     */
    public MessageFilterContainer(final int bound, final int maxSent) {
        messages = new Hashtable<String, Long>();
        messageTime = new Hashtable<Long, Long>();
        messageCount = new Hashtable<Long, Integer>();
        freeIds = new Vector<Long>();
        nextId = 1;
        this.sendBound = bound;
        this.maxSentMessages = maxSent;
    }

    /**
     * Adds the message content to the list of received messages.
     *
     * @param mc MessageContent object that should be stored in the message container of the filter
     * @return True if the message should be blocked, false otherwise
     */
    public boolean addMessageContent(final ArchiveMessage mc) {
        String data = null;
        boolean blockIt = false;
        long idValue = 0;
        int countValue = 0;

        // Get the string containing the content without EVENTTIME
        data = mc.toStringWithoutEventtime();
        if(messages.containsKey(data)) {
            // This kind of message was stored before.
            // Get the id.
            Long id = messages.get(data);
            if (id != null) {
                idValue = id.longValue();

                // Refresh the message time
                messageTime.put(idValue, System.currentTimeMillis());

                Integer count = messageCount.get(idValue);
                if (count != null) {
                    countValue = count.intValue();
                    if(countValue >= sendBound) {
                        // Do not block the message because now we have a bundle of messages.
                        // For 100 received messages that are identical, send only one message.
                        blockIt = false;

                        countValue = 0;
                    } else if(countValue <= maxSentMessages) {
                        // The message should not be blocked.
                        blockIt = false;
                    } else {
                        // The message should be blocked.
                        blockIt = true;
                    }

                    // Increment the counter for this message
                    messageCount.put(idValue, ++countValue);
                }
            }
        } else {
            if(!freeIds.isEmpty()) {
                idValue = freeIds.lastElement().longValue();
                freeIds.remove(freeIds.lastElement());
            } else {
                idValue = nextId++;
            }

            // A new message is put into the hash table
            messages.put(data, idValue);
            messageTime.put(idValue, System.currentTimeMillis());
            messageCount.put(idValue, 1);

            // Do not block the message
            blockIt = false;
        }

        return blockIt;
    }

    public final boolean containsMessageContent(final ArchiveMessage mc) {
        String data = null;

        data = mc.toStringWithoutEventtime();

        return messages.containsKey(data);
    }

    public synchronized int removeInvalidContent(final long timePeriod) {
        Enumeration<Long> tableId = null;
        long ct = 0;
        long id = 0;
        int count = 0;
        ct = System.currentTimeMillis();
        tableId = messageTime.keys();
        while (tableId.hasMoreElements()) {
            id = tableId.nextElement().longValue();
            if (ct - messageTime.get(id).longValue() > timePeriod) {
                messageCount.remove(id);
                messageTime.remove(id);
                String msg = getMessageById(id);
                if (msg != null) {
                    messages.remove(msg);
                }
                freeIds.add(id);
                count++;
            }
        }
        return count;
    }

    public final synchronized String getMessageById(final long value) {
        String key = null;
        Enumeration<String> messageKey = messages.keys();
        while (messageKey.hasMoreElements()) {
            key = messageKey.nextElement();
            if (messages.get(key).longValue() == value) {
                break;
            }
            key = null;
        }
        return key;
    }

    public int size() {
        return messages.size();
    }

    public long getNextId() {
        return nextId;
    }
}
