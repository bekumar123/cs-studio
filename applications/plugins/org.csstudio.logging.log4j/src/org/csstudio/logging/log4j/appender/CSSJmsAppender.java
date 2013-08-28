
/*
 * Copyright 1999-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.csstudio.logging.log4j.appender;

import java.util.Calendar;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.csstudio.logging.log4j.jms.JMSLogThread;
import org.csstudio.logging.log4j.jms.JmsLogMessage;

/** Log4j appender that publishes events to a JMS Topic as described
 *  in <code>JMSLogMessage</code>.
 *
 *  The configuration of the appender is somewhat convoluted:
 *  Eclipse Preferences are read on startup, or later
 *  modified from preference pages.
 *  CentralLogger.configure() is invoked on startup or from the CSS
 *  JMS preference page onOK(), which in in turn creates properties
 *  with the following names for the Log4j PropertyConfigurator,
 *  which then uses Java Bean get/set calls to configure this class:
 *  <pre>
 *    log4j.appender.css_jms.Threshold
 *    log4j.appender.css_jms.layout
 *    log4j.appender.css_jms.layout.ConversionPattern
 *    log4j.appender.css_jms.providerURL
 *    log4j.appender.css_jms.topicBindingName
 *    log4j.appender.css_jms.userName
 *    log4j.appender.css_jms.password
 *  </pre>
 *
 *  @author Ceki G&uuml;lc&uuml;: Original version
 *  @author Markus Moeller: Changes for CSS
 *  @author Kay Kasemir: Using JMSLogThread
 *
 *  @see PlatformPreferencesInitializer for defaults
 *  @see JmsLogMessage for message format
 */
public class CSSJmsAppender extends AppenderSkeleton {

	/*
		log4j.appender.JMS=test.log4j.CSSJmsAppender
		log4j.appender.JMS.Threshold=DEBUG
		log4j.appender.JMS.topicConnectionFactoryBindingName=ConnectionFactory
		log4j.appender.JMS.initialContextFactoryName=org.apache.activemq.jndi.ActiveMQInitialContextFactory
		log4j.appender.JMS.providerURL=failover\:(localhost\:64616,tcp\://localhost\:62616)?maxReconnectDelay\=5000
		log4j.appender.JMS.topicBindingName=LOG
		log4j.appender.JMS.userName=
		log4j.appender.JMS.password=
		log4j.appender.JMS.logUserName=
		log4j.appender.JMS.logHostName=
		log4j.appender.JMS.logApplicationId=
		log4j.appender.JMS.layout=org.apache.log4j.PatternLayout
		log4j.appender.JMS.layout.ConversionPattern=%m
	 */

	/** JMS server URL */
    private String url;

    private String initialContextFactoryName;

    /** JMS queue topic */
    private String topic;

    /** Unused user name */
    private String jmsUserName;

    /** Unused password */
    private String jmsPassword;

    private String logHostName;

    private String logUserName;

    private String logApplicationId;

    /** */
    private String tcfBindingName;

    /** Thread that performs the actual logging.
     *  When parameters change, a new/different thread will be created.
     *  <p>
     *  NOTE: Synchronize on <code>this</code> when accessing!
     */
    private JMSLogThread logThread = null;

    public CSSJmsAppender() {
    	super();
    }
    
    /** @return JMS server URL */
    public String getProviderURL() {
        return url;
    }

    /** @param url JMS server URL */
    public void setProviderURL(final String url) {
        this.url = url.trim();
    }

    public void setInitialContextFactoryName(String contextName) {
    	this.initialContextFactoryName = contextName;
    }

    public String getInitialContextFactoryName() {
    	return this.initialContextFactoryName;
    }

    /**
     * The <b>TopicConnectionFactoryBindingName</b> option takes a
     * string value. Its value will be used to lookup the appropriate
     * <code>TopicConnectionFactory</code> from the JNDI context.
     */
    public void setTopicConnectionFactoryBindingName(String tcfBindingName) {
        this.tcfBindingName = tcfBindingName;
    }

    /**
     * Returns the value of the <b>TopicConnectionFactoryBindingName</b> option.
     */
    public String getTopicConnectionFactoryBindingName() {
        return tcfBindingName;
    }

    /** @returns JMS topic used for logging */
    public String getTopicBindingName() {
        return topic;
    }

    /** @param topic JMS topic used for logging */
    public void setTopicBindingName(final String topic) {
        this.topic = topic.trim();
    }

    /** @returns JMS user name */
    public String getJmsUserName() {
        return jmsUserName;
    }

    /** @param user JMS user name */
    public void setJmsUserName(final String user) {
        this.jmsUserName = user.trim();
    }

    /** @returns JMS password */
    public String getJmsPassword() {
        return jmsPassword;
    }

    /** @param password JMS user name */
    public void setJmsPassword(final String password) {
        this.jmsPassword = password.trim();
    }

    public void setLogHostName(String hostName) {
    	this.logHostName = hostName;
    }

    public String getLogHostName() {
    	return this.logHostName;
    }

    public void setLogUserName(String userName) {
    	this.logUserName = userName;
    }

    public String getLogUserName() {
    	return this.logUserName;
    }

    public void setLogApplicationId(String appId) {
    	this.logApplicationId = appId;
    }

    public String getLogApplicationId() {
    	return this.logApplicationId;
    }

    /** Options are activated and become effective only after calling
     *  this method.
     */
    @SuppressWarnings("nls")
    @Override
    public void activateOptions() {

    	if (url == null) {
            LogLog.error(name + " no URL");
            return;
        }
        if (topic == null) {
            LogLog.error(name + " no topic");
            return;
        }
        synchronized (this) {
            if (logThread != null)
            {   // Ask to cancel, but can't wait for that to actually happen
                logThread.cancel();
                logThread = null;
            }
            logThread = new JMSLogThread(url, topic, jmsUserName, jmsPassword);
            logThread.start();
        }
        LogLog.debug(name + " activated for '" + topic
                + "' on " + url);
    }

    /**
     * Close this JMSAppender. Closing releases all resources used by the
     * appender. A closed appender cannot be re-opened.
     */
    @Override
	@SuppressWarnings("nls")
    public void close() {
        closed = true;
        synchronized (this) {
            if (logThread != null) {
                logThread.cancel();
                logThread = null;
            }
        }
        LogLog.debug(name + " closed.");
    }

    /** This method called by {@link AppenderSkeleton#doAppend} method to
     *  do most of the real appending work.
     */
    @Override
    public void append(final LoggingEvent event) {

    	final String text = layout.format(event).trim();
        final String severity = event.getLevel().toString();
        final Calendar eventTime = Calendar.getInstance();
        eventTime.setTimeInMillis(event.timeStamp);

        final Calendar createTime = Calendar.getInstance();

        String clazz = null;
        String method = null;
        String file = null;
        final LocationInfo location = event.getLocationInformation();
        if(location != null) {
            clazz = location.getClassName();
            method = location.getMethodName();
            file = location.getFileName();
        }

        String app = getLogApplicationId();
        String host = getLogHostName();
        String user = getLogUserName();

        final JmsLogMessage log_msg = new JmsLogMessage(text, severity,
                createTime, eventTime,
                clazz, method, file, app, host, user);
        synchronized (this) {
            if (logThread == null) {
                errorHandler.error(name + " not configured."); //$NON-NLS-1$
            } else {
                logThread.addMessage(log_msg);
            }
        }
    }

    /** The JMSAppender for CSS sends requires a layout. */
    @Override
	public boolean requiresLayout() {
        return true;
    }

    /** String representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("Log4j appender '%s': '%s' @ '%s'\n", name, topic, url);
    }
}
